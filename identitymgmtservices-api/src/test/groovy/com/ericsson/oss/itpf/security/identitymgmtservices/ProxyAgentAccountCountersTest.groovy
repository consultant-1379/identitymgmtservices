package com.ericsson.oss.itpf.security.identitymgmtservices

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class ProxyAgentAccountCountersTest extends CdiSpecification {
    def numOfProxyAccountTest = 15
    def numOfRequestedProxyAccountTest = 10
    def numOfProxyAccountLegacy = 9
    def numOfRequestedProxyAccountLegacy = 5

    def "set parameters " () {
        given:
        ProxyAgentAccountCounters proxyAgentAccountCounters = new ProxyAgentAccountCounters()
        when:
        proxyAgentAccountCounters.setNumOfProxyAccount(numOfProxyAccountTest)
        proxyAgentAccountCounters.setNumOfRequestedProxyAccount(numOfRequestedProxyAccountTest)
        proxyAgentAccountCounters.setNumOfProxyAccountLegacy(numOfProxyAccountLegacy)
        proxyAgentAccountCounters.setNumOfRequestedProxyAccountLegacy(numOfRequestedProxyAccountLegacy)
        then:
        proxyAgentAccountCounters.getNumOfProxyAccount() ==  numOfProxyAccountTest &&
                proxyAgentAccountCounters.getNumOfRequestedProxyAccount()== numOfRequestedProxyAccountTest &&
                proxyAgentAccountCounters.getNumOfProxyAccountLegacy() == numOfProxyAccountLegacy &&
                proxyAgentAccountCounters.getNumOfRequestedProxyAccountLegacy() == numOfRequestedProxyAccountLegacy
    }
}
