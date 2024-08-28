package com.ericsson.oss.itpf.security.identitymgmtservices.ejb

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager
import com.ericsson.oss.itpf.security.identitymgmtservices.DSCommunicator
import com.ericsson.oss.itpf.security.identitymgmtservices.DSCommunicatorException
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException
import com.ericsson.oss.itpf.security.identitymgmtservices.IdmConstants
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountDetails
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountGetData
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus
import org.forgerock.opendj.ldap.Attribute
import org.forgerock.opendj.ldap.DN

import org.forgerock.opendj.ldap.responses.SearchResultEntry
import spock.lang.Unroll

import javax.naming.directory.SearchControls
import java.util.concurrent.locks.Lock

class SecurityManagerBeanProxyFcTest extends CdiSpecification {
    private static final LEGACY_TREE_DN = "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
    private static final LOCKABLE_TREE_DN = "cn=ProxyAccount_38,ou=proxyagentlockable,ou=com,dc=ieatlms5223,dc=com"
    private static final INVALID_TREE_DN = "cn=ProxyAccount_38,ou=proxyagentlockable_invalid,ou=com,dc=ieatlms5223,dc=com"

    @ObjectUnderTest
    SecurityManagerBean securityManagerBean

    @MockedImplementation
    LockManager lockManager

    @MockedImplementation
    Lock m2mLock

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {
        injectionProperties.autoLocateFrom('com.ericsson.oss.itpf.security.identitymgmtservices')
        injectionProperties.autoLocateFrom('org.forgerock.opendj.ldap.responses')
        injectionProperties.autoLocateFrom('com.ericsson.oss.itpf.security.identitymgmtservices.ejb.statistics')
    }

    def setup () {
        lockManager.getDistributedLock(_ as String) >> m2mLock
        securityManagerBean.init()
    }

    Attribute attributeMock = [
            firstValueAsString : {
                return "20230901123055"
            }
    ] as Attribute

    Attribute attributeMock2 = [
            firstValueAsString : {
                return "true"
            }
    ] as Attribute

    Attribute attributeMock3 = [
            firstValueAsString : {
                return "100"
            }
    ] as Attribute

    @ImplementationInstance
    SearchResultEntry searchResultEntry = [
            getName : {
                return DN.valueOf("ou=proxyagent,ou=com,dc=ieatlms5223,dc=com")
            },
            getAttribute : {
                var1 ->
                    return attributeMock
            }
    ] as SearchResultEntry

    @ImplementationInstance
    SearchResultEntry searchResultEntry2 = [
            getName : {
                return DN.valueOf("cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com")
            },
            getAttribute : {
                var1 ->
                    return null
            }
    ] as SearchResultEntry

    @ImplementationInstance
    SearchResultEntry searchResultEntry3 = [
            getName : {
                return DN.valueOf("cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com")
            },
            getAttribute : {
                var1 ->
                    return  attributeMock2
            }
    ] as SearchResultEntry

    @ImplementationInstance
    SearchResultEntry searchResultEntry4 = [
            getName : {
                return DN.valueOf("ou=proxyagent,ou=com,dc=ieatlms5223,dc=com")
            },
            getAttribute : {
                var1 ->
                    return  attributeMock3
            }
    ] as SearchResultEntry

    def isSwitchOnLegacySubtree = false
    @ImplementationInstance
    IdentityManagementListener idmsListenerMock = [
            getProxyAccountRdnSubTree : {
                return isSwitchOnLegacySubtree
            }
    ] as IdentityManagementListener

    def ' getProxyAgentAccount ALL happy path ' () {
        given:
        def communicatorMock1 = new DSCommunicatorMock()
        securityManagerBean.communicator = communicatorMock1
        when:
        ProxyAgentAccountGetData proxyAgentAccountGetData = securityManagerBean.getProxyAgentAccount(false, false)
        then:
        proxyAgentAccountGetData.getProxyAgentAccountDetailsList().size() == 1
    }

    def ' getProxyAgentAccount ALL raise an exception on finding subtrees ' () {
        given:
        def communicatorMock3 = new DSCommunicatorMock3()
        securityManagerBean.communicator = communicatorMock3
        when:
        securityManagerBean.getProxyAgentAccount(false, false)
        then:
        thrown(IdentityManagementServiceException.class)
    }

