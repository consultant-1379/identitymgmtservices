/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount;

import java.io.Serializable;

public class IdmsConfigGetProxyAccountByInactivityDateDto extends  IdmsConfigGetProxyAccountBaseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String inactivityDate;

    public IdmsConfigGetProxyAccountByInactivityDateDto() {}

    public IdmsConfigGetProxyAccountByInactivityDateDto(String isLegacy, String isSummary, String inactivityDate ) {
        super(isLegacy, isSummary);
        this.inactivityDate = inactivityDate;
    }

    public String getInactivityDate() {
        return inactivityDate;
    }

    public void setInactivityDate(String inactivityDate) {
        this.inactivityDate = inactivityDate;
    }
}
