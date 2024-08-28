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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProxyAgentAccountGetData implements Serializable {

    private static final long serialVersionUID = 1L;

    private ProxyAgentAccountCounters proxyAgentAccountCounters;
    private List<ProxyAgentAccountDetails> proxyAgentAccountDetailsList;

    public ProxyAgentAccountGetData() {
        // Do nothing, set and get method are used instead
    }

    public ProxyAgentAccountCounters getProxyAgentAccountCounters() {
        return proxyAgentAccountCounters;
    }

    public void setProxyAgentAccountCounters(ProxyAgentAccountCounters proxyAgentAccountCounters) {
        this.proxyAgentAccountCounters = proxyAgentAccountCounters;
    }

    public List<ProxyAgentAccountDetails> getProxyAgentAccountDetailsList() {
        if (proxyAgentAccountDetailsList == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(proxyAgentAccountDetailsList);
    }

    public void setProxyAgentAccountDetailsList(List<ProxyAgentAccountDetails> proxyAgentAccountDetailsList) {
        if (proxyAgentAccountDetailsList == null) {
            this.proxyAgentAccountDetailsList = null;
        } else {
            this.proxyAgentAccountDetailsList = new ArrayList<>(proxyAgentAccountDetailsList);
        }
    }

    public void addToList(ProxyAgentAccountDetails proxyAgentAccountDetails) {
        proxyAgentAccountDetailsList.add(proxyAgentAccountDetails);
    }
}
