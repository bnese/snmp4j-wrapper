/*-
 * ~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~
 * SNMP4J Wrapper
 * ~
 * Copyright (C) 2017 - 2018 Børge Nese
 * ~
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
 */
package com.softos.net.snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;

public class SnmpTargetBuilder {
    enum Version {
        V1(SnmpConstants.version1),
        V2c(SnmpConstants.version2c);
        public final int value;

        Version(int value) {
            this.value = value;
        }
    }

    private String protocol = "udp";
    private int port = 161;
    private String host;
    private String community;
    private int numberOfRetries = 2;
    private long timeout = 1500;
    private Version version = Version.V2c;


    public static SnmpTargetBuilder builder() {
        return new SnmpTargetBuilder();
    }

    public SnmpTargetBuilder withProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public SnmpTargetBuilder withPort(int port) {
        this.port = port;
        return this;
    }

    public SnmpTargetBuilder withHost(String host) {
        this.host = host;
        return this;
    }

    public SnmpTargetBuilder withCommunity(String community) {
        this.community = community;
        return this;
    }

    public SnmpTargetBuilder withNumberOfRetries(int numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
        return this;
    }

    public SnmpTargetBuilder withTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public SnmpTargetBuilder withVersion(Version version) {
        this.version = version;
        return this;
    }

    public Target build() {
        Address targetAddress = GenericAddress.parse(String.format("%s:%s/%s", protocol, host, port));
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        target.setRetries(numberOfRetries);
        target.setTimeout(timeout);
        target.setVersion(version.value);
        return target;
    }
}
