package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.statistics

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementService
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountCounters
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountGetData
import com.ericsson.oss.itpf.security.identitymgmtservices.ejb.IdentityManagementListener
import com.ericsson.oss.itpf.security.identitymgmtservices.ejb.cluster.IdentityManagementClusterMessageListener
import com.ericsson.oss.itpf.security.identitymgmtservices.ejb.timers.IdentityManagementTimerException
import com.ericsson.oss.itpf.security.identitymgmtservices.ejb.timers.IdentityManagementTimerManager

class IdentityManagementProxyAccountGlobalCountersTest extends CdiSpecification {
    private static final String TIMER_MANAGER_EXCP_TAG = "Timer manager exception raised"
    private static final String IDMS_GLB_CNTS_TEST_EXCP = "Identity Management Test exception"
    private static final int MAX_NUM_TOT_PROXY_ACCOUNT_ENTRIES = 80000

    @ObjectUnderTest
    ProxyAccountGlobalCountersManager idmsProxyAccountGlbCnts

    def numOfProxyAccount = 100

    @ImplementationInstance
    IdentityManagementTimerManager idmsTimerManagerMockExcp = [
            addTimer : { identityManagementTimerConfig ->
                def illegalStateException = new IllegalStateException(TIMER_MANAGER_EXCP_TAG)
                throw illegalStateException
            }
    ] as IdentityManagementTimerManager

    @ImplementationInstance
    IdentityManagementTimerManager idmsTimerManagerMock = [
            addTimer : {identityManagementTimerConfig ->
            }
    ] as IdentityManagementTimerManager

    @ImplementationInstance
    IdentityManagementClusterMessageListener idmsClusterMessageListenerMock = [
            isMaster : {
                return true
            }
    ] as IdentityManagementClusterMessageListener

    @ImplementationInstance
    IdentityManagementClusterMessageListener idmsClusterMessageListenerMockSlave = [
            isMaster : {
                return false
            }
    ] as IdentityManagementClusterMessageListener

    @ImplementationInstance
    IdentityManagementService identityManagementServiceMock = [
            getProxyAgentAccountByAdminStatus : { adminStatus, isLegacy, isSummary ->
                ProxyAgentAccountGetData proxyAgentAccountGetData = new ProxyAgentAccountGetData()
                ProxyAgentAccountCounters proxyAgentAccountCounters = new ProxyAgentAccountCounters()
                proxyAgentAccountGetData.setProxyAgentAccountCounters(proxyAgentAccountCounters)
                proxyAgentAccountCounters.setNumOfProxyAccount(numOfProxyAccount)
                proxyAgentAccountCounters.setNumOfProxyAccountLegacy(numOfProxyAccount)
                proxyAgentAccountCounters.setNumOfRequestedProxyAccount(numOfProxyAccount)
                proxyAgentAccountCounters.setNumOfRequestedProxyAccountLegacy(numOfProxyAccount)

                return proxyAgentAccountGetData
            },
            getProxyAgentAccountByInactivityPeriod : { adminStatus, isLegacy, isSummary ->
                ProxyAgentAccountGetData proxyAgentAccountGetData = new ProxyAgentAccountGetData()
                ProxyAgentAccountCounters proxyAgentAccountCounters = new ProxyAgentAccountCounters()
                proxyAgentAccountGetData.setProxyAgentAccountCounters(proxyAgentAccountCounters)
                proxyAgentAccountCounters.setNumOfProxyAccount(numOfProxyAccount)
                proxyAgentAccountCounters.setNumOfProxyAccountLegacy(numOfProxyAccount)
                proxyAgentAccountCounters.setNumOfRequestedProxyAccount(numOfProxyAccount)
                proxyAgentAccountCounters.setNumOfRequestedProxyAccountLegacy(numOfProxyAccount)
                return proxyAgentAccountGetData
            }
    ] as IdentityManagementService

    @ImplementationInstance
    IdentityManagementService identityManagementServiceMockWithExcp = [
            getProxyAgentAccountByAdminStatus : { adminStatus, isLegacy, isSummary ->
                throw new IdentityManagementServiceException(IDMS_GLB_CNTS_TEST_EXCP)
            },
            getProxyAgentAccountByInactivityPeriod : { adminStatus, isLegacy, isSummary ->
            }
    ] as IdentityManagementService

