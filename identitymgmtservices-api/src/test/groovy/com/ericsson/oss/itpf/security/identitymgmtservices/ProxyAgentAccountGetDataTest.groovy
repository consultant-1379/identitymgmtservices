package com.ericsson.oss.itpf.security.identitymgmtservices

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class ProxyAgentAccountGetDataTest extends CdiSpecification {

    def proxyAgentAccountCountersTest = new ProxyAgentAccountCounters()
    def proxyAgentAccountDetailsTest = new ProxyAgentAccountDetails()
    def proxyAgentAccountDetailsListTest = new ArrayList()

    def "set parameters " () {
        given:
        ProxyAgentAccountGetData proxyAgentAccountGetData = new ProxyAgentAccountGetData()
        proxyAgentAccountDetailsListTest.add(proxyAgentAccountDetailsTest)
        when:
        proxyAgentAccountGetData.setProxyAgentAccountCounters(proxyAgentAccountCountersTest)
        proxyAgentAccountGetData.setProxyAgentAccountDetailsList(proxyAgentAccountDetailsListTest)

        then:
        proxyAgentAccountGetData.getProxyAgentAccountCounters() ==  proxyAgentAccountCountersTest &&
                proxyAgentAccountGetData.getProxyAgentAccountDetailsList() == proxyAgentAccountDetailsListTest
    }

    def "set parameters with collection null " () {
        given:
        ProxyAgentAccountGetData proxyAgentAccountGetData = new ProxyAgentAccountGetData()
        def proxyAgentAccountDetailsListNullTest = null
        when:
        proxyAgentAccountGetData.setProxyAgentAccountCounters(proxyAgentAccountCountersTest)
        proxyAgentAccountGetData.setProxyAgentAccountDetailsList(proxyAgentAccountDetailsListNullTest)

        then:
        proxyAgentAccountGetData.getProxyAgentAccountCounters() ==  proxyAgentAccountCountersTest &&
                proxyAgentAccountGetData.getProxyAgentAccountDetailsList() == Collections.emptyList()
    }
}
