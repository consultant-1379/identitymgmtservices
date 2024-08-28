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

import java.io.Serializable;

public class ProxyAgentAccountCounters implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer numOfProxyAccount;
    private Integer numOfRequestedProxyAccount;
    private Integer numOfProxyAccountLegacy;
    private Integer numOfRequestedProxyAccountLegacy;

    public ProxyAgentAccountCounters() {
        // Do nothing, set and get method are used instead
    }

    public Integer getNumOfProxyAccount() {
        return numOfProxyAccount;
    }
    public void setNumOfProxyAccount(Integer numOfProxyAccount) {
        this.numOfProxyAccount = numOfProxyAccount;
    }

    public Integer getNumOfRequestedProxyAccount() {
        return numOfRequestedProxyAccount;
    }
    public void setNumOfRequestedProxyAccount(Integer numOfRequestedProxyAccount) {
        this.numOfRequestedProxyAccount = numOfRequestedProxyAccount;
    }

    public Integer getNumOfProxyAccountLegacy() {
        return numOfProxyAccountLegacy;
    }
    public void setNumOfProxyAccountLegacy(Integer numOfProxyAccountLegacy) {
        this.numOfProxyAccountLegacy = numOfProxyAccountLegacy;
    }

    public Integer getNumOfRequestedProxyAccountLegacy() {
        return numOfRequestedProxyAccountLegacy;
    }
    public void setNumOfRequestedProxyAccountLegacy(Integer numOfRequestedProxyAccountLegacy) {
        this.numOfRequestedProxyAccountLegacy = numOfRequestedProxyAccountLegacy;
    }
}