    @ImplementationInstance
    IdentityManagementListener idmsListenerMock = [
            getProxyAccountExcessiveEntriesThreshold : {
                return MAX_NUM_TOT_PROXY_ACCOUNT_ENTRIES
            }
    ] as IdentityManagementListener

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {
        injectionProperties.autoLocateFrom('com.ericsson.oss.itpf.security.identitymgmtservices')
        injectionProperties.autoLocateFrom('org.forgerock.opendj.ldap.responses')
        injectionProperties.autoLocateFrom('com.ericsson.oss.itpf.security.identitymgmtservices.ejb.statistics')
        injectionProperties.autoLocateFrom('com.ericsson.oss.itpf.security.identitymgmtservices.ejb.timers')
    }

    def setup (){
        idmsProxyAccountGlbCnts.idmsTimerManager = idmsTimerManagerMock
        idmsProxyAccountGlbCnts.idmsClusterMessageListener = idmsClusterMessageListenerMock
        idmsProxyAccountGlbCnts.identityManagementService = identityManagementServiceMock
        idmsProxyAccountGlbCnts.identityManagementListener = idmsListenerMock
    }

    def 'add global statistics counter happy path master instance and mbean global counters'(){
        given:
        when:
        idmsProxyAccountGlbCnts.initTimer()
        idmsProxyAccountGlbCnts.onTimerEvent()
        then:
        idmsProxyAccountGlbCnts.proxyAccountGlobalCountersEvent.getNumOfProxyAccountLegacy() == numOfProxyAccount &&
        idmsProxyAccountGlbCnts.proxyAccountGlobalCountersEvent.getNumOfProxyAccountLockable() == 0 &&
        idmsProxyAccountGlbCnts.proxyAccountGlobalCountersEvent.getNumOfProxyAccountDisabledLegacy() == numOfProxyAccount &&
        idmsProxyAccountGlbCnts.proxyAccountGlobalCountersEvent.getNumOfProxyAccountDisabledLockable() == 0 &&
        idmsProxyAccountGlbCnts.proxyAccountGlobalCountersEvent.getNumOfProxyAccountEnabledLegacy() == numOfProxyAccount &&
        idmsProxyAccountGlbCnts.proxyAccountGlobalCountersEvent.getNumOfProxyAccountEnabledLockable() == 0 &&
        idmsProxyAccountGlbCnts.proxyAccountGlobalCountersEvent.getNumOfProxyAccountInactiveLegacy() == numOfProxyAccount &&
        idmsProxyAccountGlbCnts.proxyAccountGlobalCountersEvent.getNumOfProxyAccountInactiveLockable() == 0
        idmsProxyAccountGlbCnts.proxyAccountGlobalCountersEvent.getMaxProxyAccountEntriesThreshold() == MAX_NUM_TOT_PROXY_ACCOUNT_ENTRIES &&
        idmsProxyAccountGlbCnts.proxyAccountMonData.getNumberOfTotalProxyAccount() == numOfProxyAccount &&
        idmsProxyAccountGlbCnts.proxyAccountMonData.getMaxNumberOfTotalProxyAccountThreshold() == MAX_NUM_TOT_PROXY_ACCOUNT_ENTRIES
    }

    def 'add global statistics counter happy path slave instance'(){
        given:
        idmsProxyAccountGlbCnts.idmsClusterMessageListener = idmsClusterMessageListenerMockSlave
        when:
        idmsProxyAccountGlbCnts.initTimer()
        idmsProxyAccountGlbCnts.onTimerEvent()
        then:
        idmsProxyAccountGlbCnts.proxyAccountGlobalCountersEvent.eventData.size() == 0
    }

    def 'add and run excessive alarm proxy account IdentityManagementServiceException raised'(){
        given:
        idmsProxyAccountGlbCnts.identityManagementService = identityManagementServiceMockWithExcp
        when:
        idmsProxyAccountGlbCnts.initTimer()
        idmsProxyAccountGlbCnts.onTimerEvent()
        then:
        IdentityManagementTimerException e = thrown()
        e.getMessage().contains(IDMS_GLB_CNTS_TEST_EXCP)
    }

    def 'add global counter timer catch exception '(){
        given:
        idmsProxyAccountGlbCnts.idmsTimerManager = idmsTimerManagerMockExcp
        when:
        idmsProxyAccountGlbCnts.initTimer()
        then:
        notThrown(Exception.class)
    }
}
