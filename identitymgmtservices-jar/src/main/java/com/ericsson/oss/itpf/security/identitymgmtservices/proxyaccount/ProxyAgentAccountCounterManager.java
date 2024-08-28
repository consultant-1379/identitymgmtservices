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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProxyAgentAccountCounterManager {
    private static final Logger logger = LoggerFactory.getLogger(ProxyAgentAccountCounterManager.class);
    private final Map<String, ProxyAgentAccountSubtreeCounters> proxyAccountCounterMap = new HashMap<>();

    private Integer numOfTotalProxyAccountEntries = 0;
    private Integer numOfTotalProxyAccountLegacyEntries = 0;
    private Integer numOfTotalRequestedProxyAccountEntries = 0;
    private Integer numOfTotalRequestedProxyAccountLegacyEntries = 0;

    public Integer getNumOfTotalProxyAccountEntries() {
        return numOfTotalProxyAccountEntries;
    }
    public Integer getNumOfTotalProxyAccountLegacyEntries() {
        return numOfTotalProxyAccountLegacyEntries;
    }
    public Integer getNumOfTotalRequestedProxyAccountEntries() {
        return numOfTotalRequestedProxyAccountEntries;
    }
    public Integer getNumOfTotalRequestedProxyAccountLegacyEntries() {
        return numOfTotalRequestedProxyAccountLegacyEntries;
    }

    @Override
    public String toString() {
        return "ProxyAgentAccountCounterManager{" +
                "numOfTotalProxyAccountEntries=" + numOfTotalProxyAccountEntries +
                ", numOfTotalProxyAccountLegacyEntries=" + numOfTotalProxyAccountLegacyEntries +
                ", numOfTotalRequestedProxyAccountEntries=" + numOfTotalRequestedProxyAccountEntries +
                ", numOfTotalRequestedProxyAccountLegacyEntries=" + numOfTotalRequestedProxyAccountLegacyEntries +
                '}';
    }

    public void initialize (List<String> proxyAccountSubtreesDn) {
        for (String proxyAccountSubtreeDn : proxyAccountSubtreesDn) {
            ProxyAgentAccountSubtreeCounters proxyAgentAccountSubtreeCounters = new ProxyAgentAccountSubtreeCounters();
            proxyAgentAccountSubtreeCounters.setProxyAccountSubtreeDn(proxyAccountSubtreeDn);
            proxyAgentAccountSubtreeCounters.setLegacySubtree(proxyAccountSubtreeDn.contains("ou=proxyagent,ou=com"));
            proxyAgentAccountSubtreeCounters.setNumOfTotalSubtreeEntries(0);
            proxyAgentAccountSubtreeCounters.setNumOfRequestedSubtreeEntries(0);

            proxyAccountCounterMap.put(proxyAccountSubtreeDn,proxyAgentAccountSubtreeCounters);
        }
    }

    public void updateTotalSubtreeEntries( String proxyAccountSubtreeDn, Integer counterValue) {
        logger.debug("Update Subtree Total Counter: subtree=[{}], value=[{}] ", proxyAccountSubtreeDn, counterValue);
        proxyAccountCounterMap.get(proxyAccountSubtreeDn).setNumOfTotalSubtreeEntries(counterValue);
    }

    public void updateRequestedSubtreeEntries( String proxyAccountSubtreeDn, Integer counterValue) {
        logger.debug("Update Subtree Requested Counter: proxyAccountSubtreeDn=[{}], value=[{}]", proxyAccountSubtreeDn, counterValue);
        proxyAccountCounterMap.get(proxyAccountSubtreeDn).setNumOfRequestedSubtreeEntries(counterValue);
    }

    public void updateProxyAccountCounters( ) {
        logger.debug("Start Calculate cumulative global counters");
        for (Map.Entry<String,ProxyAgentAccountSubtreeCounters> entry : proxyAccountCounterMap.entrySet()) {
            String key = entry.getKey();
            if(proxyAccountCounterMap.get(key).getLegacySubtree()) {
                numOfTotalProxyAccountLegacyEntries = proxyAccountCounterMap.get(key).getNumOfTotalSubtreeEntries();
            }
            numOfTotalProxyAccountEntries += proxyAccountCounterMap.get(key).getNumOfTotalSubtreeEntries();
        }
        logger.debug("End Calculate cumulative global counters: totalEntries=[{}], totalLegacyEntries[{}]",
                numOfTotalProxyAccountEntries,numOfTotalProxyAccountLegacyEntries);
    }

    public void updateProxyAccountRequestedCounters() {
        logger.debug("Start Calculate cumulative requested counters");
        for (Map.Entry<String,ProxyAgentAccountSubtreeCounters> entry : proxyAccountCounterMap.entrySet()) {
            String key = entry.getKey();
            if(proxyAccountCounterMap.get(key).getLegacySubtree()) {
                numOfTotalRequestedProxyAccountLegacyEntries = proxyAccountCounterMap.get(key).getNumOfRequestedSubtreeEntries();
            }
            numOfTotalRequestedProxyAccountEntries += proxyAccountCounterMap.get(key).getNumOfRequestedSubtreeEntries();
        }
        logger.debug("End Calculate cumulative requested counters: RequestedEntries=[{}], RequestedLegacyEntries=[{}]",
                numOfTotalRequestedProxyAccountEntries,numOfTotalRequestedProxyAccountLegacyEntries);
    }
}
