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

import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Snmp4jWrapper {
    private final Target target;
    private final Snmp snmp;
    private final PDUFactory pduFactory;

    public static Snmp4jWrapperBuilder builder() {
        return new Snmp4jWrapperBuilder();
    }

    public Snmp4jWrapper(Target target, Snmp snmp) {
        this(target, snmp, new DefaultPDUFactory());
    }

    public Snmp4jWrapper(Target target, Snmp snmp, PDUFactory pduFactory) {
        this.target = target;
        this.snmp = snmp;
        this.pduFactory = pduFactory;
    }

    public Map<OID, List<VariableBinding>> getTable(OID[] columnOIDs) {
        return getTable(columnOIDs, null, null);
    }

    public Map<OID, List<VariableBinding>> getTable(OID[] columnOIDs,
                                                    OID lowerBoundIndex,
                                                    OID upperBoundIndex) {
        return Optional.of(new TableUtils(snmp, pduFactory))
                .map(e -> e.getTable(target, columnOIDs, lowerBoundIndex, upperBoundIndex))
                .map(Collection::stream)
                .orElseThrow(() -> new RuntimeException("Request timed out."))
                .filter(Snmp4jWrapper::nonError)
                .collect(Collectors.toMap(
                        TableEvent::getIndex,
                        tableEvent -> Arrays.stream(tableEvent.getColumns()).filter(Objects::nonNull).collect(Collectors.toList())
                ));
    }

    public List<VariableBinding> walk(OID oid) {
        return Optional.of(new TreeUtils(snmp, pduFactory))
                .map(e -> e.getSubtree(target, oid))
                .map(Collection::stream)
                .orElseThrow(() -> new RuntimeException("Request timed out."))
                .filter(Snmp4jWrapper::nonError)
                .map(TreeEvent::getVariableBindings)
                .map(Arrays::asList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<VariableBinding> get(OID oid) {
        return send(PDU.GET, oid);
    }

    public List<VariableBinding> getNext(OID oid) {
        return send(PDU.GETNEXT, oid);
    }

    public List<VariableBinding> set(VariableBinding variableBinding) {
        return send(createPdu(PDU.SET, new VariableBinding[]{variableBinding}));
    }

    public List<VariableBinding> getBulk(OID[] oids, int maxRepetitions, int nonRepeaters) {
        PDU pdu = createPdu(PDU.GETBULK, oids);
        pdu.setMaxRepetitions(maxRepetitions);
        pdu.setNonRepeaters(nonRepeaters);
        return send(pdu);
    }

    private List<VariableBinding> send(int pduType, OID oid) {
        return send(pduType, new OID[]{oid});
    }

    private List<VariableBinding> send(int pduType, OID[] oids) {
        return send(createPdu(pduType, oids));
    }

    private List<VariableBinding> send(PDU pdu) {
        try {
            return Optional.ofNullable(snmp.send(pdu, target))
                    .filter(Snmp4jWrapper::nonError)
                    .map(this::getResponse)
                    .orElseThrow(() -> new RuntimeException("Request timed out."));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PDU createPdu(int pduType, OID[] oids) {
        return createPdu(pduType, Arrays.stream(oids)
                .map(VariableBinding::new)
                .toArray(VariableBinding[]::new));
    }

    private PDU createPdu(int pduType, VariableBinding[] variableBindings) {
        PDU pdu = DefaultPDUFactory.createPDU(target, pduType);
        pdu.addAll(variableBindings);
        return pdu;
    }

    private List<VariableBinding> getResponse(ResponseEvent responseEvent) {
        if (responseEvent.getError() != null) {
            throw new RuntimeException(responseEvent.getError());
        }

        return Optional.ofNullable(responseEvent.getResponse())
                .map(PDU::toArray)
                .map(Arrays::asList)
                .orElseThrow(() -> new RuntimeException("Request timed out."));
    }

    private static boolean nonError(ResponseEvent event) {
        Optional.ofNullable(event).ifPresent(e -> {

            Optional.ofNullable(e.getError()).ifPresent(error -> {
                throw new RuntimeException(error);
            });

            Optional.ofNullable(e.getResponse())
                    .filter(el -> el.getErrorStatus() != 0)
                    .map(PDU::getErrorStatusText)
                    .ifPresent(errorMessage -> {
                        throw new RuntimeException(errorMessage);
                    });

        });

        return event != null;
    }

    private static boolean nonError(RetrievalEvent event) {
        if (event == null) {
            return false;
        } else if (event.isError()) {
            throw new RuntimeException(event.getErrorMessage());
        }
        return true;
    }
}
