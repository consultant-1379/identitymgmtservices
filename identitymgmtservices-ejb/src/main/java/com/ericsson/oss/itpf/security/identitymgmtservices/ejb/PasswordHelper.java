/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2020
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb;

import com.ericsson.oss.itpf.security.cryptography.CryptographyService;

import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

public class PasswordHelper {

    private static final String PIB_DEF_EMPTY = "[]";
    private static final String PIB_REDEFINE_EMPTY = "NULL";

    @Inject
    private CryptographyService cryptographyService;

    public String encryptEncode(final String text) {
        if (text == null) {
            return null;
        }
        return encode(encrypt(text));
    }

    public String decryptDecode(final String text) {
        return decrypt(decode(text));
    }

    public String getPassword(final String encoded) {
        final String converted = convertValue(encoded);
        return this.decryptDecode(converted);
    }

    private String encode(final byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    private byte[] encrypt(final String text) {
        return cryptographyService.encrypt(text.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] decode(final String value) {
        return DatatypeConverter.parseBase64Binary(value);
    }

    private String decrypt(final byte[] encryptedBytes) {
        return new String(cryptographyService.decrypt(encryptedBytes), StandardCharsets.UTF_8);
    }

    private String convertValue(final String str) {
        final boolean hasToConvert = str != null && (str.equals(PIB_DEF_EMPTY) || str.equals(PIB_REDEFINE_EMPTY));

        return hasToConvert ? "" : str;
    }

}
