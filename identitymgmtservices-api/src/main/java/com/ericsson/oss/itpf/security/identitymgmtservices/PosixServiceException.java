/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/package com.ericsson.oss.itpf.security.identitymgmtservices;

import javax.ejb.ApplicationException;

/**
 *
 * Check if possible to put in Deprecated status
 * Deprecated but used now by:
 *      identitymgmt-services
 *      generic-identity-mgmt-services
 */
@ApplicationException
public class PosixServiceException extends IdentityManagementServiceException {
    public PosixServiceException(final IdentityManagementServiceException e) {
        super(e.getMessage(), e.getCause(), e.getError());
    }
}
