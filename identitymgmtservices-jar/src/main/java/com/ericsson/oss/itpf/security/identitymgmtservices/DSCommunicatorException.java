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
package com.ericsson.oss.itpf.security.identitymgmtservices;

public class DSCommunicatorException extends Exception {
    /**
	 *
	 */
    private static final long serialVersionUID = 7135596857630758549L;
    private final int ldapErrorCode;
    private final String message;

    public DSCommunicatorException(final String msg, final int returnCode) {
        super(msg);
        message = msg;
        ldapErrorCode = returnCode;
    }

    public DSCommunicatorException(final String msg, final int returnCode, final Throwable arg0) {
        super(msg, arg0);
        message = msg;
        ldapErrorCode = returnCode;
    }

    public int ldapErrorCode() {
        return ldapErrorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
