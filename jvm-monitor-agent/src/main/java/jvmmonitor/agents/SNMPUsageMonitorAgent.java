package jvmmonitor.agents;

import jvmmonitor.UsageMonitorAgent;
import jvmmonitor.exceptions.AccessingUsageStatisticFailedException;
import jvmmonitor.exceptions.MonitorAgentInitializationFailed;
import jvmmonitor.models.CPUStatistic;
import jvmmonitor.models.GarbageCollectionStatistic;
import jvmmonitor.models.MemoryStatistic;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.List;

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
public class SNMPUsageMonitorAgent extends UsageMonitorAgent {

    private Snmp snmp;
    private String address;

    public SNMPUsageMonitorAgent(String snmpAddress, String snmpPort) throws MonitorAgentInitializationFailed {

        TransportMapping transport = null;
        try {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();
            address = "udp:" + snmpAddress + "/" + snmpPort;
        } catch (IOException e) {
            throw new MonitorAgentInitializationFailed(e.getMessage(), e);
        }
    }

    @Override protected List<CPUStatistic> getCPUStatistics() throws AccessingUsageStatisticFailedException {
        return null;
    }

    @Override protected List<MemoryStatistic> getMemoryStatistics() throws AccessingUsageStatisticFailedException {
        return null;
    }

    @Override protected List<GarbageCollectionStatistic> getGarbageCollectionStatistics()
            throws AccessingUsageStatisticFailedException {
        return null;
    }

    @Override public String getTargetedApplicationId() {
        return null;
    }

    /**
     * This method returns a Target, which contains information about
     * where the data should be fetched and how.
     *
     * @return
     */
    private Target getTarget(String address) {
        Address targetAddress = GenericAddress.parse(address);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("secret"));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }
}
