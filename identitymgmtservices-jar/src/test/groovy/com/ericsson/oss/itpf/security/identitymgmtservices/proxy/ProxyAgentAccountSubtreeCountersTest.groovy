package com.ericsson.oss.itpf.security.identitymgmtservices.proxy

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.proxyaccount.ProxyAgentAccountSubtreeCounters

class ProxyAgentAccountSubtreeCountersTest extends  CdiSpecification {
    def proxyAccountSubtreeDnTest = "ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
    def isLegacySubtreeTest = true
    def numOfTotalSubtreeEntriesTest = 10
    def numOfRequestedSubtreeEntriesTest = 5

    def formattedStr = "ProxyAgentAccountSubtreeCounters{" +
            "proxyAccountSubtreeDn='ou=proxyagent,ou=com,dc=ieatlms5223,dc=com', " +
            "isLegacySubtree=true, " +
            "numOfTotalSubtreeEntries=10, " +
            "numOfRequestedSubtreeEntries=5}"

    def "set parameters" () {
        given:
        ProxyAgentAccountSubtreeCounters proxyAgentAccountSubtreeCounters = new ProxyAgentAccountSubtreeCounters()
        when:
        proxyAgentAccountSubtreeCounters.setProxyAccountSubtreeDn(proxyAccountSubtreeDnTest)
        proxyAgentAccountSubtreeCounters.setLegacySubtree(isLegacySubtreeTest)
        proxyAgentAccountSubtreeCounters.setNumOfTotalSubtreeEntries(numOfTotalSubtreeEntriesTest)
        proxyAgentAccountSubtreeCounters.setNumOfRequestedSubtreeEntries(numOfRequestedSubtreeEntriesTest)
        then:
        proxyAgentAccountSubtreeCounters.getProxyAccountSubtreeDn() == proxyAccountSubtreeDnTest &&
                proxyAgentAccountSubtreeCounters.getLegacySubtree() == isLegacySubtreeTest &&
                proxyAgentAccountSubtreeCounters.getNumOfTotalSubtreeEntries() == numOfTotalSubtreeEntriesTest &&
                proxyAgentAccountSubtreeCounters.getNumOfRequestedSubtreeEntries() == numOfRequestedSubtreeEntriesTest &&
                proxyAgentAccountSubtreeCounters.toString() == formattedStr
    }
}
