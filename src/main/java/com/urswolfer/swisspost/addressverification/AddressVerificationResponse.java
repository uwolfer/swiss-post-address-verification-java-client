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

import static java.math.BigInteger.ONE;

import ch.post.adresscheckerextern.v4_02_00.AdressCheckerResponseType;

import java.math.BigInteger;

/**
 * This is just a wrapper around the generated class which allows to keep better
 * source compatibility on case of binding updates and also hides the not-so-nice
 * package name and semi-translated class name.
 */
public class AddressVerificationResponse extends AdressCheckerResponseType {

    public enum MatchUniqueness {
        NO, UNIQUE, MULTIPLE
    }

    public enum MatchType {
        NONE, EXACT, SIMILAR, ABBREVIATIONS
    }

    public enum MatchHistoric {
        NONE, HISTORICAL, ACTIVE
    }

    public enum GuaranteedDelivery {
        DELIVERABLE, NOT_GUARANTEED, NON_DELIVERABLE
    }

    public enum ZipType {
        DOMICILE_OR_PO_BOX, HOME, PO_BOX, COMPANY_ADDRESS, UNKNOWN
    }

    public enum ZipLang {
        GERMAN, FRENCH, ITALIAN
    }

    public TypedResult getSingleResult() {
        return new TypedResult(getRows().get(0));
    }

    public class TypedResult {
        private MatchUniqueness matchUniqueness;
        private MatchType matchType;
        private MatchHistoric matchHistoric;
        private GuaranteedDelivery guaranteedDelivery;
        private Boolean hasPbox;
        private BigInteger houseKey;
        private String street;
        private String streetFormatted;
        private String houseNbr;
        private Boolean deliveryPoint;
        private Boolean adressOfficial;
        private Boolean streetValid;
        private Boolean houseNbrValid;
        private BigInteger deliveryPointHouseKey;
        private BigInteger onrp;
        private String zip;
        private String town18;
        private String town27;
        private ZipType zipType;
        private ZipLang zipLang;
        private Boolean zipValid;
        private Boolean townValid;
        private Boolean streetOfficial;

        TypedResult(Rows row) {
            matchUniqueness = convertMatchUniqueness(row.getMatchUniqueness());
            matchType = convertMatchType(row.getMatchType());
            matchHistoric = convertMatchHistoric(row.getMatchHistoric());
            guaranteedDelivery = convertGuaranteedDelivery(row.getGuaranteedDelivery());
            zipType = convertZipType(row.getZipType());
            zipLang = convertZipLang(row.getZipLang());
            hasPbox = toBoolean(row.getHasPbox());
            houseKey = row.getHouseKey();
            street = row.getStreet();
            streetFormatted = row.getStreetFormatted();
            houseNbr = row.getHouseNbr();
            deliveryPoint = toBoolean(row.getDeliveryPoint());
            adressOfficial = toBoolean(row.getAdressOfficial());
            streetValid = toBoolean(row.getStreetValid());
            houseNbrValid = toBoolean(row.getHouseNbrValid());
            deliveryPointHouseKey = row.getDeliveryPointHouseKey();
            onrp = row.getOnrp();
            zip = row.getZip();
            town18 = row.getTown18();
            town27 = row.getTown27();
            zipValid = toBoolean(row.getZipValid());
            townValid = toBoolean(row.getTownValid());
            streetOfficial = toBoolean(row.getStreetOfficial());
        }

        public MatchUniqueness getMatchUniqueness() {
            return matchUniqueness;
        }

        public MatchType getMatchType() {
            return matchType;
        }

        public MatchHistoric getMatchHistoric() {
            return matchHistoric;
        }

        public GuaranteedDelivery getGuaranteedDelivery() {
            return guaranteedDelivery;
        }

        public Boolean getHasPbox() {
            return hasPbox;
        }

        public BigInteger getHouseKey() {
            return houseKey;
        }

        public String getStreet() {
            return street;
        }

        public String getStreetFormatted() {
            return streetFormatted;
        }

        public String getHouseNbr() {
            return houseNbr;
        }

        public Boolean getDeliveryPoint() {
            return deliveryPoint;
        }

        public Boolean getAdressOfficial() {
            return adressOfficial;
        }

        public Boolean getStreetValid() {
            return streetValid;
        }

        public Boolean getHouseNbrValid() {
            return houseNbrValid;
        }

        public BigInteger getDeliveryPointHouseKey() {
            return deliveryPointHouseKey;
        }

        public BigInteger getOnrp() {
            return onrp;
        }

        public String getZip() {
            return zip;
        }

        public String getTown18() {
            return town18;
        }

        public String getTown27() {
            return town27;
        }

        public ZipType getZipType() {
            return zipType;
        }

        public ZipLang getZipLang() {
            return zipLang;
        }

        public Boolean getZipValid() {
            return zipValid;
        }

        public Boolean getTownValid() {
            return townValid;
        }

        public Boolean getStreetOfficial() {
            return streetOfficial;
        }

        private MatchUniqueness convertMatchUniqueness(BigInteger matchUniqueness) {
            switch (matchUniqueness.intValue()) {
                case 0:
                    return MatchUniqueness.NO;
                case 1:
                    return MatchUniqueness.UNIQUE;
                case 2:
                    return MatchUniqueness.MULTIPLE;
            }
            return null;
        }

        private MatchType convertMatchType(BigInteger matchType) {
            switch (matchType.intValue()) {
                case 0:
                    return MatchType.NONE;
                case 1:
                    return MatchType.EXACT;
                case 2:
                    return MatchType.SIMILAR;
                case 3:
                    return MatchType.ABBREVIATIONS;
            }
            return null;
        }

        private MatchHistoric convertMatchHistoric(BigInteger matchHistoric) {
            switch (matchHistoric.intValue()) {
                case 0:
                    return MatchHistoric.NONE;
                case 1:
                    return MatchHistoric.HISTORICAL;
                case 2:
                    return MatchHistoric.ACTIVE;
            }
            return null;
        }

        private GuaranteedDelivery convertGuaranteedDelivery(BigInteger guaranteedDelivery) {
            switch (guaranteedDelivery.intValue()) {
                case 1:
                    return GuaranteedDelivery.DELIVERABLE;
                case 2:
                    return GuaranteedDelivery.NOT_GUARANTEED;
                case 3:
                    return GuaranteedDelivery.NON_DELIVERABLE;
            }
            return null;
        }

        private ZipType convertZipType(BigInteger zipType) {
            if (zipType != null) {
                switch (zipType.intValue()) {
                    case 10:
                        return ZipType.DOMICILE_OR_PO_BOX;
                    case 20:
                        return ZipType.HOME;
                    case 30:
                        return ZipType.PO_BOX;
                    case 40:
                        return ZipType.COMPANY_ADDRESS;
                    case 50:
                        return ZipType.UNKNOWN;
                }
            }
            return null;
        }

        private ZipLang convertZipLang(BigInteger zipLang) {
            if (zipLang != null) {
                switch (zipLang.intValue()) {
                    case 1:
                        return ZipLang.GERMAN;
                    case 2:
                        return ZipLang.FRENCH;
                    case 3:
                        return ZipLang.ITALIAN;
                }
            }
            return null;
        }

        private Boolean toBoolean(BigInteger number) {
            return ONE.equals(number);
        }
    }
}
