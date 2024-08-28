package com.ericsson.oss.itpf.security.identitymgmtservices.proxy

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.proxyaccount.ProxyAgentAccountBuilder
import spock.lang.Unroll

class ProxyAgentAccountBuilderTest extends  CdiSpecification {
    def proxyAccountSubtreeDnTest = "ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
    def proxyAccountSubtreeDnLockableTest = "ou=proxyagentlockable,ou=com,dc=ieatlms5223,dc=com"

    def proxyAccountSubtreeDnList = new ArrayList()

    def setup() {
        proxyAccountSubtreeDnList.add(proxyAccountSubtreeDnTest)
        proxyAccountSubtreeDnList.add(proxyAccountSubtreeDnLockableTest)
    }
    @Unroll
    def "constructor with args" () {
        given:
        when:
        ProxyAgentAccountBuilder proxyAgentAccountBuilder = new ProxyAgentAccountBuilder(isLegacy, false, proxyAccountSubtreeDnList)
        proxyAgentAccountBuilder.toString()
        then:
        proxyAgentAccountBuilder.getProxyAccountSubtreesActive().size() == activeSubtreeActive &&
                proxyAgentAccountBuilder.getProxyAccountSubtrees().size == accountSubtree &&
                proxyAgentAccountBuilder.getSkipProxyAccountGetOperation() == false
        where:
        isLegacy | activeSubtreeActive | accountSubtree
        false    | 2                   | 2
        true     | 2                   | 1
    }
}
