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

/**
 * This class defines the IdentityManagementException class.
 *
 * @author TOR/ENM Identity Management/Access Control
 * @version 1.0
 *
 * Note that this exception may be wrapped inside an EJBException under certain circumstances; eg: When a method which throws this exception,
 * is invoked remotely.
 *
 * Now used by :
 *      identitymgmt-services
 *      smrs.service
 *      ap-workflow-ecim-ear
 *      ap-workflow-macro
 *      nodecmd-job-service
 *      node-security-ear
 *      pmul-service
 *      nodecmd-admin-service
 *      network-model-retriever-tool
 *      generic-identity-mgmt-services
 */

public class IdentityManagementServiceException extends RuntimeException {

    private static final long serialVersionUID = 3999846618720463038L;

    /**
     * This enumeration holds the standardized error types, to ensure that the client applications are always presented with the same set of error
     * details
     */
    public enum Error {

        NO_SUCH_ATTRIBUTE("No such attribute in datastore"),
        INVALID_CREDENTIALS("Invalid credentials for accessing datastore"),
        ENTRY_NOT_FOUND("No such entry found in datastore"),
        ENTRY_ALREADY_EXISTS("Entry already exists in datastore"),
        ATTR_OR_VALUE_ALREADY_EXISTS("Attribute or value already exists in datastore"),
        DATA_STORE_CONNECTION_FAILURE("Failed to connect to datastore"),
        ATTRIBUTE_NOT_AVAILABLE("Attribute is not available"),
        UNEXPECTED_ERROR("Unexpected or unknown error encountered");

        private final String description;

        Error(final String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return this.description;
        }
    }

    /**
     * Private field to hold the value of the Error enum (with default value specified).
     */
    private final Error error;

    /*
     * Available constructors
     */
    public IdentityManagementServiceException() {
        super();
        this.error = Error.UNEXPECTED_ERROR;
    }

    public IdentityManagementServiceException(final Throwable ex) {
        super(ex);
        this.error = Error.UNEXPECTED_ERROR;
    }

    public IdentityManagementServiceException(final String msg) {
        super(msg);
        this.error = Error.UNEXPECTED_ERROR;
    }

    public IdentityManagementServiceException(final String msg, final Error error) {
        super(msg);
        this.error = error;
    }

    public IdentityManagementServiceException(final String msg, final Throwable ex, final Error error) {
        super(msg, ex);
        this.error = error;
    }

    public Error getError() {
        return this.error;
    }

    @Override
    public String toString() {
        return this.error.toString() + ": " + super.toString();
    }

}
