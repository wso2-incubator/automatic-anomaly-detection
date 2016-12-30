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

import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.executor.function.FunctionExecutor;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.exception.ExecutionPlanValidationException;

/*
* sd(x, xx, n);
* Returns the value of sqrt( (xx_val / n) - pow(x_val / n, 2.0) ).
* Accept Type(s): INT,LONG,FLOAT,DOUBLE
* Return Type(s): DOUBLE
*/
public class SDFunctionExtension extends FunctionExecutor {

    private SDFunctionExtension sdFunction;

    @Override
    protected void init(ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {
        if (attributeExpressionExecutors.length != 3) {
            throw new ExecutionPlanValidationException("Invalid no of arguments passed to statistic:sd() function, " +
                    "required 3, but found " + attributeExpressionExecutors.length);
        }

        Attribute.Type attributeType1 = attributeExpressionExecutors[0].getReturnType();
        Attribute.Type attributeType2 = attributeExpressionExecutors[1].getReturnType();
        Attribute.Type attributeType3 = attributeExpressionExecutors[2].getReturnType();

        if (attributeType3 != Attribute.Type.INT) {
            throw new ExecutionPlanValidationException("Invalid parameter type found for the arguments of statistic:sd() function. Required INT, but found " + attributeType3.toString());
        } else if (attributeType1 == Attribute.Type.DOUBLE && attributeType2 == Attribute.Type.DOUBLE) {
            sdFunction = new SDFunctionExtensionDouble();
        } else if (attributeType1 == Attribute.Type.INT && attributeType2 == Attribute.Type.INT) {
            sdFunction = new SDFunctionExtensionInt();
        } else if (attributeType1 == Attribute.Type.FLOAT && attributeType2 == Attribute.Type.FLOAT) {
            sdFunction = new SDFunctionExtensionFloat();
        } else if (attributeType1 == Attribute.Type.LONG && attributeType2 == Attribute.Type.LONG) {
            sdFunction = new SDFunctionExtensionLong();
        } else {
            throw new ExecutionPlanValidationException("Invalid parameter type found for the arguments of statistic:x() function, ");
        }
    }

    @Override
    protected Object execute(Object[] data) {
        return sdFunction.execute(data);
    }

    @Override
    protected Object execute(Object data) {
        return null;  //Since the max function takes in 3 parameters, this method does not get called. Hence, not implemented.
    }

    @Override
    public void start() {
        //Nothing to start.
    }

    @Override
    public void stop() {
        //Nothing to stop.
    }

    @Override
    public Attribute.Type getReturnType() {
        return sdFunction.getReturnType();
    }

    @Override
    public Object[] currentState() {
        return null;    //No need to maintain state.
    }

    @Override
    public void restoreState(Object[] state) {
        //Since there's no need to maintain a state, nothing needs to be done here.
    }


    private class SDFunctionExtensionDouble extends SDFunctionExtension {

        private final Attribute.Type type = Attribute.Type.DOUBLE;

        @Override
        protected Object execute(Object[] data) {
            double x_val;
            double xx_val;
            int n;

            if (data[0] == null || data[1] == null || data[2] == null) {
                return null;
            }

            n = (Integer) data[2];
            if (n == 0) {
                return null;
            }

            x_val = (Double) data[0];
            xx_val = (Double) data[1];
            double temVal = Math.sqrt((xx_val / n) - Math.pow(x_val / n, 2.0));

            if (Double.isNaN(temVal)) {
                return 0d;
            }
            return temVal;
        }

        @Override
        public Attribute.Type getReturnType() {
            return type;
        }

    }

    private class SDFunctionExtensionInt extends SDFunctionExtension {

        private final Attribute.Type type = Attribute.Type.DOUBLE;

        @Override
        protected Object execute(Object[] data) {
            int x_val;
            int xx_val;
            int n;

            if (data[0] == null || data[1] == null || data[2] == null) {
                return null;
            }

            n = (Integer) data[2];
            if (n == 0) {
                return null;
            }

            x_val = (Integer) data[0];
            xx_val = (Integer) data[1];
            double temVal = Math.sqrt((((double) xx_val) / n) - Math.pow(((double) x_val) / n, 2.0));

            if (Double.isNaN(temVal)) {
                return 0d;
            }
            return temVal;
        }

        @Override
        public Attribute.Type getReturnType() {
            return type;
        }

    }

    private class SDFunctionExtensionFloat extends SDFunctionExtension {

        private final Attribute.Type type = Attribute.Type.DOUBLE;

        @Override
        protected Object execute(Object[] data) {
            float x_val;
            float xx_val;
            int n;

            if (data[0] == null || data[1] == null || data[2] == null) {
                return null;
            }

            n = (Integer) data[2];
            if (n == 0) {
                return null;
            }

            x_val = (Float) data[0];
            xx_val = (Float) data[1];
            double temVal = Math.sqrt((((double) xx_val) / n) - Math.pow(((double) x_val) / n, 2.0));

            if (Double.isNaN(temVal)) {
                return 0d;
            }
            return temVal;
        }

        @Override
        public Attribute.Type getReturnType() {
            return type;
        }

    }

    private class SDFunctionExtensionLong extends SDFunctionExtension {

        private final Attribute.Type type = Attribute.Type.DOUBLE;

        @Override
        protected Object execute(Object[] data) {
            long x_val;
            long xx_val;
            int n;

            if (data[0] == null || data[1] == null || data[2] == null) {
                return null;
            }

            n = (Integer) data[2];
            if (n == 0) {
                return null;
            }

            x_val = (Long) data[0];
            xx_val = (Long) data[1];
            double temVal = Math.sqrt((((double) xx_val) / n) - Math.pow(((double) x_val) / n, 2.0));

            if (Double.isNaN(temVal)) {
                return 0d;
            }
            return temVal;
        }

        @Override
        public Attribute.Type getReturnType() {
            return type;
        }

    }

}
