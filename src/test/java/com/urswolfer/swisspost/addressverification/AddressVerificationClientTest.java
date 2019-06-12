/*
 * Copyright (C) 2018 Urs Wolfer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.urswolfer.swisspost.addressverification;

import static org.junit.jupiter.api.Assertions.*;

import ch.post.adresscheckerextern.v4_02_00.AdressCheckerRequestType;
import org.junit.jupiter.api.Test;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import java.math.BigInteger;

class AddressVerificationClientTest {

    @Test
    void testCreateSoapRequestWithoutValuesShouldApplyDefaults() throws Exception {
        AddressVerificationClient addressVerificationClient = new AddressVerificationClient("", "TU_X", "");

        SOAPMessage message = addressVerificationClient.createSoapRequest(new AddressVerificationRequest());

        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        assertEquals("v4:AdressCheckerRequest", envelope.getBody().getFirstChild().getNodeName());
        assertEquals("0", envelope.getBody().getFirstChild().getChildNodes().item(1).getFirstChild().getNodeValue());
        NodeList paramsNodeList = envelope.getBody().getFirstChild().getChildNodes().item(0).getChildNodes();
        assertEquals("TU_X", paramsNodeList.item(1).getFirstChild().getNodeValue());
    }

    @Test
    void testCreateSoapRequestWithHouseKeySet() throws Exception {
        AddressVerificationClient addressVerificationClient = new AddressVerificationClient("", "", "");
        AddressVerificationRequest request = new AddressVerificationRequest();
        request.setHouseKey(BigInteger.TEN);

        SOAPMessage message = addressVerificationClient.createSoapRequest(request);

        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        assertEquals("10", envelope.getBody().getFirstChild().getChildNodes().item(1).getFirstChild().getNodeValue());
    }

    @Test
    void testCreateSoapRequestWithRequestTypeSet() throws Exception {
        AddressVerificationClient addressVerificationClient = new AddressVerificationClient("", "TU_X", "");
        AddressVerificationRequest request = new AddressVerificationRequest();
        AdressCheckerRequestType.Params params = new AdressCheckerRequestType.Params();
        params.setSearchType(BigInteger.TEN);
        request.setParams(params);

        SOAPMessage message = addressVerificationClient.createSoapRequest(request);

        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        NodeList paramsNodeList = envelope.getBody().getFirstChild().getChildNodes().item(0).getChildNodes();
        assertEquals("10", paramsNodeList.item(1).getFirstChild().getNodeValue());
        assertEquals("TU_X", paramsNodeList.item(0).getFirstChild().getNodeValue());
    }
}