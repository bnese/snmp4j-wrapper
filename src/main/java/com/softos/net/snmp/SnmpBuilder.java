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

import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.Address;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

public class SnmpBuilder {
    private TransportMapping<? extends Address> transport = null;

    public static Snmp getDefaultSnmp() throws IOException {
        return new SnmpBuilder().build();
    }

    public static SnmpBuilder builder() throws IOException {
        return new SnmpBuilder();
    }

    private SnmpBuilder() throws IOException {
    }

    public SnmpBuilder withTransport(TransportMapping<? extends Address> transport) {
        this.transport = transport;
        return this;
    }

    public Snmp build() throws IOException {
        if (transport == null) {
            transport = new DefaultUdpTransportMapping();
            transport.listen();
        }
        Snmp snmp = new Snmp(transport);
        snmp.addTransportMapping(transport);

        return snmp;
    }
}
