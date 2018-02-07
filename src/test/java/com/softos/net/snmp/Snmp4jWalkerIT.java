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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.snmp4j.Snmp;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*
* Start SnmpAgent prior to running these tests
*/
public class Snmp4jWalkerIT {
    private static final String SYS_UP_TIME_REGEX = "(.*)(\\d+):(\\d+):(\\d+)\\.(\\d+)";
    private static final OID SYS_UP_TIME_OID = new OID("1.3.6.1.2.1.1.3");
    private static final OID SYSTEM_OID = new OID("1.3.6.1.2.1.1");
    private static final OID SYS_CONTACT = new OID("1.3.6.1.2.1.1.4.0");

    private Snmp4jWrapper wrapper;
    private Snmp snmp;
    private SnmpTargetBuilder snmpTargetBuilder;

    @Before
    public void setup() throws IOException {
        snmpTargetBuilder = SnmpTargetBuilder.builder()
                .withHost("localhost")
                .withCommunity("public")
                .withPort(2001);

        snmp = SnmpBuilder.getDefaultSnmp();
        wrapper = new Snmp4jWrapper(snmpTargetBuilder.build(), snmp);
    }

    @Test
    public void table() {
        Map<OID, List<VariableBinding>> result = wrapper.getTable(new OID[]{new OID("1.3.6.1.6.3.16.1.4.1")});

        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void walk() {
        List<VariableBinding> result = wrapper.walk(SYSTEM_OID);

        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void get_withNoIndex_shouldReturnNoSuchInstance() {
        List<VariableBinding> result = wrapper.get(SYS_UP_TIME_OID);
        String value = result.get(0).toValueString();

        Assert.assertEquals("noSuchInstance", value);
    }

    @Test
    public void get_withIndex_shouldReturnValue() {
        List<VariableBinding> result = wrapper.get(((OID) SYS_UP_TIME_OID.clone()).append("0"));
        String value = result.get(0).toValueString();

        Assert.assertTrue("Value is: " + value, value.matches(SYS_UP_TIME_REGEX));
    }

    @Test
    public void getNext_shouldReturnValue() throws InterruptedException {
        List<VariableBinding> result = wrapper.getNext(SYS_UP_TIME_OID);
        String value = result.get(0).toValueString();

        Assert.assertTrue("Value is: " + value, value.matches(SYS_UP_TIME_REGEX));
    }

    @Test
    public void getBulk() {
        List<VariableBinding> result = wrapper.getBulk(new OID[]{SYS_UP_TIME_OID, new OID("1.3.6.1.2.1.1.9.1.2.1")}, 3, 0);

        Assert.assertEquals(6, result.size());
    }

    @Test
    public void set() {
        String randomValue = UUID.randomUUID().toString();

        List<VariableBinding> requestSysContact = wrapper.get(SYS_CONTACT);
        Assert.assertFalse(requestSysContact.isEmpty());
        Assert.assertFalse(randomValue.equals(requestSysContact.get(0).toValueString()));

        List<VariableBinding> setValue = wrapper.set(new VariableBinding(SYS_CONTACT, new OctetString(randomValue)));
        List<VariableBinding> actualValue = wrapper.get(SYS_CONTACT);
        Assert.assertEquals(setValue, actualValue);
    }
}
