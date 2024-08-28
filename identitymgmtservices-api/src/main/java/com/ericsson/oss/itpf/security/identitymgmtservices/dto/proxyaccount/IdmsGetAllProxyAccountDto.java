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

public class IdmsGetAllProxyAccountDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String idmsUserDn;
    private String idmsCreateTimestamp;
    private String idmsLastLoginTime;
    private String idmsAccountDisabled;

    public String getIdmsUserDn() {
        return idmsUserDn;
    }
    public void setIdmsUserDn(String idmsUserDn) {
        this.idmsUserDn = idmsUserDn;
    }

    public String getIdmsCreateTimestamp() {
        return idmsCreateTimestamp;
    }
    public void setIdmsCreateTimestamp(String idmsCreateTimestamp) {
        this.idmsCreateTimestamp = idmsCreateTimestamp;
    }

    public String getIdmsLastLoginTime() {
        return idmsLastLoginTime;
    }
    public void setIdmsLastLoginTime(String idmsLastLoginTime) {
        this.idmsLastLoginTime = idmsLastLoginTime;
    }

    public String getIdmsAccountDisabled() {
        return idmsAccountDisabled;
    }
    public void setIdmsAccountDisabled(String idmsAccountDisabled) {
        this.idmsAccountDisabled = idmsAccountDisabled;
    }
}
