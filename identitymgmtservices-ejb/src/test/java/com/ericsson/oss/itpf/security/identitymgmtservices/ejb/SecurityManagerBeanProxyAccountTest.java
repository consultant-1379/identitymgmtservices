package com.ericsson.oss.itpf.security.identitymgmtservices.ejb;

import static com.ericsson.oss.itpf.security.identitymgmtservices.IdmConstants.PROXY_ACCOUNT_DEFAULT_PASSWORD_LENGTH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;

import javax.naming.directory.SearchControls;
import javax.xml.bind.DatatypeConverter;

import com.ericsson.oss.itpf.security.identitymgmtservices.ejb.statistics.ProxyAccountMonitoredDataManager;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.security.cryptography.CryptographyService;
import com.ericsson.oss.itpf.security.identitymgmtservices.ConfigurationBean;
import com.ericsson.oss.itpf.security.identitymgmtservices.DSCommunicator;
import com.ericsson.oss.itpf.security.identitymgmtservices.DSCommunicatorException;
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException;
import com.ericsson.oss.itpf.security.identitymgmtservices.IdmConstants;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountData;
import com.ericsson.oss.itpf.security.identitymgmtservices.crypto.CryptographyServiceClient;

@RunWith(MockitoJUnitRunner.class)
public class SecurityManagerBeanProxyAccountTest {

    @Mock
    private SystemRecorder systemRecorder;

    private Logger logger = LoggerFactory.getLogger(SecurityManagerBeanProxyAccountTest.class);
    private StringBuffer dnPostfix = null;
    public final static String CHARSET = "UTF-8";
    public final static String PROXY_AGENT_BRANCH = "ou=proxyagent,ou=com";

    private SecurityManagerBean secManager = null;

    @Mock
    private DSCommunicator mockedCommunicator;

    @Mock
    private CryptographyService mockedCryptographyService;

    @Mock
    private IdentityManagementListener identityManagementListener;

    @Mock
    private ProxyAccountMonitoredDataManager mockedProxyAccountMonitoredDataManager;

    private String baseDN;
    private String proxyUserContainer;
    private Lock M2MLock;
    private Lock proxyLock;

    @BeforeClass
    public static void setUpBeforeClass() {
        System.setProperty(IdmConstants.CONFIGURATION_PROPERTY, "./src/test/resources/datastore.properties");
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        secManager = new SecurityManagerBean();
        secManager.logger = logger;
        secManager.systemRecorder = systemRecorder;
        baseDN = ConfigurationBean.get(IdmConstants.COM_INF_LDAP_ROOT_SUFFIX_PROPERTY);
        proxyUserContainer = ConfigurationBean.get(IdmConstants.COM_INF_LDAP_PROXY_USER_CONTAINER_PROPERTY, PROXY_AGENT_BRANCH);
        logger.debug("TEST proxyUserContainer: {}", proxyUserContainer);

        secManager.communicator = mockedCommunicator;

        secManager.cryptoClient = new CryptographyServiceClient(mockedCryptographyService);
        M2MLock = mock(Lock.class);
        proxyLock = mock(Lock.class);
        secManager.lockManager = mock(LockManager.class);
        when(secManager.getDistributedLock(IdmConstants.PROXY_LOCK_NAME)).thenReturn(proxyLock);
        when(secManager.getDistributedLock(IdmConstants.M2M_LOCK_NAME)).thenReturn(M2MLock);

        secManager.identityManagementListener = identityManagementListener;
        when(secManager.identityManagementListener.getPasswordLength()).thenReturn(PROXY_ACCOUNT_DEFAULT_PASSWORD_LENGTH);
    }

    @After
    public void tearDown() {
    }

    @Test
    @Ignore("waiting for rework")
    public void testCreateProxyUserSuccess() throws UnsupportedEncodingException, DSCommunicatorException {

        //arrange
        final String password = "changeit";
        final String expectedCN = "ProxyAccount_1";

        final HashMap<String, ArrayList<String>> expectedAvPairs;
        expectedAvPairs = new HashMap<>();
        final ArrayList<String> userObjectClassList = getLDAPUserObjectClasses();

        final byte[] encryptedBytesArr = password.getBytes(CHARSET);
        when(mockedCryptographyService.encrypt(DatatypeConverter.parseBase64Binary(password))).thenReturn(encryptedBytesArr);
        logger.debug("testCreateProxyUserSuccess: Generated encrypted password: {}",encryptedBytesArr);

        expectedAvPairs.put("objectClass", userObjectClassList);
        expectedAvPairs.put(IdmConstants.LDAP_FULL_NAME, getArrayList(expectedCN));
        expectedAvPairs.put(IdmConstants.LDAP_LAST_NAME, getArrayList(expectedCN));
        expectedAvPairs.put(IdmConstants.LDAP_USER_CRED, getArrayList(password));

        //action
        ProxyAgentAccountData proxyAgentAccountData = secManager.addProxyAccount();

        //verify
        assertEquals(makeProxyUserDN(expectedCN), proxyAgentAccountData.getUserDN());
        assertEquals(PROXY_ACCOUNT_DEFAULT_PASSWORD_LENGTH, proxyAgentAccountData.getUserPassword().length());

    }

