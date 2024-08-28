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

public class IdmsConfigGetProxyAccountByAdminStatusDto extends  IdmsConfigGetProxyAccountBaseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String adminStatus;

    public IdmsConfigGetProxyAccountByAdminStatusDto() {}

    public IdmsConfigGetProxyAccountByAdminStatusDto(String isLegacy, String isSummary, String adminStatus ) {
        super(isLegacy, isSummary);
        this.adminStatus = adminStatus;
    }

    public String getAdminStatus() {
        return adminStatus;
    }

    public void setAdminStatus(String adminStatus) {
        this.adminStatus = adminStatus;
    }

}