    def ' getProxyAgentAccount ALL raise an exception on getting entries ' () {
        given:
        def communicatorMock4 = new DSCommunicatorMock4()
        securityManagerBean.communicator = communicatorMock4
        when:
        securityManagerBean.getProxyAgentAccount(false, false)
        then:
        thrown(IdentityManagementServiceException.class)
    }

    def ' getProxyAgentAccount ALL raise an exception on counting global counters ' () {
        given:
        def communicatorMock6 = new DSCommunicatorMock6()
        securityManagerBean.communicator = communicatorMock6
        when:
        securityManagerBean.getProxyAgentAccount(false, false)
        then:
        thrown(IdentityManagementServiceException.class)
    }

    def ' getProxyAgentAccountByAdminStatus happy path ' () {
        given:
        def communicatorMock1 = new DSCommunicatorMock()
        securityManagerBean.communicator = communicatorMock1
        when:
        ProxyAgentAccountGetData proxyAgentAccountGetData = securityManagerBean.getProxyAgentAccountByAdminStatus(ProxyAgentAccountAdminStatus.ENABLED,false, false)
        then:
        proxyAgentAccountGetData.getProxyAgentAccountDetailsList().size() == 1
    }

    def ' getProxyAgentAccountByInactivityPeriod happy path ' () {
        given:
        def communicatorMock1 = new DSCommunicatorMock()
        securityManagerBean.communicator = communicatorMock1
        Long inactivityPeriod = 100000
        when:
        ProxyAgentAccountGetData proxyAgentAccountGetData = securityManagerBean.getProxyAgentAccountByInactivityPeriod(inactivityPeriod,false, false)
        then:
        proxyAgentAccountGetData.getProxyAgentAccountDetailsList().size() == 1
    }

    def ' getProxyAgentAccountDetails happy path ' () {
        given:
        def communicatorMock1 = new DSCommunicatorMock()
        securityManagerBean.communicator = communicatorMock1
        String userDn = "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
        when:
        ProxyAgentAccountDetails proxyAgentAccountDetails = securityManagerBean.getProxyAgentAccountDetails(userDn)
        then:
        proxyAgentAccountDetails.getUserDn() == userDn &&
                proxyAgentAccountDetails.getAdminStatus() == ProxyAgentAccountAdminStatus.DISABLED
    }

    def ' getProxyAgentAccountDetails raise an exception when finding entry ' () {
        given:
        def communicatorMock2 = new DSCommunicatorMock2()
        securityManagerBean.communicator = communicatorMock2
        String userDn = "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
        when:
        securityManagerBean.getProxyAgentAccountDetails(userDn)
        then:
        thrown(IdentityManagementServiceException.class)
    }

    @Unroll
    def ' updateProxyAgentAccountAdminStatus happy path, increase counter by 1 if subtree is valid' () {
        given:
        def communicatorMock1 = new DSCommunicatorMock()
        securityManagerBean.communicator = communicatorMock1
        def monitoredDataInst = securityManagerBean.proxyAccountMonitoredDataManager.proxyAccountMonitoredData
        String userDn = dn
        ProxyAgentAccountAdminStatus adminStatus = status
        when:
        Boolean ret = securityManagerBean.updateProxyAgentAccountAdminStatus(userDn, adminStatus)
        then:
        ret == true
        and:
        monitoredDataInst.getNumberOfDisabledProxyAccountOnLegacySubtree() == discntleg
        and:
        monitoredDataInst.getNumberOfEnabledProxyAccountOnLegacySubtree() == encntleg
        and:
        monitoredDataInst.getNumberOfDisabledProxyAccountOnLockableSubtree() == discntlock
        and:
        monitoredDataInst.getNumberOfEnabledProxyAccountOnLockableSubtree() == encntlock
        where:
        dn               |  status                                | discntleg | encntleg | discntlock | encntlock
        LEGACY_TREE_DN   |ProxyAgentAccountAdminStatus.DISABLED   | 1         | 0        | 0          | 0
        LEGACY_TREE_DN   |ProxyAgentAccountAdminStatus.ENABLED    | 0         | 1        | 0          | 0
        LOCKABLE_TREE_DN |ProxyAgentAccountAdminStatus.DISABLED   | 0         | 0        | 1          | 0
        LOCKABLE_TREE_DN |ProxyAgentAccountAdminStatus.ENABLED    | 0         | 0        | 0          | 1
        INVALID_TREE_DN  |ProxyAgentAccountAdminStatus.DISABLED   | 0         | 0        | 0          | 0
    }

