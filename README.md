[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# SNMP4J Wrapper

# Introduction
A small and convenient wrapper around SNMP4J. Require Java 8 or higher.

## Maven
```xml
<dependency>
        <groupId>com.softos.net</groupId>
        <artifactId>snmp4j-wrapper</artifactId>
        <version>1.0.0</version>
</dependency>
```

## Example

```java
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
```

### Example output
```
ifIndexedColumns - no lower bound index, stop at index 2 (inclusive)
1: [1.3.6.1.2.1.2.2.1.1.1 = 1, 1.3.6.1.2.1.2.2.1.2.1 = GigabitEthernet8, 1.3.6.1.2.1.31.1.1.1.1.1 = Gi8, 1.3.6.1.2.1.31.1.1.1.18.1 = WAN, 1.3.6.1.2.1.2.2.1.5.1 = 1000000000]
2: [1.3.6.1.2.1.2.2.1.1.2 = 2, 1.3.6.1.2.1.2.2.1.2.2 = GigabitEthernet9, 1.3.6.1.2.1.31.1.1.1.1.2 = Gi9, 1.3.6.1.2.1.31.1.1.1.18.2 = , 1.3.6.1.2.1.2.2.1.5.2 = 1000000000]

ifIndexedColumns - start at index 1 (exclusive), stop at index 3 (inclusive)
2: [1.3.6.1.2.1.2.2.1.1.2 = 2, 1.3.6.1.2.1.2.2.1.2.2 = GigabitEthernet9, 1.3.6.1.2.1.31.1.1.1.1.2 = Gi9, 1.3.6.1.2.1.31.1.1.1.18.2 = , 1.3.6.1.2.1.2.2.1.5.2 = 1000000000]
3: [1.3.6.1.2.1.2.2.1.1.3 = 3, 1.3.6.1.2.1.2.2.1.2.3 = GigabitEthernet0, 1.3.6.1.2.1.31.1.1.1.1.3 = Gi0, 1.3.6.1.2.1.31.1.1.1.18.3 = VPN-Server, 1.3.6.1.2.1.2.2.1.5.3 = 100000000]

ipaddressIndexedColumns - start at index 192.168.1.0 (exclusive), no upper bound
192.168.9.9: [1.3.6.1.2.1.4.20.1.1.192.168.9.9 = 192.168.9.9, 1.3.6.1.2.1.4.20.1.2.192.168.9.9 = 20, 1.3.6.1.2.1.4.20.1.3.192.168.9.9 = 255.255.255.252, 1.3.6.1.2.1.4.20.1.4.192.168.9.9 = 1, 1.3.6.1.2.1.4.20.1.5.192.168.9.9 = 18024]
192.168.40.1: [1.3.6.1.2.1.4.20.1.1.192.168.40.1 = 192.168.40.1, 1.3.6.1.2.1.4.20.1.2.192.168.40.1 = 21, 1.3.6.1.2.1.4.20.1.3.192.168.40.1 = 255.255.255.0, 1.3.6.1.2.1.4.20.1.4.192.168.40.1 = 1, 1.3.6.1.2.1.4.20.1.5.192.168.40.1 = 18024]
192.168.8.1: [1.3.6.1.2.1.4.20.1.1.192.168.8.1 = 192.168.8.1, 1.3.6.1.2.1.4.20.1.2.192.168.8.1 = 13, 1.3.6.1.2.1.4.20.1.3.192.168.8.1 = 255.255.255.0, 1.3.6.1.2.1.4.20.1.4.192.168.8.1 = 1, 1.3.6.1.2.1.4.20.1.5.192.168.8.1 = 18024]

ipaddressIndexedColumns - No bounds
192.168.9.9: [1.3.6.1.2.1.4.20.1.1.192.168.9.9 = 192.168.9.9, 1.3.6.1.2.1.4.20.1.2.192.168.9.9 = 20, 1.3.6.1.2.1.4.20.1.3.192.168.9.9 = 255.255.255.252, 1.3.6.1.2.1.4.20.1.4.192.168.9.9 = 1, 1.3.6.1.2.1.4.20.1.5.192.168.9.9 = 18024]
192.168.40.1: [1.3.6.1.2.1.4.20.1.1.192.168.40.1 = 192.168.40.1, 1.3.6.1.2.1.4.20.1.2.192.168.40.1 = 21, 1.3.6.1.2.1.4.20.1.3.192.168.40.1 = 255.255.255.0, 1.3.6.1.2.1.4.20.1.4.192.168.40.1 = 1, 1.3.6.1.2.1.4.20.1.5.192.168.40.1 = 18024]
92.x.y.z: [1.3.6.1.2.1.4.20.1.1.92.x.y.z = 92.x.y.z, 1.3.6.1.2.1.4.20.1.2.92.x.y.z = 16, 1.3.6.1.2.1.4.20.1.3.92.x.y.z = 255.255.248.0, 1.3.6.1.2.1.4.20.1.4.92.x.y.z = 1, 1.3.6.1.2.1.4.20.1.5.92.x.y.z = 18024]
192.168.8.1: [1.3.6.1.2.1.4.20.1.1.192.168.8.1 = 192.168.8.1, 1.3.6.1.2.1.4.20.1.2.192.168.8.1 = 13, 1.3.6.1.2.1.4.20.1.3.192.168.8.1 = 255.255.255.0, 1.3.6.1.2.1.4.20.1.4.192.168.8.1 = 1, 1.3.6.1.2.1.4.20.1.5.192.168.8.1 = 18024]

sysUpTime:
1.3.6.1.2.1.1.3.0 = 17 days, 4:17:40.52
```

## License
Licensed under Apache License Version 2.0, see [LICENSE](LICENSE). See [THIRD-PARTY-LICENSE.txt](THIRD-PARTY-LICENSE.txt) for dependency licenses. 
