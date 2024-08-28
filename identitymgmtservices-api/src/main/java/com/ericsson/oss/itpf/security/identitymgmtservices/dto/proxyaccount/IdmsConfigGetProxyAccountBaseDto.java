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

public class IdmsConfigGetProxyAccountBaseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String isLegacy;
    private String isSummary;

    public IdmsConfigGetProxyAccountBaseDto() {}

    public IdmsConfigGetProxyAccountBaseDto(String isLegacy, String isSummary ) {
        this.isLegacy = isLegacy;
        this.isSummary = isSummary;
    }

    public String getIsLegacy() {
        return isLegacy;
    }

    public void setIsLegacy(String isLegacy) {
        this.isLegacy = isLegacy;
    }

    public String getIsSummary() {
        return isSummary;
    }

    public void setIsSummary(String isSummary) {
        this.isSummary = isSummary;
    }

}
