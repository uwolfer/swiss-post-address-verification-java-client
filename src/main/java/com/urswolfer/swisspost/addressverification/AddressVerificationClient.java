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

import ch.post.adresscheckerextern.v4_02_00.AdressCheckerRequestType;
import ch.post.adresscheckerextern.v4_02_00.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class AddressVerificationClient {

    private final String endpointUrl;
    private final String userName;

    public AddressVerificationClient(String endpointUrl,
                                     String userName,
                                     String password) {
        this.endpointUrl = endpointUrl;
        this.userName = userName;
        setupAuthenticator(userName, password);
    }

    public AddressVerificationResponse callWebService(AddressVerificationRequest addressVerificationRequest) {
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            try {
                SOAPMessage soapRequest = createSoapRequest(addressVerificationRequest);
                SOAPMessage soapResponse = soapConnection.call(soapRequest, endpointUrl);
                AddressVerificationResponse addressVerificationResponse = parseSoapResponse(soapResponse);
                addXmlContent(soapResponse, addressVerificationResponse);
                return addressVerificationResponse;
            } catch (TransformerException e) {
                throw new RuntimeException(e);
            } finally {
                soapConnection.close();
            }
        } catch (SOAPException | JAXBException e) {
            throw new AddressVerificationClientException(e);
        }
    }

    private void addXmlContent(SOAPMessage soapResponse, AddressVerificationResponse addressVerificationResponse) throws SOAPException, TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult(outputStream);
        Source soapContent = soapResponse.getSOAPPart().getContent();
        transformer.transform(soapContent, streamResult);
        String xmlContent = new String(outputStream.toByteArray());
        addressVerificationResponse.setXmlContent(xmlContent);
    }

    private void setupAuthenticator(final String userName, final String password) {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                if (getRequestingURL().toString().startsWith(endpointUrl)) {
                    return new PasswordAuthentication(userName, password.toCharArray());
                }
                return null;
            }
        });
    }

    SOAPMessage createSoapRequest(AddressVerificationRequest addressVerificationRequest)
            throws SOAPException, JAXBException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        createSoapEnvelope(soapMessage, addressVerificationRequest);
        soapMessage.saveChanges();
        return soapMessage;
    }

    private void createSoapEnvelope(SOAPMessage soapMessage,
                                    AddressVerificationRequest addressVerificationRequest)
            throws SOAPException, JAXBException {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        JAXBElement<AdressCheckerRequestType> addressVerificationRequestElm =
                new ObjectFactory().createAdressCheckerRequest(addressVerificationRequest);
        String namespace = "v4";
        String namespaceURI = addressVerificationRequestElm.getName().getNamespaceURI();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        soapEnvelope.addNamespaceDeclaration(namespace, namespaceURI);
        SOAPBody soapBody = soapEnvelope.getBody();

        applyDefaults(addressVerificationRequest);
        addressVerificationRequest.getParams().setCallUser(userName);

        JAXBContext jaxbContext = JAXBContext.newInstance(AdressCheckerRequestType.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(addressVerificationRequestElm, soapBody);
    }

    private AddressVerificationResponse parseSoapResponse(SOAPMessage soapMessage)
            throws SOAPException, JAXBException {
        SOAPBody soapBody = soapMessage.getSOAPBody();
        Class<AddressVerificationResponse> clazz = AddressVerificationResponse.class;
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return unmarshaller.unmarshal(soapBody.getFirstChild(), clazz).getValue();
    }

    private void applyDefaults(AddressVerificationRequest addressVerificationRequest) {
        if (addressVerificationRequest.getHouseKey() == null) {
            addressVerificationRequest.setHouseKey(BigInteger.ZERO);
        }
        if (addressVerificationRequest.getParams() == null) {
            AddressVerificationRequest.Params params = new AddressVerificationRequest.Params();
            params.setMaxRows(BigInteger.ONE);
            params.setSearchLanguage(BigInteger.ONE);
            params.setSearchType(BigInteger.ONE);
            addressVerificationRequest.setParams(params);
        }
    }
}
