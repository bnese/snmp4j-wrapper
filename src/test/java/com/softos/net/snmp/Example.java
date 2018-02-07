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
import org.snmp4j.Target;
import org.snmp4j.smi.OID;

import java.io.IOException;
import java.text.ParseException;

import static com.softos.net.snmp.SnmpTargetBuilder.Version.V2c;

public class Example {
    public static void main(String[] args) throws IOException, ParseException {
        Target target = SnmpTargetBuilder.builder()
                .withHost("192.168.8.1")
                .withCommunity("public")
                .withVersion(V2c)
                .build();

        final Snmp snmp = SnmpBuilder.getDefaultSnmp();

        final Snmp4jWrapper wrapper = new Snmp4jWrapper(target, snmp);

        final OID[] ifIndexedColumns = {
                new OID("1.3.6.1.2.1.2.2.1.1"),
                new OID("1.3.6.1.2.1.2.2.1.2"),
                new OID("1.3.6.1.2.1.31.1.1.1.1"),
                new OID("1.3.6.1.2.1.31.1.1.1.18"),
                new OID("1.3.6.1.2.1.2.2.1.5")};

        final OID[] ipaddressIndexedColumns = {
                new OID("1.3.6.1.2.1.4.20.1.1"),
                new OID("1.3.6.1.2.1.4.20.1.2"),
                new OID("1.3.6.1.2.1.4.20.1.3"),
                new OID("1.3.6.1.2.1.4.20.1.4"),
                new OID("1.3.6.1.2.1.4.20.1.5")
        };

        System.out.println("ifIndexedColumns - no lower bound index, stop at index 2 (inclusive)");
        wrapper.getTable(ifIndexedColumns, null, new OID("2"))
                .forEach((oid, variableBindings) -> System.out.println(oid + ": " + variableBindings));
        System.out.println();

        System.out.println("ifIndexedColumns - start at index 1 (exclusive), stop at index 3 (inclusive)");
        wrapper.getTable(ifIndexedColumns, new OID("1"), new OID("3"))
                .forEach((oid, variableBindings) -> System.out.println(oid + ": " + variableBindings));
        System.out.println();

        System.out.println("ipaddressIndexedColumns - start at index 192.168.1.0 (exclusive), no upper bound");
        wrapper.getTable(ipaddressIndexedColumns, new OID("192.168.1.0"), null)
                .forEach((oid, variableBindings) -> System.out.println(oid + ": " + variableBindings));
        System.out.println();

        System.out.println("ipaddressIndexedColumns - No bounds");
        wrapper.getTable(ipaddressIndexedColumns)
                .forEach((oid, variableBindings) -> System.out.println(oid + ": " + variableBindings));
        System.out.println();

        System.out.println("sysUpTime:");
        wrapper.getNext(new OID("1.3.6.1.2.1.1.3"))
                .forEach(System.out::println);
        System.out.println();

    }
}