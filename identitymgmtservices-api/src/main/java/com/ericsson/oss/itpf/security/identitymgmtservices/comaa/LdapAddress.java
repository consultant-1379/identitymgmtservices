/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.comaa;

/**
 * Now used by :
 *      identitymgmt-services
 *      node-security
 *      ap-workflow-ecim
 */

import java.io.Serializable;

public class LdapAddress implements Serializable {

    private static final long serialVersionUID = -1416408077815021143L;

    private String primary;
    private String fallback;

    public LdapAddress(final String primary, final String fallback) {
        super();
        this.primary = primary;
        this.fallback = fallback;
    }

    public LdapAddress() {
        super();
    }

    public String getPrimary() {
        return primary;
    }

    public String getFallback() {
        return fallback;
    }

}
