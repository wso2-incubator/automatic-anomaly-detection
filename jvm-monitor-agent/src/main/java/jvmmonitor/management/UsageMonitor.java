package jvmmonitor.management;

import jvmmonitor.model.UsageLog;

import java.lang.management.PlatformManagedObject;

/*
*  Copyright (c) ${date}, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
abstract class UsageMonitor<T extends PlatformManagedObject> {

    protected T mxBean;

    /**
     * Create UsageLog obj according to the management class type
     *
     * @return
     */
    public UsageLog getUsageLog() {
        if (mxBean != null) {
            return getUsageDataFromMXBean();
        } else {
            throw new NullPointerException();
        }
    }

    /**
     * Access the MXBeans and collect the desired usage data into the UsageLog obj
     *
     * @return
     */
    protected abstract UsageLog getUsageDataFromMXBean();
}
