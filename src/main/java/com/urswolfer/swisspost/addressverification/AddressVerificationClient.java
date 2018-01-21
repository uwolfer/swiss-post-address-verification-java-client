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
                return parseSoapResponse(soapResponse);
            } finally {
                soapConnection.close();
            }
        } catch (SOAPException | JAXBException e) {
            throw new AddressVerificationClientException(e);
        }
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
