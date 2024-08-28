package com.ericsson.oss.itpf.security.identitymgmtservices.proxy

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus
import com.ericsson.oss.itpf.security.identitymgmtservices.proxyaccount.ProxyAgentAccountGetDataManager

class ProxyAgentAccountGetDataManagerTest extends  CdiSpecification {

    def userDnTest = "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
    def adminStatusTest = "false"
    // format is "yyMMddHHmmss"
    def createTimestampTest = "20230901170530"
    def lastLoginTimeTest = "20230901180530"

    def createTimestampWrongTest = "202309011705"
    def lastLoginTimeWrongTest = "202309011805"

    def proxyAccountSubtreeDnTest = "ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
    def proxyAccountSubtreeDnLockableTest = "ou=proxyagentlockable,ou=com,dc=ieatlms5223,dc=com"
    def proxyAgentAccountSubtreeList = new ArrayList()
    def isLegacy = false
    def isSummary = false

    def setup() {
        proxyAgentAccountSubtreeList.add(proxyAccountSubtreeDnTest)
        proxyAgentAccountSubtreeList.add(proxyAccountSubtreeDnLockableTest)
    }

    def "update details with known parameters" () {
        given:
        ProxyAgentAccountGetDataManager proxyAgentAccountGetDataManager = new ProxyAgentAccountGetDataManager()
        proxyAgentAccountGetDataManager.initializeProxyAgentAccountGetData(isLegacy, isSummary, proxyAgentAccountSubtreeList)
        when:
        proxyAgentAccountGetDataManager.updateProxyAgentAccountDetails(userDnTest, createTimestampTest, lastLoginTimeTest, adminStatusTest )
        then:
        def itemOfList = proxyAgentAccountGetDataManager.getProxyAgentAccountGetData().getProxyAgentAccountDetailsList().get(0)
        itemOfList.getUserDn() == userDnTest &&
                itemOfList.getAdminStatus() == ProxyAgentAccountAdminStatus.ENABLED
    }

    def "update details with null parameters" () {
        given:
        ProxyAgentAccountGetDataManager proxyAgentAccountGetDataManager = new ProxyAgentAccountGetDataManager()
        proxyAgentAccountGetDataManager.initializeProxyAgentAccountGetData(isLegacy, isSummary, proxyAgentAccountSubtreeList)
        when:
        proxyAgentAccountGetDataManager.updateProxyAgentAccountDetails(userDnTest, null, null, null )
        then:
        def itemOfList = proxyAgentAccountGetDataManager.getProxyAgentAccountGetData().getProxyAgentAccountDetailsList().get(0)
        itemOfList.getUserDn() == userDnTest &&
                itemOfList.getAdminStatus() == null &&
                itemOfList.getLastLoginTime() == null &&
                itemOfList.getCreateTimestamp() == null
    }

    def "update details with time parameters in wrong format" () {
        given:
        ProxyAgentAccountGetDataManager proxyAgentAccountGetDataManager = new ProxyAgentAccountGetDataManager()
        proxyAgentAccountGetDataManager.initializeProxyAgentAccountGetData(isLegacy, isSummary, proxyAgentAccountSubtreeList)
        when:
        proxyAgentAccountGetDataManager.updateProxyAgentAccountDetails(userDnTest, createTimestampWrongTest, lastLoginTimeWrongTest, adminStatusTest )
        then:
        def itemOfList = proxyAgentAccountGetDataManager.getProxyAgentAccountGetData().getProxyAgentAccountDetailsList().get(0)
        itemOfList.getUserDn() == userDnTest &&
                itemOfList.getAdminStatus() == ProxyAgentAccountAdminStatus.ENABLED &&
                itemOfList.getLastLoginTime() == null &&
                itemOfList.getCreateTimestamp() == null
    }

    def "update counters parameters" () {
        given:
        ProxyAgentAccountGetDataManager proxyAgentAccountGetDataManager = new ProxyAgentAccountGetDataManager()
        proxyAgentAccountGetDataManager.initializeProxyAgentAccountGetData(isLegacy, isSummary, proxyAgentAccountSubtreeList)
        when:
        proxyAgentAccountGetDataManager.updateTotalSubtreeEntries(proxyAccountSubtreeDnTest, 15)
        proxyAgentAccountGetDataManager.updateRequestedSubtreeEntries(proxyAccountSubtreeDnTest, 10)
        proxyAgentAccountGetDataManager.updateProxyAgentAccountRequestedCounters()
        proxyAgentAccountGetDataManager.updateProxyAgentAccountCounters()
        then:
        def itemOfCounters = proxyAgentAccountGetDataManager.getProxyAgentAccountGetData().getProxyAgentAccountCounters()
        itemOfCounters.getNumOfRequestedProxyAccount() == 10 &&
        itemOfCounters.getNumOfProxyAccount() == 15
    }
}
