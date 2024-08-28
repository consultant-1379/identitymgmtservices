package com.ericsson.oss.itpf.security.identitymgmtservices.ejb

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountDetails
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountGetData
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus

class IdentityManagementServiceBeanFcTest extends CdiSpecification {

    @ObjectUnderTest
    IdentityManagementServiceBean identityManagementServiceBean

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def proxyAgentAccountGetDataMock = new ProxyAgentAccountGetData()
    def proxyAgentAccountDetailsMock = new ProxyAgentAccountDetails()
    def setup (){}

    def 'getProxyAgentAccount ALL Happy Path'(){
        given:
        SecurityManagerBeanMock securityManagerBeanMock = new SecurityManagerBeanMock()
        identityManagementServiceBean.secManager = securityManagerBeanMock
        when:
        ProxyAgentAccountGetData proxyAgentAccountGetData = identityManagementServiceBean.getProxyAgentAccount(false,false)
        then:
        proxyAgentAccountGetData.equals(proxyAgentAccountGetDataMock)
    }

    def 'getProxyAgentAccount ALL with Null Input Data'(){
        given:
        SecurityManagerBeanMock securityManagerBeanMock = new SecurityManagerBeanMock()
        identityManagementServiceBean.secManager = securityManagerBeanMock
        when:
        identityManagementServiceBean.getProxyAgentAccount(null,null)
        then:
        thrown(IdentityManagementServiceException.class)
    }

    def 'getProxyAgentAccount by admin status '(){
        given:
        SecurityManagerBeanMock securityManagerBeanMock = new SecurityManagerBeanMock()
        identityManagementServiceBean.secManager = securityManagerBeanMock
        when:
        ProxyAgentAccountGetData proxyAgentAccountGetData = identityManagementServiceBean.getProxyAgentAccountByAdminStatus(
                ProxyAgentAccountAdminStatus.ENABLED, false,false)
        then:
        proxyAgentAccountGetData.equals(proxyAgentAccountGetDataMock)
    }

    def 'getProxyAgentAccount by admin status with NULL admin status'(){
        given:
        SecurityManagerBeanMock securityManagerBeanMock = new SecurityManagerBeanMock()
        identityManagementServiceBean.secManager = securityManagerBeanMock
        when:
        identityManagementServiceBean.getProxyAgentAccountByAdminStatus(null ,
                false,false)
        then:
        thrown(IdentityManagementServiceException.class)
    }

    def 'getProxyAgentAccount by inactivity period '(){
        given:
        SecurityManagerBeanMock securityManagerBeanMock = new SecurityManagerBeanMock()
        identityManagementServiceBean.secManager = securityManagerBeanMock
        when:
        ProxyAgentAccountGetData proxyAgentAccountGetData = identityManagementServiceBean.getProxyAgentAccountByInactivityPeriod(
                100000, false,false)
        then:
        proxyAgentAccountGetData.equals(proxyAgentAccountGetDataMock)
    }

    def 'getProxyAgentAccount by inactivity period with NULL inactivity date'(){
        given:
        SecurityManagerBeanMock securityManagerBeanMock = new SecurityManagerBeanMock()
        identityManagementServiceBean.secManager = securityManagerBeanMock
        when:
        identityManagementServiceBean.getProxyAgentAccountByInactivityPeriod(null,
                false,false)
        then:
        thrown(IdentityManagementServiceException.class)
    }

    def 'getProxyAgentAccountDetails '(){
        given:
        SecurityManagerBeanMock securityManagerBeanMock = new SecurityManagerBeanMock()
        identityManagementServiceBean.secManager = securityManagerBeanMock
        when:
        ProxyAgentAccountDetails proxyAgentAccountDetails = identityManagementServiceBean.getProxyAgentAccountDetails("userDn")
        then:
        proxyAgentAccountDetails.equals(proxyAgentAccountDetailsMock)
    }

    def 'updateProxyAgentAccountAdminStatus '(){
        given:
        SecurityManagerBeanMock securityManagerBeanMock = new SecurityManagerBeanMock()
        identityManagementServiceBean.secManager = securityManagerBeanMock
        when:
        Boolean ret = identityManagementServiceBean.updateProxyAgentAccountAdminStatus("userDn",
                ProxyAgentAccountAdminStatus.ENABLED)
        then:
        ret == true
    }

    def 'updateProxyAgentAccountAdminStatus null admin status'(){
        given:
        SecurityManagerBeanMock securityManagerBeanMock = new SecurityManagerBeanMock()
        identityManagementServiceBean.secManager = securityManagerBeanMock
        when:
        identityManagementServiceBean.updateProxyAgentAccountAdminStatus("userDn", null)
        then:
        thrown(IdentityManagementServiceException.class)
    }

    class SecurityManagerBeanMock extends SecurityManagerBean {
        ProxyAgentAccountGetData getProxyAgentAccount(Boolean isLegacy, Boolean isSummary){
            return proxyAgentAccountGetDataMock
        }

        ProxyAgentAccountGetData getProxyAgentAccountByAdminStatus(ProxyAgentAccountAdminStatus adminStatus,
                                                                   Boolean isLegacy, Boolean isSummary) {
            return proxyAgentAccountGetDataMock
        }

        ProxyAgentAccountGetData getProxyAgentAccountByInactivityPeriod(Long inactivityPeriod, Boolean isLegacy,
                                                                        Boolean isSummary) {
            return proxyAgentAccountGetDataMock
        }

        ProxyAgentAccountDetails getProxyAgentAccountDetails(String userDn) {
            return proxyAgentAccountDetailsMock
        }

        Boolean updateProxyAgentAccountAdminStatus(String userDn, ProxyAgentAccountAdminStatus adminStatus) {
            return true
        }
    }
}
