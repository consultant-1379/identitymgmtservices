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
package com.ericsson.oss.itpf.security.identitymgmtservices.proxyaccount;

import java.io.Serializable;

public class ProxyAgentAccountSubtreeCounters implements Serializable {
    private static final long serialVersionUID = 1L;
    private String proxyAccountSubtreeDn;
    private Boolean isLegacySubtree;
    private Integer numOfTotalSubtreeEntries;
    private Integer numOfRequestedSubtreeEntries;

    public ProxyAgentAccountSubtreeCounters() {
        // Do nothing, set and get method are used instead
    }

    public String getProxyAccountSubtreeDn() {
        return proxyAccountSubtreeDn;
    }
    public void setProxyAccountSubtreeDn(String proxyAccountSubtreeDn) {
        this.proxyAccountSubtreeDn = proxyAccountSubtreeDn;
    }

    public Boolean getLegacySubtree() {
        return isLegacySubtree;
    }
    public void setLegacySubtree(Boolean legacySubtree) {
        isLegacySubtree = legacySubtree;
    }

    public Integer getNumOfTotalSubtreeEntries() {
        return numOfTotalSubtreeEntries;
    }
    public void setNumOfTotalSubtreeEntries(Integer numOfTotalSubtreeEntries) {
        this.numOfTotalSubtreeEntries = numOfTotalSubtreeEntries;
    }

    public Integer getNumOfRequestedSubtreeEntries() {
        return numOfRequestedSubtreeEntries;
    }
    public void setNumOfRequestedSubtreeEntries(Integer numOfRequestedSubtreeEntries) {
        this.numOfRequestedSubtreeEntries = numOfRequestedSubtreeEntries;
    }

    @Override
    public String toString() {
        return "ProxyAgentAccountSubtreeCounters{" +
                "proxyAccountSubtreeDn='" + proxyAccountSubtreeDn + '\'' +
                ", isLegacySubtree=" + isLegacySubtree +
                ", numOfTotalSubtreeEntries=" + numOfTotalSubtreeEntries +
                ", numOfRequestedSubtreeEntries=" + numOfRequestedSubtreeEntries +
                '}';
    }
}