    def ' updateProxyAgentAccountAdminStatus raise an exception when update entry' () {
        given:
        def communicatorMock2 = new DSCommunicatorMock2()
        securityManagerBean.communicator = communicatorMock2
        String userDn = "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
        ProxyAgentAccountAdminStatus adminStatus = ProxyAgentAccountAdminStatus.DISABLED
        when:
        securityManagerBean.updateProxyAgentAccountAdminStatus(userDn, adminStatus)
        then:
        thrown(IdentityManagementServiceException.class)
    }

    def ' updateProxyAgentAccountAdminStatus raise an exception when no such entry ' () {
        given:
        def communicatorMock5 = new DSCommunicatorMock5()
        securityManagerBean.communicator = communicatorMock5
        String userDn = "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
        ProxyAgentAccountAdminStatus adminStatus = ProxyAgentAccountAdminStatus.DISABLED
        when:
        Boolean ret = securityManagerBean.updateProxyAgentAccountAdminStatus(userDn, adminStatus)
        then:
        ret == false
    }

    @Unroll
    def ' add proxy account happy path, increase counter by 1 on each subtree ' () {
        given:
        def communicatorMock1 = new DSCommunicatorMock()
        securityManagerBean.communicator = communicatorMock1
        isSwitchOnLegacySubtree = islegacy
        securityManagerBean.identityManagementListener = idmsListenerMock
        def monitoredDataInst = securityManagerBean.proxyAccountMonitoredDataManager.proxyAccountMonitoredData
        when:
        Boolean ret = securityManagerBean.addProxyAccount()
        then:
        ret == true
        and:
        monitoredDataInst.getNumberOfCreatedProxyAccountOnLegacySubtree() == cntLegacy
        and:
        monitoredDataInst.getNumberOfCreatedProxyAccountOnLockableSubtree() == cntLockable
        where :
        islegacy    | cntLegacy     |   cntLockable
        true        | 1             |   0
        false       | 0             |   1
    }

    @Unroll
    def ' remove proxy account happy path, increase counter by 1 if subtree is valid ' () {
        given:
        def communicatorMock1 = new DSCommunicatorMock()
        securityManagerBean.communicator = communicatorMock1
        def monitoredDataInst = securityManagerBean.proxyAccountMonitoredDataManager.proxyAccountMonitoredData
        when:
        Boolean ret = securityManagerBean.removeProxyAccount(dn)
        then:
        ret == true
        and:
        monitoredDataInst.getNumberOfDeletedProxyAccountOnLegacySubtree() == removeleg
        and:
        monitoredDataInst.getNumberOfDeletedProxyAccountOnLockableSubtree() == removelock
        where:
        dn               | removeleg |removelock
        LEGACY_TREE_DN   | 1         | 0
        LOCKABLE_TREE_DN | 0         | 1
        INVALID_TREE_DN  | 0         | 0
    }

    class DSCommunicatorMock extends DSCommunicator {
        List<SearchResultEntry> queryGenericMultipleAttributes(final String dnPostfix, final int scope,
                                                                      final List<String> attributesToReturn,
                                                                      final String filterString) throws DSCommunicatorException {

            List<SearchResultEntry> resultListWithSearchEntries = new ArrayList<>()

            if(scope == SearchControls.ONELEVEL_SCOPE) {
                resultListWithSearchEntries.add(searchResultEntry)
            } else if (scope == SearchControls.SUBTREE_SCOPE) {
                resultListWithSearchEntries.add (searchResultEntry3)
            } else if (scope == SearchControls.OBJECT_SCOPE) {
                resultListWithSearchEntries.add (searchResultEntry4)
            }
            else {
                resultListWithSearchEntries.add(searchResultEntry2)
            }
            return resultListWithSearchEntries
        }

        void modifyEntryReplace(final String dn, final String attrName, final String value) throws DSCommunicatorException {}
        void addEntry(final String dn, final Map<String, ArrayList<String>> avPairs) {}
        void deleteEntry(final String dn) {}
    }

