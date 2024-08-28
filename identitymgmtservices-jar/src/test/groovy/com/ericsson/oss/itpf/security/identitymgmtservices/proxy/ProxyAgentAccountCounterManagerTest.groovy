package com.ericsson.oss.itpf.security.identitymgmtservices.proxy

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.proxyaccount.ProxyAgentAccountCounterManager

class ProxyAgentAccountCounterManagerTest extends  CdiSpecification{
    def proxyAccountSubtreeDnTest = "ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
    def proxyAccountSubtreeDnLockableTest = "ou=proxyagentlockable,ou=com,dc=ieatlms5223,dc=com"

    def proxyAccountSubtreeDnValue = 15
    def proxyAccountSubtreeDnRequestedValue = 10

    def proxyAccountSubtreeDnList = new ArrayList()

    def setup() {
        proxyAccountSubtreeDnList.add(proxyAccountSubtreeDnTest)
        proxyAccountSubtreeDnList.add(proxyAccountSubtreeDnLockableTest)
    }

    def "initialize and test default parameters" () {
        given:
        ProxyAgentAccountCounterManager proxyAgentAccountCounterManager = new ProxyAgentAccountCounterManager()
        when:
        proxyAgentAccountCounterManager.initialize(proxyAccountSubtreeDnList)
        then:
        proxyAgentAccountCounterManager.proxyAccountCounterMap.get(proxyAccountSubtreeDnTest).getLegacySubtree() == true &&
                proxyAgentAccountCounterManager.proxyAccountCounterMap.get(proxyAccountSubtreeDnLockableTest).getLegacySubtree() == false
    }

    def "update parameters" () {
        given:
        ProxyAgentAccountCounterManager proxyAgentAccountCounterManager = new ProxyAgentAccountCounterManager()
        proxyAgentAccountCounterManager.initialize(proxyAccountSubtreeDnList)
        when:
        proxyAgentAccountCounterManager.updateTotalSubtreeEntries(proxyAccountSubtreeDnTest, proxyAccountSubtreeDnValue)
        proxyAgentAccountCounterManager.updateRequestedSubtreeEntries(proxyAccountSubtreeDnTest, proxyAccountSubtreeDnRequestedValue)

        proxyAgentAccountCounterManager.updateProxyAccountCounters()
        proxyAgentAccountCounterManager.updateProxyAccountRequestedCounters()

        proxyAgentAccountCounterManager.toString()
        then:
        proxyAgentAccountCounterManager.proxyAccountCounterMap.get(proxyAccountSubtreeDnTest).getLegacySubtree() == true &&
                proxyAgentAccountCounterManager.proxyAccountCounterMap.get(proxyAccountSubtreeDnLockableTest).getLegacySubtree() == false &&
                proxyAgentAccountCounterManager.getNumOfTotalProxyAccountEntries() == proxyAccountSubtreeDnValue &&
                proxyAgentAccountCounterManager.getNumOfTotalProxyAccountLegacyEntries() == proxyAccountSubtreeDnValue &&
                proxyAgentAccountCounterManager.getNumOfTotalRequestedProxyAccountEntries() == proxyAccountSubtreeDnRequestedValue &&
                proxyAgentAccountCounterManager.getNumOfTotalRequestedProxyAccountLegacyEntries() == proxyAccountSubtreeDnRequestedValue
    }
}
