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
* x(a,b);
* Returns the sum of 'a' and 'b'.
* Accept Type(s): INT,LONG,FLOAT,DOUBLE
* Return Type(s): INT,LONG,FLOAT,DOUBLE
*/
public class XSumFunctionExtension extends FunctionExecutor {

    private XSumFunctionExtension xSumFunction;

    @Override
    protected void init(ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {
        if (attributeExpressionExecutors.length != 2) {
            throw new ExecutionPlanValidationException("Invalid no of arguments passed to statistic:x() function, " +
                    "required 2, but found " + attributeExpressionExecutors.length);
        }

        Attribute.Type attributeType1 = attributeExpressionExecutors[0].getReturnType();
        Attribute.Type attributeType2 = attributeExpressionExecutors[1].getReturnType();

        if (attributeType1 == Attribute.Type.DOUBLE && attributeType2 == Attribute.Type.DOUBLE) {
            xSumFunction = new XSumFunctionExtensionDouble();
        } else if (attributeType1 == Attribute.Type.INT && attributeType2 == Attribute.Type.INT) {
            xSumFunction = new XSumFunctionExtensionInt();
        } else if (attributeType1 == Attribute.Type.FLOAT && attributeType2 == Attribute.Type.FLOAT) {
            xSumFunction = new XSumFunctionExtensionFloat();
        } else if (attributeType1 == Attribute.Type.LONG && attributeType2 == Attribute.Type.LONG) {
            xSumFunction = new XSumFunctionExtensionLong();
        } else {
            throw new ExecutionPlanValidationException("Invalid parameter type found for the arguments of statistic:x() function, ");
        }
    }

    @Override
    protected Object execute(Object[] data) {
        return xSumFunction.execute(data);
    }

    @Override
    protected Object execute(Object data) {
        return null;  //Since the max function takes in 2 parameters, this method does not get called. Hence, not implemented.
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
        return xSumFunction.getReturnType();
    }

    @Override
    public Object[] currentState() {
        return null;    //No need to maintain state.
    }

    @Override
    public void restoreState(Object[] state) {
        //Since there's no need to maintain a state, nothing needs to be done here.
    }


    private class XSumFunctionExtensionDouble extends XSumFunctionExtension {

        private final Attribute.Type type = Attribute.Type.DOUBLE;

        @Override
        protected Object execute(Object[] data) {
            double inputVal1 = 0d;
            double inputVal2 = 0d;

            if (data[0] == null && data[1] == null) {
                return null;
            }

            if (data[0] != null) {
                inputVal1 = (Double) data[0];
            }
            if (data[1] != null) {
                inputVal2 = (Double) data[1];
            }
            return inputVal1 + inputVal2;
        }

        @Override
        public Attribute.Type getReturnType() {
            return type;
        }

    }

    private class XSumFunctionExtensionInt extends XSumFunctionExtension {

        private final Attribute.Type type = Attribute.Type.INT;

        @Override
        protected Object execute(Object[] data) {
            int inputVal1 = 0;
            int inputVal2 = 0;

            if (data[0] == null && data[1] == null) {
                return null;
            }

            if (data[0] != null) {
                inputVal1 = (Integer) data[0];
            }
            if (data[1] != null) {
                inputVal2 = (Integer) data[1];
            }
            return inputVal1 + inputVal2;
        }

        @Override
        public Attribute.Type getReturnType() {
            return type;
        }

    }

    private class XSumFunctionExtensionFloat extends XSumFunctionExtension {

        private final Attribute.Type type = Attribute.Type.FLOAT;

        @Override
        protected Object execute(Object[] data) {
            float inputVal1 = 0f;
            float inputVal2 = 0f;

            if (data[0] == null && data[1] == null) {
                return null;
            }

            if (data[0] != null) {
                inputVal1 = (Float) data[0];
            }
            if (data[1] != null) {
                inputVal2 = (Float) data[1];
            }
            return inputVal1 + inputVal2;
        }

        @Override
        public Attribute.Type getReturnType() {
            return type;
        }

    }

    private class XSumFunctionExtensionLong extends XSumFunctionExtension {

        private final Attribute.Type type = Attribute.Type.LONG;

        @Override
        protected Object execute(Object[] data) {
            long inputVal1 = 0l;
            long inputVal2 = 0l;

            if (data[0] == null && data[1] == null) {
                return null;
            }

            if (data[0] != null) {
                inputVal1 = (Long) data[0];
            }
            if (data[1] != null) {
                inputVal2 = (Long) data[1];
            }
            return inputVal1 + inputVal2;
        }

        @Override
        public Attribute.Type getReturnType() {
            return type;
        }

    }


}
