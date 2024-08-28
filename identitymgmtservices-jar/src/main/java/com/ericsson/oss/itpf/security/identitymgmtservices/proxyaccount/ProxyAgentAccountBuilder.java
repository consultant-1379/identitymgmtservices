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

import java.util.ArrayList;
import java.util.List;

public class ProxyAgentAccountBuilder {
    private List<String> proxyAccountSubtrees = new ArrayList<>();
    private List<String> proxyAccountSubtreesActive = new ArrayList<>();
    private Boolean skipProxyAccountGetOperation;

    public ProxyAgentAccountBuilder(Boolean isLegacy, Boolean isSummary, List<String> proxyAgentAccountSubtreeList) {
        proxyAccountSubtreesActive.addAll(proxyAgentAccountSubtreeList);
        if (!isLegacy ) {
            proxyAccountSubtrees.addAll(proxyAgentAccountSubtreeList);
        } else {
            for (String proxyAgentAccountSubtree : proxyAgentAccountSubtreeList) {
                if (proxyAgentAccountSubtree.contains("ou=proxyagent,ou=com")) {
                    proxyAccountSubtrees.add(proxyAgentAccountSubtree);
                }
            }
        }
        this.skipProxyAccountGetOperation = isSummary;
    }

    public List<String> getProxyAccountSubtrees() {
        return new ArrayList<>(proxyAccountSubtrees);
    }
    public List<String> getProxyAccountSubtreesActive() {
        return new ArrayList<>(proxyAccountSubtreesActive);
    }
    public Boolean getSkipProxyAccountGetOperation() {
        return skipProxyAccountGetOperation;
    }

    @Override
    public String toString() {
        return "ProxyAgentAccountBuilder{" +
                "proxyAccountSubtrees=" + proxyAccountSubtrees.toString() +
                ", skipProxyAccountGetOperation=" + skipProxyAccountGetOperation +
                '}';
    }
}
