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

import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;

import java.io.IOException;
import java.util.Optional;

public class Snmp4jWrapperBuilder {
    private SnmpBuilder snmpBuilder;
    private SnmpTargetBuilder snmpTargetBuilder;
    private PDUFactory pduFactory = new DefaultPDUFactory();

    public static Snmp4jWrapperBuilder builder() throws IOException {
        return new Snmp4jWrapperBuilder();
    }

    public Snmp4jWrapperBuilder withSnmpBuilder(SnmpBuilder snmpBuilder) {
        this.snmpBuilder = snmpBuilder;
        return this;
    }

    public Snmp4jWrapperBuilder withTargetBuilder(SnmpTargetBuilder snmpTargetBuilder) {
        this.snmpTargetBuilder = snmpTargetBuilder;
        return this;
    }

    public Snmp4jWrapperBuilder withPduFactory(PDUFactory pduFactory) {
        this.pduFactory = pduFactory;
        return this;
    }

    public Snmp4jWrapper build() throws IOException {
        return new Snmp4jWrapper(snmpTargetBuilder.build(),
                Optional.ofNullable(snmpBuilder).orElse(SnmpBuilder.builder()).build(),
                pduFactory);
    }
}
