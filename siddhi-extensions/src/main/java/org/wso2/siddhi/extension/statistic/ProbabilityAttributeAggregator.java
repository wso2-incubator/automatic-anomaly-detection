/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.siddhi.extension.statistic;

import org.apache.log4j.Logger;
import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.exception.OperationNotSupportedException;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.query.selector.attribute.aggregator.AttributeAggregator;
import org.wso2.siddhi.query.api.definition.Attribute;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class ProbabilityAttributeAggregator extends AttributeAggregator {

    private static final Logger log = Logger.getLogger(ProbabilityAttributeAggregator.class);
    private ProbabilityAttributeAggregator probOutputAttributeAggregator;

    /**
     * The initialization method for FunctionExecutor
     *
     * @param attributeExpressionExecutors are the executors of each attributes in the function
     * @param executionPlanContext         Execution plan runtime context
     */
    @Override
    protected void init(ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {

        if (attributeExpressionExecutors.length != 4) {
            throw new OperationNotSupportedException("Probability aggregator has to have exactly 4 parameter, currently " +
                    attributeExpressionExecutors.length + " parameters provided");
        }

        Attribute.Type vType = attributeExpressionExecutors[3].getReturnType();
        switch (vType) {
            case FLOAT:
                probOutputAttributeAggregator = new ProbabilityAttributeAggregatorFloat();
                break;
            case INT:
                probOutputAttributeAggregator = new ProbabilityAttributeAggregatorInt();
                break;
            case LONG:
                probOutputAttributeAggregator = new ProbabilityAttributeAggregatorLong();
                break;
            case DOUBLE:
                probOutputAttributeAggregator = new ProbabilityAttributeAggregatorDouble();
                break;
            default:
                throw new OperationNotSupportedException("Probability not supported for " + vType);
        }

    }

    public Attribute.Type getReturnType() {
        return probOutputAttributeAggregator.getReturnType();
    }

    @Override
    public Object processAdd(Object data) {
        return new IllegalStateException("Probability cannot process data");
    }

    @Override
    public Object processAdd(Object[] data) {
        return probOutputAttributeAggregator.processAdd(data);
    }

    @Override
    public Object processRemove(Object data) {
        return new IllegalStateException("Probability cannot process for data");
    }

    @Override
    public Object processRemove(Object[] data) {
        return probOutputAttributeAggregator.processRemove(data);
    }

    @Override
    public Object reset() {
        return probOutputAttributeAggregator.reset();
    }

    @Override
    public void start() {
        //Nothing to start
    }

    @Override
    public void stop() {
        //nothing to stop
    }

    @Override
    public Object[] currentState() {
        return probOutputAttributeAggregator.currentState();
    }

    @Override
    public void restoreState(Object[] state) {
        probOutputAttributeAggregator.restoreState(state);
    }


    private class ProbabilityAttributeAggregatorDouble extends ProbabilityAttributeAggregator {

        private boolean isSetDivider = false;

        private long probBucket[];
        private long totalEventCount = 0;
        private Queue<Double> dataList = new LinkedList<Double>();

        private final Attribute.Type type = Attribute.Type.DOUBLE;

        private double minValue = 0;
        private double maxValue = 0;
        private int numberOfParts = 0;
        private double divider = 1.0;

        public void setMinMax(double val1, double val2) {
            this.minValue = Math.min(val1, val2);
            this.maxValue = Math.max(val1, val2);
        }

        public void setNumberOfParts(int numberOfParts) {
            this.numberOfParts = numberOfParts;
        }

        public void setValue(double inputValue) {
            probBucket = new long[numberOfParts];
            divider = Math.abs((maxValue - minValue) / numberOfParts);

            if (divider == 0) {
                isSetDivider = false;
//                log.error("Divider can not be possible. Value : " + divider);
                divider = 1.0;
            }
        }

        public Attribute.Type getReturnType() {
            return type;
        }

        @Override
        public Object processAdd(Object[] data) {

            try {
                double inputValue = (Double) data[3];
                double temMin = Math.min((Double) data[0], inputValue);
                double temMax = Math.max((Double) data[1], inputValue);

                if (!isSetDivider) {
                    setMinMax(temMin, temMax);
                    setNumberOfParts((Integer) data[2]);
                    isSetDivider = true;
                    setValue(inputValue);
                }

                //Check minValue is change
                if (minValue > temMin) {
                    int additionalBuckets = (int) Math.ceil(((minValue - temMin) / divider));
                    minValue = temMin;

                    numberOfParts += additionalBuckets;
                    long temProbBucket[] = new long[numberOfParts];

                    System.arraycopy(probBucket, 0, temProbBucket, additionalBuckets, probBucket.length);
                    probBucket = temProbBucket;
                }

                //Check maxValue is change
                if (maxValue < temMax) {
                    int additionalBuckets = (int) Math.ceil(((temMax - maxValue) / divider));
                    maxValue = temMax;

                    numberOfParts += additionalBuckets;
                    long temProbBucket[] = new long[numberOfParts];

                    System.arraycopy(probBucket, 0, temProbBucket, 0, probBucket.length);
                    probBucket = temProbBucket;
                }

                int index = (int) Math.abs((inputValue - minValue) / divider);
                dataList.add(inputValue);

                numberOfParts = probBucket.length;
                if (index >= numberOfParts) {
                    index = numberOfParts - 1;
                } else if (index < 0) {
                    index = 0;
                }

                double prob = 1;
                if (totalEventCount != 0) {
                    prob = ((double) probBucket[index]) / totalEventCount;
                }

                if (prob < 0) {
                    prob = 0;
                }
                if (prob > 1) {
                    prob = 1;
                }

                totalEventCount++;
                probBucket[index] = probBucket[index] + 1;
                return prob;
            } catch (Exception e) {
                return 0;
            }

        }

        @Override
        public Object processRemove(Object[] obj) {

            Iterator itr = dataList.iterator();
            if (itr.hasNext()) {
                double fistCome = dataList.poll();
                int fistComeIndex = (int) Math.abs((fistCome - minValue) / divider);

                numberOfParts = probBucket.length;
                if (fistComeIndex >= numberOfParts) {
                    fistComeIndex = numberOfParts - 1;
                }
                if (fistComeIndex < 0) {
                    fistComeIndex = 0;
                }

                if (probBucket[fistComeIndex] >= 1) {
                    probBucket[fistComeIndex] = probBucket[fistComeIndex] - 1;
                    totalEventCount--;
                }
                return fistComeIndex;
            }

            return null;
        }

        @Override
        public Object reset() {
            totalEventCount = 0;
            return 0.0;
        }

        @Override
        public Object[] currentState() {
            return new Object[]{probBucket};
        }

        @Override
        public void restoreState(Object[] state) {
            probBucket = new long[numberOfParts];
        }

    }

    private class ProbabilityAttributeAggregatorFloat extends ProbabilityAttributeAggregator {

        private boolean isSetDivider = false;

        private long probBucket[];
        private long totalEventCount = 0;
        private Queue<Float> dataList = new LinkedList<Float>();

        private final Attribute.Type type = Attribute.Type.DOUBLE;

        private float minValue = 0;
        private float maxValue = 0;
        private int numberOfParts = 0;
        private double divider = 1.0;

        public void setMinMax(float val1, float val2) {
            this.minValue = Math.min(val1, val2);
            this.maxValue = Math.max(val1, val2);
        }

        public void setNumberOfParts(int numberOfParts) {
            this.numberOfParts = numberOfParts;
        }

        public void setValue(float inputValue) {
            probBucket = new long[numberOfParts];
            divider = Math.abs(((double) (maxValue - minValue)) / numberOfParts);

            if (divider == 0) {
                isSetDivider = false;
//                log.error("Divider can not be possible. Value : " + divider);
                divider = 1.0;
            }
        }

        public Attribute.Type getReturnType() {
            return type;
        }


        @Override
        public Object processAdd(Object[] data) {

            float inputValue = (Float) data[3];
            float temMin = Math.min((Float) data[0], inputValue);
            float temMax = Math.max((Float) data[1], inputValue);

            if (!isSetDivider) {
                setMinMax(temMin, temMax);
                setNumberOfParts((Integer) data[2]);
                isSetDivider = true;
                setValue(inputValue);
            }

            //Check minValue is change
            if (minValue > temMin) {
                int additionalBuckets = (int) Math.ceil(((minValue - temMin) / divider));
                minValue = temMin;

                numberOfParts += additionalBuckets;
                long temProbBucket[] = new long[numberOfParts];

                System.arraycopy(probBucket, 0, temProbBucket, additionalBuckets, probBucket.length);
                probBucket = temProbBucket;
            }

            //Check maxValue is change
            if (maxValue < temMax) {
                int additionalBuckets = (int) Math.ceil(((temMax - maxValue) / divider));
                maxValue = temMax;

                numberOfParts += additionalBuckets;
                long temProbBucket[] = new long[numberOfParts];

                System.arraycopy(probBucket, 0, temProbBucket, 0, probBucket.length);
                probBucket = temProbBucket;
            }

            int index = (int) Math.abs((inputValue - minValue) / divider);
            dataList.add(inputValue);

            numberOfParts = probBucket.length;
            if (index >= numberOfParts) {
                index = numberOfParts - 1;
            } else if (index < 0) {
                index = 0;
            }

            double prob = 1;
            if (totalEventCount != 0) {
                prob = ((double) probBucket[index]) / totalEventCount;
            }

            if (prob < 0) {
                prob = 0;
            }
            if (prob > 1) {
                prob = 1;
            }

            totalEventCount++;
            probBucket[index] = probBucket[index] + 1;
            return prob;
        }

        @Override
        public Object processRemove(Object[] obj) {

            Iterator itr = dataList.iterator();
            if (itr.hasNext()) {
                double fistCome = dataList.poll();
                int fistComeIndex = (int) Math.abs((fistCome - minValue) / divider);

                numberOfParts = probBucket.length;
                if (fistComeIndex >= numberOfParts) {
                    fistComeIndex = numberOfParts - 1;
                }
                if (fistComeIndex < 0) {
                    fistComeIndex = 0;
                }

                if (probBucket[fistComeIndex] >= 1) {
                    probBucket[fistComeIndex] = probBucket[fistComeIndex] - 1;
                    totalEventCount--;
                }
                return fistComeIndex;
            }

            return null;
        }

        @Override
        public Object reset() {
            totalEventCount = 0;
            return 0.0;
        }

        @Override
        public Object[] currentState() {
            return new Object[]{probBucket};
        }

        @Override
        public void restoreState(Object[] state) {
            probBucket = new long[numberOfParts];
        }

    }

    private class ProbabilityAttributeAggregatorInt extends ProbabilityAttributeAggregator {

        private boolean isSetDivider = false;

        private long probBucket[];
        private long totalEventCount = 0;
        private Queue<Integer> dataList = new LinkedList<Integer>();

        private final Attribute.Type type = Attribute.Type.DOUBLE;

        private int minValue = 0;
        private int maxValue = 0;
        private int numberOfParts = 0;
        private double divider = 1.0;

        public void setMinMax(int val1, int val2) {
            this.minValue = Math.min(val1, val2);
            this.maxValue = Math.max(val1, val2);
        }

        public void setNumberOfParts(int numberOfParts) {
            this.numberOfParts = numberOfParts;
        }

        public void setValue(int inputValue) {
            probBucket = new long[numberOfParts];
            divider = Math.abs(((double) (maxValue - minValue)) / numberOfParts);

            if (divider == 0) {
                isSetDivider = false;
//                log.error("Divider can not be possible. Value : " + divider);
                divider = 1.0;
            }
        }

        public Attribute.Type getReturnType() {
            return type;
        }

        @Override
        public Object processAdd(Object[] data) {

            int inputValue = (Integer) data[3];
            int temMin = Math.min((Integer) data[0], inputValue);
            int temMax = Math.max((Integer) data[1], inputValue);

            if (!isSetDivider) {
                setMinMax(temMin, temMax);
                setNumberOfParts((Integer) data[2]);
                isSetDivider = true;
                setValue(inputValue);
            }

            //Check minValue is change
            if (minValue > temMin) {
                int additionalBuckets = (int) Math.ceil(((minValue - temMin) / divider));
                minValue = temMin;

                numberOfParts += additionalBuckets;
                long temProbBucket[] = new long[numberOfParts];

                System.arraycopy(probBucket, 0, temProbBucket, additionalBuckets, probBucket.length);
                probBucket = temProbBucket;
            }

            //Check maxValue is change
            if (maxValue < temMax) {
                int additionalBuckets = (int) Math.ceil(((temMax - maxValue) / divider));
                maxValue = temMax;

                numberOfParts += additionalBuckets;
                long temProbBucket[] = new long[numberOfParts];

                System.arraycopy(probBucket, 0, temProbBucket, 0, probBucket.length);
                probBucket = temProbBucket;
            }

            int index = (int) Math.abs((inputValue - minValue) / divider);
            dataList.add(inputValue);

            numberOfParts = probBucket.length;
            if (index >= numberOfParts) {
                index = numberOfParts - 1;
            } else if (index < 0) {
                index = 0;
            }

            double prob = 1;
            if (totalEventCount != 0) {
                prob = ((double) probBucket[index]) / totalEventCount;
            }

            if (prob < 0) {
                prob = 0;
            }
            if (prob > 1) {
                prob = 1;
            }

            totalEventCount++;
            probBucket[index] = probBucket[index] + 1;
            return prob;
        }

        @Override
        public Object processRemove(Object[] obj) {

            Iterator itr = dataList.iterator();
            if (itr.hasNext()) {
                double fistCome = dataList.poll();
                int fistComeIndex = (int) Math.abs((fistCome - minValue) / divider);

                numberOfParts = probBucket.length;
                if (fistComeIndex >= numberOfParts) {
                    fistComeIndex = numberOfParts - 1;
                }
                if (fistComeIndex < 0) {
                    fistComeIndex = 0;
                }

                if (probBucket[fistComeIndex] >= 1) {
                    probBucket[fistComeIndex] = probBucket[fistComeIndex] - 1;
                    totalEventCount--;
                }
                return fistComeIndex;
            }

            return null;
        }

        @Override
        public Object reset() {
            totalEventCount = 0;
            return 0.0;
        }

        @Override
        public Object[] currentState() {
            return new Object[]{probBucket};
        }

        @Override
        public void restoreState(Object[] state) {
            probBucket = new long[numberOfParts];
        }

    }

    private class ProbabilityAttributeAggregatorLong extends ProbabilityAttributeAggregator {

        private boolean isSetDivider = false;

        private long probBucket[];
        private long totalEventCount = 0;
        private Queue<Long> dataList = new LinkedList<Long>();

        private final Attribute.Type type = Attribute.Type.DOUBLE;

        private long minValue = 0;
        private long maxValue = 0;
        private int numberOfParts = 0;
        private double divider = 1.0;

        public void setMinMax(long val1, long val2) {
            this.minValue = Math.min(val1, val2);
            this.maxValue = Math.max(val1, val2);
        }

        public void setNumberOfParts(int numberOfParts) {
            this.numberOfParts = numberOfParts;
        }

        public void setValue(long inputValue) {
            probBucket = new long[numberOfParts];
            divider = Math.abs(((double) (maxValue - minValue)) / numberOfParts);

            if (divider == 0) {
                isSetDivider = false;
//                log.error("Divider can not be possible. Value : " + divider);
                divider = 1.0;
            }
        }

        public Attribute.Type getReturnType() {
            return type;
        }

        @Override
        public Object processAdd(Object[] data) {

            long inputValue = (Long) data[3];
            long temMin = Math.min((Long) data[0], inputValue);
            long temMax = Math.max((Long) data[1], inputValue);

            if (!isSetDivider) {
                setMinMax(temMin, temMax);
                setNumberOfParts((Integer) data[2]);
                isSetDivider = true;
                setValue(inputValue);
            }

            //Check minValue is change
            if (minValue > temMin) {
                int additionalBuckets = (int) Math.ceil(((minValue - temMin) / divider));
                minValue = temMin;

                numberOfParts += additionalBuckets;
                long temProbBucket[] = new long[numberOfParts];

                System.arraycopy(probBucket, 0, temProbBucket, additionalBuckets, probBucket.length);
                probBucket = temProbBucket;
            }

            //Check maxValue is change
            if (maxValue < temMax) {
                int additionalBuckets = (int) Math.ceil(((temMax - maxValue) / divider));
                maxValue = temMax;

                numberOfParts += additionalBuckets;
                long temProbBucket[] = new long[numberOfParts];

                System.arraycopy(probBucket, 0, temProbBucket, 0, probBucket.length);
                probBucket = temProbBucket;
            }

            int index = (int) Math.abs((inputValue - minValue) / divider);
            dataList.add(inputValue);

            numberOfParts = probBucket.length;
            if (index >= numberOfParts) {
                index = numberOfParts - 1;
            } else if (index < 0) {
                index = 0;
            }

            double prob = 1;
            if (totalEventCount != 0) {
                prob = ((double) probBucket[index]) / totalEventCount;
            }

            if (prob < 0) {
                prob = 0;
            }
            if (prob > 1) {
                prob = 1;
            }

            totalEventCount++;
            probBucket[index] = probBucket[index] + 1;
            return prob;
        }

        @Override
        public Object processRemove(Object[] obj) {

            Iterator itr = dataList.iterator();
            if (itr.hasNext()) {
                double fistCome = dataList.poll();
                int fistComeIndex = (int) Math.abs((fistCome - minValue) / divider);

                numberOfParts = probBucket.length;
                if (fistComeIndex >= numberOfParts) {
                    fistComeIndex = numberOfParts - 1;
                }
                if (fistComeIndex < 0) {
                    fistComeIndex = 0;
                }

                if (probBucket[fistComeIndex] >= 1) {
                    probBucket[fistComeIndex] = probBucket[fistComeIndex] - 1;
                    totalEventCount--;
                }
                return fistComeIndex;
            }

            return null;
        }

        @Override
        public Object reset() {
            totalEventCount = 0;
            return 0.0;
        }

        @Override
        public Object[] currentState() {
            return new Object[]{probBucket};
        }

        @Override
        public void restoreState(Object[] state) {
            probBucket = new long[numberOfParts];
        }

    }


}
