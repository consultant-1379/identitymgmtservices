/*------------------------------------------------------------------------------
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
package com.ericsson.oss.itpf.security.identitymgmtservices;

import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus;

import java.io.Serializable;

public class ProxyAgentAccountDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    private String userDn;
    private Long createTimestamp;
    private Long lastLoginTime;
    private ProxyAgentAccountAdminStatus adminStatus;

    public ProxyAgentAccountDetails() {
        // Do nothing, set and get method are used instead
    }

    public String getUserDn() { return userDn; }
    public void setUserDn(String userDn) {
        this.userDn = userDn;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }
    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Long getLastLoginTime() {
        return lastLoginTime;
    }
    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public ProxyAgentAccountAdminStatus getAdminStatus() {
        return adminStatus;
    }
    public void setAdminStatus(ProxyAgentAccountAdminStatus adminStatus) {
        this.adminStatus = adminStatus;
    }
}