    class DSCommunicatorMock2 extends DSCommunicator {
        List<SearchResultEntry> queryGenericMultipleAttributes(final String dnPostfix, final int scope,
                                                               final List<String> attributesToReturn,
                                                               final String filterString) throws DSCommunicatorException {
            throw new DSCommunicatorException("Failed to get datastore for queryGenericMultipleAttributes operation",
                    IdmConstants.LDAP_ERROR_CONNECTION_FAILURE)
        }

        void modifyEntryReplace(final String dn, final String attrName, final String value) throws DSCommunicatorException {
            throw new DSCommunicatorException("Failed to get datastore for queryGenericMultipleAttributes operation",
                    IdmConstants.LDAP_ERROR_CONNECTION_FAILURE)
        }
    }

    class DSCommunicatorMock3 extends DSCommunicator {
        List<SearchResultEntry> queryGenericMultipleAttributes(final String dnPostfix, final int scope,
                                                               final List<String> attributesToReturn,
                                                               final String filterString) throws DSCommunicatorException {

            List<SearchResultEntry> resultListWithSearchEntries = new ArrayList<>()

            if(scope == SearchControls.ONELEVEL_SCOPE) {
                throw new DSCommunicatorException("Failed to get datastore for queryGenericMultipleAttributes operation",
                        IdmConstants.LDAP_ERROR_CONNECTION_FAILURE)
            } else if (scope == SearchControls.SUBTREE_SCOPE) {
                resultListWithSearchEntries.add (searchResultEntry3)
            }
            else {
                resultListWithSearchEntries.add(searchResultEntry2)
            }
            return resultListWithSearchEntries
        }
    }

    class DSCommunicatorMock4 extends DSCommunicator {
        List<SearchResultEntry> queryGenericMultipleAttributes(final String dnPostfix, final int scope,
                                                               final List<String> attributesToReturn,
                                                               final String filterString) throws DSCommunicatorException {

            List<SearchResultEntry> resultListWithSearchEntries = new ArrayList<>()

            if (dnPostfix.contains("ou=proxyagent")) {
                throw new DSCommunicatorException("Failed to get datastore for queryGenericMultipleAttributes operation",
                        IdmConstants.LDAP_ERROR_CONNECTION_FAILURE)
            } else {
                resultListWithSearchEntries.add(searchResultEntry)
            }
            return resultListWithSearchEntries
        }
    }

    class DSCommunicatorMock5 extends DSCommunicator {
        List<SearchResultEntry> queryGenericMultipleAttributes(final String dnPostfix, final int scope,
                                                               final List<String> attributesToReturn,
                                                               final String filterString) throws DSCommunicatorException {
            DSCommunicatorException excp = new DSCommunicatorException("Failed to get datastore for queryGenericMultipleAttributes operation",
                    IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY)
            throw excp
        }

        void modifyEntryReplace(final String dn, final String attrName, final String value) throws DSCommunicatorException {
            DSCommunicatorException excp = new DSCommunicatorException("Failed to get datastore for queryGenericMultipleAttributes operation",
                    IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY)
            throw excp
        }
    }

    class DSCommunicatorMock6 extends DSCommunicator {
        List<SearchResultEntry> queryGenericMultipleAttributes(final String dnPostfix, final int scope,
                                                               final List<String> attributesToReturn,
                                                               final String filterString) throws DSCommunicatorException {
            List<SearchResultEntry> resultListWithSearchEntries = new ArrayList<>()
            if (scope == SearchControls.ONELEVEL_SCOPE) {
                resultListWithSearchEntries.add(searchResultEntry)

            } else if (scope == SearchControls.SUBTREE_SCOPE) {
                resultListWithSearchEntries.add(searchResultEntry3)
            } else if (scope == SearchControls.OBJECT_SCOPE) {
                throw new DSCommunicatorException("Failed to get datastore for queryGenericMultipleAttributes operation",
                        IdmConstants.LDAP_ERROR_CONNECTION_FAILURE)
            } else {
                resultListWithSearchEntries.add(searchResultEntry2)
            }
            return resultListWithSearchEntries
        }

        void modifyEntryReplace(final String dn, final String attrName, final String value) throws DSCommunicatorException {}}
}
