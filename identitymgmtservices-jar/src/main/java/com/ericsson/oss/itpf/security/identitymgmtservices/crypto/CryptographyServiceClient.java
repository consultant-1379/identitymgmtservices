/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.itpf.security.identitymgmtservices.crypto;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.security.cryptography.CryptographyService;
import com.ericsson.oss.itpf.security.identitymgmtservices.IdmConstants;

/**
 *
 * This acts as a client in invoking the encryption/decryption APIs of CryptographyService.
 *
 */

public class CryptographyServiceClient {

    private final CryptographyService cryptographyService;

    private final Logger logger = LoggerFactory.getLogger(CryptographyServiceClient.class);

    public CryptographyServiceClient(final CryptographyService cryptographyService) {
        this.cryptographyService = cryptographyService;
    }

    /**
     * Encrypts the password using the cryptography service
     *
     * @param userPassword
     * @return base64 encoded encrypted string, in case of any exceptions from cryptography service, we return null
     */
    public String getEncryptedPassword(final String userPassword) {
        //call the cryptography API for encryption
        try {
            byte[] encryptedByteArray;
            final StringBuilder encryptedPassword = new StringBuilder();
            logger.debug("invoking the cryptography api for encrypting the password in getEncryptedPassword():");
            encryptedByteArray = cryptographyService.encrypt(DatatypeConverter.parseBase64Binary(userPassword));
            encryptedPassword.append(IdmConstants.LDAP_AUTH_CRED_SCHEME);
            encryptedPassword.append(IdmConstants.CIPHER_PREFIX_AES_CBC128);
            encryptedPassword.append(DatatypeConverter.printBase64Binary(encryptedByteArray));
            logger.debug("encryption using the cryptography api worked in getEncryptedPassword():");
            logger.debug("Encrypted password is:::: {}",encryptedPassword);
            return encryptedPassword.toString();
        } catch (final Exception e) {
            logger.error("getEncryptedPassword excp:",e);
            final StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            logger.error("Error encrypting the password, exception: {}",errors);
            return null;
        }
    }

    /**
     * Decrypts the password using the cryptography service
     *
     * @param encryptedPassword
     * @return decrypted string, in case of any exceptions from cryptography service, we return null
     */
    public String getDecryptedPassword(final String encryptedPassword) {
        try {
            logger.debug("invoking the cryptography api for decrypting the password in getDecryptedPassword():");
            final byte[] decryptedBytes = cryptographyService.decrypt(DatatypeConverter.parseBase64Binary(encryptedPassword));
            logger.debug("converting the bytes to string in getDecryptedPassword()");
            return DatatypeConverter.printBase64Binary(decryptedBytes);
        } catch (final Exception exp) {
            logger.error("getEncryptedPassword excp:",exp);
            final StringWriter errors = new StringWriter();
            exp.printStackTrace(new PrintWriter(errors));
            logger.error("Error decrypting the password, exception: {}",errors);
            return null;
        }
    }
}