    @Test
    public void proxyPasswordShouldNotBeEncrypted() throws DSCommunicatorException {
        final String generatedPassword = "changeit";
        final String dummyCN = "ProxyAccount_1";

        final ArrayList<String> dummyList = getArrayList(dummyCN);
        final HashMap<String, ArrayList<String>> expectedAvPairsWithNotEncryptedPassword;
        expectedAvPairsWithNotEncryptedPassword = new HashMap<>();
        final ArrayList<String> dummyUserObjectClassList = getLDAPUserObjectClasses();

        expectedAvPairsWithNotEncryptedPassword.put("objectClass", dummyUserObjectClassList);
        expectedAvPairsWithNotEncryptedPassword.put(IdmConstants.LDAP_FULL_NAME, dummyList);
        expectedAvPairsWithNotEncryptedPassword.put(IdmConstants.LDAP_LAST_NAME, dummyList);
        expectedAvPairsWithNotEncryptedPassword.put(IdmConstants.LDAP_USER_CRED, getArrayList(generatedPassword));

        secManager.proxyAccountMonitoredDataManager = mockedProxyAccountMonitoredDataManager;

        ProxyAgentAccountData proxyAgentAccountData = secManager.addProxyAccount();
        final String actualPassword = proxyAgentAccountData.getUserPassword();

        verify(mockedCryptographyService, never()).encrypt(DatatypeConverter.parseBase64Binary(generatedPassword));
        assertEquals(PROXY_ACCOUNT_DEFAULT_PASSWORD_LENGTH, actualPassword.length());
    }
    @Test
    public void proxyPasswordCreateOnLockableSubtree() throws DSCommunicatorException {
        final String generatedPassword = "changeit";
        final String dummyCN = "ProxyAccount_1";

        final ArrayList<String> dummyList = getArrayList(dummyCN);
        final HashMap<String, ArrayList<String>> expectedAvPairsWithNotEncryptedPassword;
        expectedAvPairsWithNotEncryptedPassword = new HashMap<>();
        final ArrayList<String> dummyUserObjectClassList = getLDAPUserObjectClasses();

        expectedAvPairsWithNotEncryptedPassword.put("objectClass", dummyUserObjectClassList);
        expectedAvPairsWithNotEncryptedPassword.put(IdmConstants.LDAP_FULL_NAME, dummyList);
        expectedAvPairsWithNotEncryptedPassword.put(IdmConstants.LDAP_LAST_NAME, dummyList);
        expectedAvPairsWithNotEncryptedPassword.put(IdmConstants.LDAP_USER_CRED, getArrayList(generatedPassword));

        secManager.proxyAccountMonitoredDataManager = mockedProxyAccountMonitoredDataManager;
        when(identityManagementListener.getProxyAccountRdnSubTree()).thenReturn(false);
        ProxyAgentAccountData proxyAgentAccountData = secManager.addProxyAccount();
        final String actualPassword = proxyAgentAccountData.getUserPassword();

        verify(mockedCryptographyService, never()).encrypt(DatatypeConverter.parseBase64Binary(generatedPassword));
        assertEquals(PROXY_ACCOUNT_DEFAULT_PASSWORD_LENGTH, actualPassword.length());
        assertTrue(proxyAgentAccountData.getUserDN().contains("ou=proxyagentlockable,ou=com"));
    }
    @Test
    public void proxyPasswordCreateOnLegacySubtree() throws DSCommunicatorException {
        final String generatedPassword = "changeit";
        final String dummyCN = "ProxyAccount_1";

        final ArrayList<String> dummyList = getArrayList(dummyCN);
        final HashMap<String, ArrayList<String>> expectedAvPairsWithNotEncryptedPassword;
        expectedAvPairsWithNotEncryptedPassword = new HashMap<>();
        final ArrayList<String> dummyUserObjectClassList = getLDAPUserObjectClasses();

        expectedAvPairsWithNotEncryptedPassword.put("objectClass", dummyUserObjectClassList);
        expectedAvPairsWithNotEncryptedPassword.put(IdmConstants.LDAP_FULL_NAME, dummyList);
        expectedAvPairsWithNotEncryptedPassword.put(IdmConstants.LDAP_LAST_NAME, dummyList);
        expectedAvPairsWithNotEncryptedPassword.put(IdmConstants.LDAP_USER_CRED, getArrayList(generatedPassword));

        secManager.proxyAccountMonitoredDataManager = mockedProxyAccountMonitoredDataManager;
        when(identityManagementListener.getProxyAccountRdnSubTree()).thenReturn(true);
        ProxyAgentAccountData proxyAgentAccountData = secManager.addProxyAccount();
        final String actualPassword = proxyAgentAccountData.getUserPassword();

        verify(mockedCryptographyService, never()).encrypt(DatatypeConverter.parseBase64Binary(generatedPassword));
        assertEquals(PROXY_ACCOUNT_DEFAULT_PASSWORD_LENGTH, actualPassword.length());
        assertTrue(proxyAgentAccountData.getUserDN().contains("ou=proxyagent,ou=com"));
    }

