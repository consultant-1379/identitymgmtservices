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
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.cryptography.CryptographyService

class PasswordHelperTest extends CdiSpecification {
    private static final String CLEAR_TEXT= "marameo@123_despicable42"
    private static final String ENCRYPTED_TEXT = "xyz@!x"

    private static final String CLEAR_TEXT_FOR_ENCRYPTED_TEXT_EMPTY = "acbd"
    private static final String ENCRYPTED_TEXT_EMPTY = ""

    @ObjectUnderTest
    PasswordHelper passwordHelper

    @ImplementationInstance
    CryptographyService cryptographyServiceBean = [
            encrypt: { var1 ->
                return ENCRYPTED_TEXT.getBytes()
            },
            decrypt: { var1 ->
                return CLEAR_TEXT.getBytes()
            }
    ] as CryptographyService

    @ImplementationInstance
    CryptographyService cryptographyServiceSpecialTextBean = [
            encrypt: { var1 ->
                return ENCRYPTED_TEXT_EMPTY.getBytes()
            },
            decrypt: { var1 ->
                return CLEAR_TEXT_FOR_ENCRYPTED_TEXT_EMPTY.getBytes();
            }
    ] as CryptographyService

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {
    }

    def setup() {
        passwordHelper.cryptographyService = cryptographyServiceBean
        System.setProperty("configuration.java.properties", "src/test/resources/global.properties")
    }

    def 'verify encryption/decryption process is invertible '() {
        when:
            String encrypted = passwordHelper.encryptEncode(CLEAR_TEXT)
            String decrypted = passwordHelper.decryptDecode(encrypted)
        then:
            decrypted == CLEAR_TEXT
    }

    def 'verify encryption of null text return null '() {
        when:
            String encrypted = passwordHelper.encryptEncode(null)
        then:
            encrypted == null
    }

    def 'verify encoded normal password is not converted and decrypted '() {
        when:
            String decrypted = passwordHelper.getPassword(ENCRYPTED_TEXT)
        then:
            decrypted == CLEAR_TEXT
    }

    def 'verify encoded password "[]" is converted to empty text and decrypted '() {
        given:
            passwordHelper.cryptographyService = cryptographyServiceSpecialTextBean
        when:
            String decrypted = passwordHelper.getPassword(passwordHelper.PIB_DEF_EMPTY)
        then:
            decrypted == CLEAR_TEXT_FOR_ENCRYPTED_TEXT_EMPTY
    }

    def 'verify encoded password "NULL" is converted to empty text and decrypted '() {
        given:
            passwordHelper.cryptographyService = cryptographyServiceSpecialTextBean
        when:
            String decrypted = passwordHelper.getPassword(passwordHelper.PIB_REDEFINE_EMPTY)
        then:
            decrypted == CLEAR_TEXT_FOR_ENCRYPTED_TEXT_EMPTY
    }
}
