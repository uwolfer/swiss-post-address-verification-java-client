/*
 * Copyright (C) 2018 Urs Wolfer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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