    @Test
    public void proxyPasswordCreateOnLockableSubtreeNullPib() throws DSCommunicatorException {
        final String generatedPassword = "changeit";
        final String dummyCN = "ProxyAccount_1";

        final ArrayList<String> dummyList = getArrayList(dummyCN);
        final HashMap<String, ArrayList<String>> expectedAvPairsWithNotEncryptedPassword;
        expectedAvPairsWithNotEncryptedPassword = new HashMap<>();
        final ArrayList<String> dummyUserObjectClassList = getLDAPUserObjectClasses();

        expectedAvPairsWithNotEncryptedPassword.put("objectClass", dummyUserObjectClassList);
        expectedAvPairsWithNotEncryptedPassword.put(IdmConstants.LDAP_FULL_NAME, dummyList);
        expectedAvPairsWithNotEncryptedPassword.put(IdmConstants.LDAP_LAST_NAME, dummyList);
        expectedAvPairsWithNotEncryptedPassword.put(IdmConstants.LDAP_USER_CRED, getArrayList(generatedPassword));

        secManager.proxyAccountMonitoredDataManager = mockedProxyAccountMonitoredDataManager;
        when(identityManagementListener.getProxyAccountRdnSubTree()).thenReturn(null);
        ProxyAgentAccountData proxyAgentAccountData = secManager.addProxyAccount();
        final String actualPassword = proxyAgentAccountData.getUserPassword();

        verify(mockedCryptographyService, never()).encrypt(DatatypeConverter.parseBase64Binary(generatedPassword));
        assertEquals(PROXY_ACCOUNT_DEFAULT_PASSWORD_LENGTH, actualPassword.length());
        assertTrue(proxyAgentAccountData.getUserDN().contains("ou=proxyagentlockable,ou=com"));
    }

    @Test
    public void testDeleteProxyUserSuccess() throws DSCommunicatorException {
        final String userName = "ProxyAccount_4";
        final String userDN = makeProxyUserDN(userName);
        secManager.proxyAccountMonitoredDataManager = mockedProxyAccountMonitoredDataManager;

        assertTrue(secManager.removeProxyAccount(userDN));
        verify(mockedCommunicator).deleteEntry(userDN);

    }

    @Test
    public void testDeleteProxyUserFailedNoSuchUser() throws DSCommunicatorException {
        final String userName = "ProxyAccount_4";
        final String userDN = makeProxyUserDN(userName);

        doThrow(new DSCommunicatorException("TestException", IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY)).when(mockedCommunicator).deleteEntry(userDN);

        assertFalse(secManager.removeProxyAccount(userDN));

    }

    @Test
    public void testDeleteProxyUserFailedOtherDSCommunicatorException() throws DSCommunicatorException {
        final String userName = "ProxyAccount_4";
        final String userDN = makeProxyUserDN(userName);

        doThrow(new DSCommunicatorException("TestException", IdmConstants.LDAP_ERROR_CONNECTION_FAILURE)).when(mockedCommunicator)
                .deleteEntry(userDN);

        try {
            secManager.removeProxyAccount(userDN);
            fail("No exception");
        } catch (IdentityManagementServiceException e) {
            assertEquals("removeProxyAccount: Problem occurred on removing user " + userDN, e.getMessage());
            assertEquals(IdentityManagementServiceException.Error.DATA_STORE_CONNECTION_FAILURE, e.getError());
        } catch (Exception e) {
            fail("Wrong type of exception");
        }

    }

    @Test
    @Ignore("waiting for rework")
    public void testCreateProxyAccountUniqueCn() throws DSCommunicatorException, UnsupportedEncodingException {
        final String ProfileDN = PROXY_AGENT_BRANCH + "," + ConfigurationBean.get(IdmConstants.COM_INF_LDAP_ROOT_SUFFIX_PROPERTY);
        final String password = "changeit";

        final ArrayList<String> userList = new ArrayList<String>();
        for (int i = 1; i < 10; i++) {
            userList.add("ProxyAccount_" + i);
        }

        when(mockedCommunicator.querySingleAttribute(ProfileDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_FULL_NAME, null))
                .thenReturn(userList);
        when(mockedCommunicator.getRandomPassword()).thenReturn(password);
        final byte[] encryptedBytesArr = password.getBytes(CHARSET);
        when(mockedCryptographyService.encrypt(DatatypeConverter.parseBase64Binary(password))).thenReturn(encryptedBytesArr);

        assertEquals("ProxyAccount_10", secManager.addProxyAccount().getUserDN().substring(3, 18));//secManager.generateUniqueCn());
    }

    @Test
    @Ignore("waiting for rework")
    public void testCreateProxyAccountUniqueCnWithoutNumberInName () throws DSCommunicatorException, UnsupportedEncodingException {
        final String ProfileDN = PROXY_AGENT_BRANCH + "," + ConfigurationBean.get(IdmConstants.COM_INF_LDAP_ROOT_SUFFIX_PROPERTY);
        final String password = "changeit";

        final ArrayList<String> userList = new ArrayList<String>();
        for (int i = 1; i < 10; i++) {
            userList.add("ProxyAccount_" + i);
        }
        userList.add("ProxyAccount_Test");

        when(mockedCommunicator.querySingleAttribute(ProfileDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_FULL_NAME, null))
                .thenReturn(userList);
        when(mockedCommunicator.getRandomPassword()).thenReturn(password);
        final byte[] encryptedBytesArr = password.getBytes(CHARSET);
        when(mockedCryptographyService.encrypt(DatatypeConverter.parseBase64Binary(password))).thenReturn(encryptedBytesArr);

        assertEquals("ProxyAccount_10", secManager.addProxyAccount().getUserDN().substring(3, 18));//secManager.generateUniqueCn());
    }

    @Test
    @Ignore("waiting for rework")
    public void testCreateProxyAccountUniqueCnWithoutProperPrefix () throws DSCommunicatorException, UnsupportedEncodingException {
        final String ProfileDN = PROXY_AGENT_BRANCH + "," + ConfigurationBean.get(IdmConstants.COM_INF_LDAP_ROOT_SUFFIX_PROPERTY);
        final String password = "changeit";

        final ArrayList<String> userList = new ArrayList<String>();
        for (int i = 1; i < 10; i++) {
            userList.add("ProxyAccount_" + i);
        }
        userList.add("ProxyAccountTest_1");
        userList.add("TestProxyAccount_13");

        when(mockedCommunicator.querySingleAttribute(ProfileDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_FULL_NAME, null))
                .thenReturn(userList);
        when(mockedCommunicator.getRandomPassword()).thenReturn(password);
        final byte[] encryptedBytesArr = password.getBytes(CHARSET);
        when(mockedCryptographyService.encrypt(DatatypeConverter.parseBase64Binary(password))).thenReturn(encryptedBytesArr);

        assertEquals("ProxyAccount_10", secManager.addProxyAccount().getUserDN().substring(3, 18));//secManager.generateUniqueCn());
    }

    @Test
    @Ignore("waiting for rework")
    public void testCreateProxyAccountUniqueCnDSCommunicatorException() throws DSCommunicatorException {
        final String ProfileDN = PROXY_AGENT_BRANCH + "," + ConfigurationBean.get(IdmConstants.COM_INF_LDAP_ROOT_SUFFIX_PROPERTY);

        doThrow(new DSCommunicatorException("TestException", IdmConstants.IDMS_UNEXPECTED_ERROR)).when(mockedCommunicator).querySingleAttribute(
                ProfileDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_FULL_NAME, null);

        IdentityManagementServiceException expectedException = new IdentityManagementServiceException(
                ("Problem retrieving Proxy users, error TestException"), IdentityManagementServiceException.Error.UNEXPECTED_ERROR);

        try {
            //action
            secManager.addProxyAccount();//secManager.generateUniqueCn();
            fail("No exception");
        } catch (IdentityManagementServiceException e) {
            assertEquals(expectedException.getError(), e.getError());
            assertEquals(expectedException.getMessage(), e.getMessage());
        } catch (Exception e) {
            fail("Wrong exception");

        }

    }

    private String makeProxyUserDN(final String name) {
        final StringBuffer userDN = new StringBuffer("cn=" + name);
        userDN.append("," + proxyUserContainer);
        userDN.append(getDnPostfix());
        return userDN.toString();
    }

    private String getDnPostfix() {
        if (dnPostfix == null) {
            dnPostfix = new StringBuffer(",");
            dnPostfix.append(baseDN);
        }
        return dnPostfix.toString();
    }

    private ArrayList<String> getLDAPUserObjectClasses() {
        final ArrayList<String> objs = new ArrayList<String>();
        objs.add("top");
        objs.add("inetorgperson");
        objs.add("person");
        return objs;
    }

    private ArrayList<String> getArrayList(final String value) {
        final ArrayList<String> objs = new ArrayList<String>();
        objs.add(value);
        return objs;
    }

}
