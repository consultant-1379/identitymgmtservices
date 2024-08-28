/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb;

import com.ericsson.oss.itpf.security.identitymgmtservices.ConfigurationBean;
import com.ericsson.oss.itpf.security.identitymgmtservices.DSCommunicator;
import com.ericsson.oss.itpf.security.identitymgmtservices.DSCommunicatorException;
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException;
import com.ericsson.oss.itpf.security.identitymgmtservices.IdmConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ModifyPosixAttributesTest {
    private SystemRecorder systemRecorder = null;
    private Logger logger = LoggerFactory.getLogger(ModifyPosixAttributesTest.class);
    private SecurityManagerBean secManager = null;

    private final static String FIELD_TECH_USER_HOME = "/home/smrs";
    private final static String PEOPLE_CONTAINER = "People";
    private final static String FIELD_TECH_USER_CONTAINER = PEOPLE_CONTAINER;
    private final static String FIELD_TECH_USER_GROUP_NAME = "mm-smrsusers";
    private final static String AMOS_USER_GROUP_NAME = "amos_users";
    private final static ArrayList<String> GID_NUMBER_STRING_LIST = new ArrayList<String>();
    private final static ArrayList<String> GROUP_NAME_STRING_LIST = new ArrayList<String>();
    private final static ArrayList<String> UID_NUMBER_STRING_LIST = new ArrayList<String>();
    private final static ArrayList<String> HOME_DIRECTORY_LIST = new ArrayList<String>();
    private final static ArrayList<String> POSIXACCOUNT_OBJECTCLASS_LIST = new ArrayList<String>();
    private final static ArrayList<String> LOGIN_SHELL_LIST = new ArrayList<String>();
    private final static String GID_NUMBER = "5000";
    private final static String AMOS_GID_NUMBER = "5001";
    private final static String NEW_UID_NUMBER = "20000";
    private String baseDN;
    private StringBuffer dnPostfix = null;
    private final static HashMap<String, ArrayList<String>> attributeValuePairs = new HashMap<String, ArrayList<String>>();
    private static final Integer ANY_UID = 201234;
    private static final String AMOS_HOME_DIR_PREFIX = "/home/shared/";
    private static final String AMOS_LOGIN_SHELL = "/bin/bash";
    private static ArrayList<String> attrList = new ArrayList<String>();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.setProperty(IdmConstants.CONFIGURATION_PROPERTY, "./src/test/resources/datastore.properties");
        GROUP_NAME_STRING_LIST.add(FIELD_TECH_USER_GROUP_NAME);
        GID_NUMBER_STRING_LIST.add(GID_NUMBER);
        UID_NUMBER_STRING_LIST.add("20010");
        UID_NUMBER_STRING_LIST.add("20012");
        HOME_DIRECTORY_LIST.add(FIELD_TECH_USER_HOME);
        LOGIN_SHELL_LIST.add(IdmConstants.LDAP_NO_LOGIN_SHELL);
        POSIXACCOUNT_OBJECTCLASS_LIST.add(IdmConstants.LDAP_POSIXACCOUNT);

        attributeValuePairs.put(IdmConstants.LDAP_OBJECTCLASS, POSIXACCOUNT_OBJECTCLASS_LIST);
        attributeValuePairs.put(IdmConstants.LDAP_UID_NUMBER, UID_NUMBER_STRING_LIST);
        attributeValuePairs.put(IdmConstants.LDAP_GID_NUMBER, GID_NUMBER_STRING_LIST);
        attributeValuePairs.put(IdmConstants.LDAP_HOME_DIRECTORY, HOME_DIRECTORY_LIST);
        attributeValuePairs.put(IdmConstants.LDAP_LOGIN_SHELL, LOGIN_SHELL_LIST);

        attrList.add(IdmConstants.LDAP_UID);
        attrList.add(IdmConstants.LDAP_FULL_NAME);
        attrList.add(IdmConstants.LDAP_LAST_NAME);
        attrList.add(IdmConstants.LDAP_UID_NUMBER);
        attrList.add(IdmConstants.LDAP_GID_NUMBER);
        attrList.add(IdmConstants.LDAP_HOME_DIRECTORY);
        attrList.add(IdmConstants.LDAP_LOGIN_SHELL);
        attrList.add(IdmConstants.LDAP_ACCOUNT_EXPIRATION_TIME);
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        secManager = new SecurityManagerBean();
        secManager.logger = logger;
        systemRecorder = mock(SystemRecorder.class);
        secManager.systemRecorder = systemRecorder;
        baseDN = ConfigurationBean.get(IdmConstants.COM_INF_LDAP_ROOT_SUFFIX_PROPERTY, "dc=apache,dc=com");
    }

    @After
    public void tearDown() {
        //
    }

    @Test
    public void testAddPosixAttributesMethodNoUidNumberExist() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser1";
        final String userDN = makeUserDN(userName);
        final String groupDN = makeGroupDN();
        final HashMap<String, ArrayList<String>> avPairsToModify = new HashMap<String, ArrayList<String>>();
        final ArrayList<String> emptyList = new ArrayList<String>();
        avPairsToModify.put(IdmConstants.LDAP_OBJECTCLASS, POSIXACCOUNT_OBJECTCLASS_LIST);
        avPairsToModify.put(IdmConstants.LDAP_UID_NUMBER, getArrayList(NEW_UID_NUMBER));
        avPairsToModify.put(IdmConstants.LDAP_GID_NUMBER, GID_NUMBER_STRING_LIST);
        avPairsToModify.put(IdmConstants.LDAP_HOME_DIRECTORY, HOME_DIRECTORY_LIST);
        avPairsToModify.put(IdmConstants.LDAP_LOGIN_SHELL, LOGIN_SHELL_LIST);
        when(communicator.querySingleAttribute(userDN, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UID_NUMBER, null)).thenReturn(emptyList);

        when(communicator.querySingleAttribute(getDnPostfix().replaceFirst(",", ""), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER,
                null)).thenReturn(UID_NUMBER_STRING_LIST);
        when(communicator.querySingleAttribute(groupDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER, null))
                .thenReturn(UID_NUMBER_STRING_LIST);
        when(communicator.querySingleAttribute(userDN, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_GID_NUMBER, null)).thenReturn(emptyList);
        when(communicator.querySingleAttribute(groupDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                .thenReturn(GID_NUMBER_STRING_LIST);

        doNothing().when(communicator).modifyEntryAddMultipleAttributes(userDN, avPairsToModify);
        doNothing().when(communicator).modifyEntryAdd(groupDN, IdmConstants.LDAP_UNIQUE_MEMBER, userDN);
        final String result = secManager.addPosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME, FIELD_TECH_USER_HOME,
                IdmConstants.LDAP_NO_LOGIN_SHELL);
        verify(communicator).modifyEntryAddMultipleAttributes(userDN, avPairsToModify);
        verify(communicator).modifyEntryAdd(groupDN, IdmConstants.LDAP_UNIQUE_MEMBER, userDN);
        assertNull(result);
    }

    @Test
    public void testAddPosixAttributesMethodUidNumberExist() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser1";
        final String userDN = makeUserDN(userName);
        final String groupDN = makeGroupDN();
        final String oldUidNumber = "20004";
        final HashMap<String, ArrayList<String>> expectedAttributesToReplace = new HashMap<String, ArrayList<String>>();
        final ArrayList<String> nullGidNumber = new ArrayList<>();
        nullGidNumber.add(IdmConstants.LDAP_NULL_POSIX_GROUP_NUMBER);
        expectedAttributesToReplace.put(IdmConstants.LDAP_HOME_DIRECTORY, HOME_DIRECTORY_LIST);
        expectedAttributesToReplace.put(IdmConstants.LDAP_GID_NUMBER, GID_NUMBER_STRING_LIST);
        expectedAttributesToReplace.put(IdmConstants.LDAP_LOGIN_SHELL, LOGIN_SHELL_LIST);
        when(communicator.querySingleAttribute(userDN, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UID_NUMBER, null))
                .thenReturn(getArrayList(oldUidNumber));

        when(communicator.querySingleAttribute(getDnPostfix().replaceFirst(",", ""), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER,
                null)).thenReturn(UID_NUMBER_STRING_LIST);
        when(communicator.querySingleAttribute(groupDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER, null))
                .thenReturn(UID_NUMBER_STRING_LIST);
        when(communicator.querySingleAttribute(userDN, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_GID_NUMBER, null)).thenReturn(nullGidNumber);
        when(communicator.querySingleAttribute(groupDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                .thenReturn(GID_NUMBER_STRING_LIST);

        final String result = secManager.addPosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME, FIELD_TECH_USER_HOME,
                IdmConstants.LDAP_NO_LOGIN_SHELL);
        verify(communicator).modifyEntryReplaceMultipleAttributes(userDN, expectedAttributesToReplace);
        assertNull(result);
    }

    @Test
    public void testAddPosixAttributesMethodNoSuchUser() {
        final ArrayList<String> emptyList = new ArrayList<String>();
        final DSCommunicator communicator = mock(DSCommunicator.class);
        final String userName = "fieldTechUser2";
        final String userDN = makeUserDN(userName);
        final String groupDN = makeGroupDN();
        final int errorCode = IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY;
        final HashMap<String, ArrayList<String>> avPairsToModify = new HashMap<String, ArrayList<String>>();
        avPairsToModify.put(IdmConstants.LDAP_OBJECTCLASS, POSIXACCOUNT_OBJECTCLASS_LIST);
        avPairsToModify.put(IdmConstants.LDAP_UID_NUMBER, getArrayList("20000"));
        avPairsToModify.put(IdmConstants.LDAP_GID_NUMBER, GID_NUMBER_STRING_LIST);
        avPairsToModify.put(IdmConstants.LDAP_HOME_DIRECTORY, HOME_DIRECTORY_LIST);
        avPairsToModify.put(IdmConstants.LDAP_LOGIN_SHELL, LOGIN_SHELL_LIST);
        try {
            when(communicator.querySingleAttribute(userDN, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UID_NUMBER, null)).thenReturn(emptyList);
            when(communicator.querySingleAttribute(groupDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER, null))
                    .thenReturn(UID_NUMBER_STRING_LIST);
            when(communicator.querySingleAttribute(userDN, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_GID_NUMBER, null)).thenReturn(emptyList);
            when(communicator.querySingleAttribute(groupDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                    .thenReturn(GID_NUMBER_STRING_LIST);

            doThrow(new DSCommunicatorException("Entry " + userName + " does not exist", errorCode)).when(communicator)
                    .modifyEntryAddMultipleAttributes(userDN, avPairsToModify);
            doNothing().when(communicator).modifyEntryAdd(groupDN, IdmConstants.LDAP_UNIQUE_MEMBER, userDN);
            secManager.communicator = communicator;
        } catch (final DSCommunicatorException e) {
            fail("shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        }
        try {
            final String result = secManager.addPosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME, FIELD_TECH_USER_HOME,
                    IdmConstants.LDAP_NO_LOGIN_SHELL);
            fail("testAddPosixAttributesMethodNoSuchUser: should throw exception but msg : " + result);
        } catch (final IdentityManagementServiceException e) {
            assertTrue("testAddPosixAttributesMethodNoSuchUser ", e.getError() == IdentityManagementServiceException.Error.ENTRY_NOT_FOUND);
        }
    }

    @Test
    public void testAddPosixAttributesMethodTwice() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        final String userName = "fieldTechUser3";
        final String userDN = makeUserDN(userName);
        final String groupDN = makeGroupDN();
        final ArrayList<String> emptyList = new ArrayList<String>();
        final HashMap<String, ArrayList<String>> avPairsToModify = new HashMap<String, ArrayList<String>>();
        avPairsToModify.put(IdmConstants.LDAP_OBJECTCLASS, POSIXACCOUNT_OBJECTCLASS_LIST);
        avPairsToModify.put(IdmConstants.LDAP_UID_NUMBER, getArrayList(NEW_UID_NUMBER));
        avPairsToModify.put(IdmConstants.LDAP_GID_NUMBER, GID_NUMBER_STRING_LIST);
        avPairsToModify.put(IdmConstants.LDAP_HOME_DIRECTORY, HOME_DIRECTORY_LIST);
        avPairsToModify.put(IdmConstants.LDAP_LOGIN_SHELL, LOGIN_SHELL_LIST);

        final HashMap<String, ArrayList<String>> returnsAttributes = new HashMap<String, ArrayList<String>>();
        returnsAttributes.put(IdmConstants.LDAP_OBJECTCLASS, POSIXACCOUNT_OBJECTCLASS_LIST);
        returnsAttributes.put(IdmConstants.LDAP_UID_NUMBER, getArrayList(NEW_UID_NUMBER));
        returnsAttributes.put(IdmConstants.LDAP_GID_NUMBER, GID_NUMBER_STRING_LIST);
        returnsAttributes.put(IdmConstants.LDAP_HOME_DIRECTORY, HOME_DIRECTORY_LIST);
        returnsAttributes.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList("bin/bash"));

        try {

            when(communicator.querySingleAttribute(userDN, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UID_NUMBER, null)).thenReturn(emptyList);
            when(communicator.querySingleAttribute(groupDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER, null))
                    .thenReturn(UID_NUMBER_STRING_LIST);
            when(communicator.querySingleAttribute(userDN, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                    .thenReturn(getArrayList("5013"));
            when(communicator.querySingleAttribute(groupDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                    .thenReturn(GID_NUMBER_STRING_LIST);

            when(communicator.queryMultipleAttributes(userDN, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(returnsAttributes);
            secManager.communicator = communicator;
        } catch (final DSCommunicatorException e) {
            fail("shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        }
        try {
            final String result = secManager.addPosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME, FIELD_TECH_USER_HOME,
                    IdmConstants.LDAP_NO_LOGIN_SHELL);
            fail("testAddPosixAttributesMethodTwice: should throw exception but msg : " + result);
        } catch (final IdentityManagementServiceException e) {
            assertTrue("testAddPosixAttributesMethodTwice ", e.getError() == IdentityManagementServiceException.Error.ATTR_OR_VALUE_ALREADY_EXISTS);
        }
    }

    @Test
    public void testAddPosixAttributesMethodFailOnUniqueMemberAlreadyExist() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser4";
        final String userDN = makeUserDN(userName);
        final String groupDN = makeGroupDN();
        final HashMap<String, ArrayList<String>> avPairsToModify = new HashMap<String, ArrayList<String>>();
        avPairsToModify.put(IdmConstants.LDAP_OBJECTCLASS, POSIXACCOUNT_OBJECTCLASS_LIST);
        avPairsToModify.put(IdmConstants.LDAP_UID_NUMBER, getArrayList(NEW_UID_NUMBER));
        avPairsToModify.put(IdmConstants.LDAP_GID_NUMBER, GID_NUMBER_STRING_LIST);
        avPairsToModify.put(IdmConstants.LDAP_HOME_DIRECTORY, HOME_DIRECTORY_LIST);
        avPairsToModify.put(IdmConstants.LDAP_LOGIN_SHELL, LOGIN_SHELL_LIST);
        try {
            when(communicator.querySingleAttribute(groupDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER, null))
                    .thenReturn(UID_NUMBER_STRING_LIST);
            when(communicator.querySingleAttribute(userDN, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                    .thenReturn(GID_NUMBER_STRING_LIST);
            when(communicator.querySingleAttribute(groupDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                    .thenReturn(GID_NUMBER_STRING_LIST);
        } catch (final DSCommunicatorException e) {
            fail("testAddPosixAttributesMethodFailOnUniqueMemberAlreadyExist, shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        }
        try {
            final String result = secManager.addPosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME, FIELD_TECH_USER_HOME,
                    IdmConstants.LDAP_NO_LOGIN_SHELL);
            fail("testAddPosixAttributesMethodTwice: should throw exception but msg : " + result);
        } catch (final IdentityManagementServiceException e) {
            assertTrue("testAddPosixAttributesMethodTwice ", e.getError() == IdentityManagementServiceException.Error.ATTR_OR_VALUE_ALREADY_EXISTS);
        }
    }

    @Test
    public void testAddPosixAttributesMethodFailOnUniqueMemberError() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser5";
        final String userDN = makeUserDN(userName);
        final String groupDN = makeGroupDN();
        final ArrayList<String> emptyList = new ArrayList<String>();
        final HashMap<String, ArrayList<String>> avPairsToModify = new HashMap<String, ArrayList<String>>();
        avPairsToModify.put(IdmConstants.LDAP_OBJECTCLASS, POSIXACCOUNT_OBJECTCLASS_LIST);
        avPairsToModify.put(IdmConstants.LDAP_UID_NUMBER, getArrayList(NEW_UID_NUMBER));
        avPairsToModify.put(IdmConstants.LDAP_GID_NUMBER, GID_NUMBER_STRING_LIST);
        avPairsToModify.put(IdmConstants.LDAP_HOME_DIRECTORY, HOME_DIRECTORY_LIST);
        avPairsToModify.put(IdmConstants.LDAP_LOGIN_SHELL, LOGIN_SHELL_LIST);
        try {
            when(communicator.querySingleAttribute(userDN, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UID_NUMBER, null)).thenReturn(emptyList);
            when(communicator.querySingleAttribute(groupDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER, null))
                    .thenReturn(UID_NUMBER_STRING_LIST);
            when(communicator.querySingleAttribute(userDN, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_GID_NUMBER, null)).thenReturn(emptyList);
            when(communicator.querySingleAttribute(groupDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                    .thenReturn(GID_NUMBER_STRING_LIST);

            doNothing().when(communicator).modifyEntryAddMultipleAttributes(userDN, avPairsToModify);
            doThrow(new DSCommunicatorException("adding unique member is not performed", IdmConstants.IDMS_UNEXPECTED_ERROR)).when(communicator)
                    .modifyEntryAdd(groupDN, IdmConstants.LDAP_UNIQUE_MEMBER, userDN);

        } catch (final DSCommunicatorException e) {
            fail("testAddPosixAttributesMethodFailOnUniqueMemberError, shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        }
        final String result = secManager.addPosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME, FIELD_TECH_USER_HOME,
                IdmConstants.LDAP_NO_LOGIN_SHELL);
        logger.info("testAddPosixAttributesMethodFailOnUniqueMemberError: result {}", result);
        assertNotNull(result);
    }

    @Test(expected = IdentityManagementServiceException.class)
    public void testAddPosixAttributesConnectionFailure() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser5";
        final String userDN = makeUserDN(userName);
        final String groupDN = makeGroupDN();

        try {
            doThrow(new DSCommunicatorException("connection failure", IdmConstants.LDAP_ERROR_CONNECTION_FAILURE)).when(communicator)
                    .querySingleAttribute(userDN.toString(), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_GID_NUMBER, null);

            when(communicator.querySingleAttribute(groupDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER, null))
                    .thenReturn(UID_NUMBER_STRING_LIST);

        } catch (final DSCommunicatorException e) {
            fail("testAddPosixAttributesConnectionFailure, shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        }
        secManager.addPosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME, FIELD_TECH_USER_HOME);
    }

    @Test(expected = IdentityManagementServiceException.class)
    public void testAddPosixAttributesGetUidNumberConnectionFailure() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser5";
        final String userDN = makeUserDN(userName);
        final String groupDN = makeGroupDN();

        try {
            doThrow(new DSCommunicatorException("connection failure", IdmConstants.LDAP_ERROR_CONNECTION_FAILURE)).when(communicator)
                    .querySingleAttribute(userDN.toString(), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UID_NUMBER, null);
        } catch (final DSCommunicatorException e) {
            fail("testAddPosixAttributesConnectionFailure, shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        }
        secManager.addPosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME, FIELD_TECH_USER_HOME);
    }

    @Test
    public void testRemovePosixAttributesMethod() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser1";
        final String userDN = makeUserDN(userName);
        final ArrayList<String> membersOfGrop = new ArrayList<String>();
        membersOfGrop.add(userDN);

        final HashMap<String, ArrayList<String>> expectedAttributeToReplace = new HashMap<String, ArrayList<String>>();
        expectedAttributeToReplace.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(IdmConstants.LDAP_NO_LOGIN_SHELL));
        expectedAttributeToReplace.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(IdmConstants.LDAP_NULL_POSIX_GROUP_NUMBER));
        expectedAttributeToReplace.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(IdmConstants.LDAP_NULL_HOME_DIRECTORY));

        when(communicator.querySingleAttribute(makeGroupDN(), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER, null))
                .thenReturn(membersOfGrop);
        when(communicator.queryMultipleAttributes(userDN, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(attributeValuePairs);

        final String result = secManager.removePosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME);

        verify(communicator).modifyEntryReplaceMultipleAttributes(userDN, expectedAttributeToReplace);
        verify(communicator).modifyEntryDelete(makeGroupDN(), IdmConstants.LDAP_UNIQUE_MEMBER, userDN);

        assertNull(result);

    }

    @Test
    public void testRemovePosixAttributesMethodUserBelongToMoreThenOneGroupPrimaryGroup() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser1";
        final String userDN = makeUserDN(userName);
        final ArrayList<String> membersOfGrop = new ArrayList<String>();

        membersOfGrop.add(userDN);
        final ArrayList<String> amosGidNumber = new ArrayList<>();
        amosGidNumber.add(AMOS_GID_NUMBER);
        final HashMap<String, ArrayList<String>> avPairsToModify = new HashMap<String, ArrayList<String>>();
        avPairsToModify.put(IdmConstants.LDAP_OBJECTCLASS, POSIXACCOUNT_OBJECTCLASS_LIST);
        avPairsToModify.put(IdmConstants.LDAP_GID_NUMBER, GID_NUMBER_STRING_LIST);
        avPairsToModify.put(IdmConstants.LDAP_HOME_DIRECTORY, HOME_DIRECTORY_LIST);
        avPairsToModify.put(IdmConstants.LDAP_LOGIN_SHELL, LOGIN_SHELL_LIST);

        when(communicator.querySingleAttribute(makeGroupDN(FIELD_TECH_USER_GROUP_NAME), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER,
                null)).thenReturn(membersOfGrop);
        when(communicator.querySingleAttribute(makeGroupDN(AMOS_USER_GROUP_NAME), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER, null))
                .thenReturn(membersOfGrop);
        when(communicator.queryMultipleAttributes(userDN, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(avPairsToModify);
        when(communicator.querySingleAttribute(makeGroupDN(FIELD_TECH_USER_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER,
                null)).thenReturn(GID_NUMBER_STRING_LIST);
        when(communicator.querySingleAttribute(makeGroupDN(AMOS_USER_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                .thenReturn(amosGidNumber);

        doNothing().when(communicator).modifyEntryDelete(makeGroupDN(), IdmConstants.LDAP_UNIQUE_MEMBER, userDN);
        doNothing().when(communicator).modifyEntryRemoveMultipleAttributes(userDN, avPairsToModify);
        final String result = secManager.removePosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME);
        verify(communicator).modifyEntryReplace(userDN, IdmConstants.LDAP_GID_NUMBER, AMOS_GID_NUMBER);
        verify(communicator).modifyEntryDelete(makeGroupDN(), IdmConstants.LDAP_UNIQUE_MEMBER, userDN);

        assertNull(result);

    }

    @Test
    public void testRemovePosixAttributesMethodUserBelongToMoreThenOneGroupCaseSecondaryGroup() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser1";
        final String userDN = makeUserDN(userName);
        final ArrayList<String> membersOfGrop = new ArrayList<String>();

        membersOfGrop.add(userDN);
        final ArrayList<String> amosGidNumber = new ArrayList<>();
        amosGidNumber.add(AMOS_GID_NUMBER);
        final HashMap<String, ArrayList<String>> avPairsToModify = new HashMap<String, ArrayList<String>>();
        avPairsToModify.put(IdmConstants.LDAP_OBJECTCLASS, POSIXACCOUNT_OBJECTCLASS_LIST);
        avPairsToModify.put(IdmConstants.LDAP_GID_NUMBER, amosGidNumber);
        avPairsToModify.put(IdmConstants.LDAP_HOME_DIRECTORY, HOME_DIRECTORY_LIST);
        avPairsToModify.put(IdmConstants.LDAP_LOGIN_SHELL, LOGIN_SHELL_LIST);

        when(communicator.querySingleAttribute(makeGroupDN(FIELD_TECH_USER_GROUP_NAME), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER,
                null)).thenReturn(membersOfGrop);
        when(communicator.querySingleAttribute(makeGroupDN(AMOS_USER_GROUP_NAME), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER, null))
                .thenReturn(membersOfGrop);
        when(communicator.queryMultipleAttributes(userDN, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(avPairsToModify);
        when(communicator.querySingleAttribute(makeGroupDN(FIELD_TECH_USER_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER,
                null)).thenReturn(GID_NUMBER_STRING_LIST);
        when(communicator.querySingleAttribute(makeGroupDN(AMOS_USER_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                .thenReturn(amosGidNumber);

        doNothing().when(communicator).modifyEntryDelete(makeGroupDN(), IdmConstants.LDAP_UNIQUE_MEMBER, userDN);
        doNothing().when(communicator).modifyEntryRemoveMultipleAttributes(userDN, avPairsToModify);
        final String result = secManager.removePosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME);
        verify(communicator, never()).modifyEntryReplace(userDN, IdmConstants.LDAP_GID_NUMBER, AMOS_GID_NUMBER);
        verify(communicator, never()).modifyEntryReplace(userDN, IdmConstants.LDAP_GID_NUMBER, GID_NUMBER);

        verify(communicator).modifyEntryDelete(makeGroupDN(), IdmConstants.LDAP_UNIQUE_MEMBER, userDN);

        assertNull(result);

    }

    @Test
    public void testRemovePosixAttributesMethodTwice() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser2";
        final String userDN = makeUserDN(userName);
        final ArrayList<String> membersOfGrop = new ArrayList<String>();
        final HashMap<String, ArrayList<String>> avPairsToModify = new HashMap<String, ArrayList<String>>();

        try {
            when(communicator.querySingleAttribute(makeGroupDN(), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER, null))
                    .thenReturn(membersOfGrop);
            when(communicator.queryMultipleAttributes(userDN, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(avPairsToModify);

            doNothing().when(communicator).modifyEntryDelete(makeGroupDN(), IdmConstants.LDAP_UNIQUE_MEMBER, userDN);
            doNothing().when(communicator).modifyEntryRemoveMultipleAttributes(userDN, avPairsToModify);

            final String result = secManager.removePosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME);
            fail("testRemovePosixAttributesMethodTwice: should throw exception but msg : " + result);
        } catch (final DSCommunicatorException e) {
            fail("shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        } catch (final IdentityManagementServiceException e) {
            assertTrue("testRemovePosixAttributesMethodTwice ", e.getError() == IdentityManagementServiceException.Error.NO_SUCH_ATTRIBUTE);
        }
    }

    @Test
    public void testRemovePosixAttributesMethodNoSuchUser() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser3";
        final String userDN = makeUserDN(userName);
        final ArrayList<String> membersOfGrop = new ArrayList<String>();
        membersOfGrop.add(userDN);
        final HashMap<String, ArrayList<String>> avPairsToModify = new HashMap<String, ArrayList<String>>();
        avPairsToModify.put(IdmConstants.LDAP_OBJECTCLASS, POSIXACCOUNT_OBJECTCLASS_LIST);
        avPairsToModify.put(IdmConstants.LDAP_GID_NUMBER, GID_NUMBER_STRING_LIST);
        avPairsToModify.put(IdmConstants.LDAP_HOME_DIRECTORY, HOME_DIRECTORY_LIST);
        avPairsToModify.put(IdmConstants.LDAP_LOGIN_SHELL, LOGIN_SHELL_LIST);

        final HashMap<String, ArrayList<String>> attributesToReplace = new HashMap<>();
        attributesToReplace.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(IdmConstants.LDAP_NO_LOGIN_SHELL));
        attributesToReplace.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(IdmConstants.LDAP_NULL_POSIX_GROUP_NUMBER));
        attributesToReplace.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(IdmConstants.LDAP_NULL_HOME_DIRECTORY));

        final int errorCode = IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY;

        try {
            when(communicator.querySingleAttribute(makeGroupDN(), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER, null))
                    .thenReturn(membersOfGrop);
            when(communicator.queryMultipleAttributes(userDN, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(avPairsToModify);
            doNothing().when(communicator).modifyEntryDelete(makeGroupDN(), IdmConstants.LDAP_UNIQUE_MEMBER, userDN);
            doThrow(new DSCommunicatorException("Entry " + userName + " does not exist", errorCode)).when(communicator)
                    .modifyEntryReplaceMultipleAttributes(userDN, attributesToReplace);
            final String result = secManager.removePosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME);
            fail("testRemovePosixAttributesMethodNoSuchUser: should throw exception but msg : " + result);
        } catch (final DSCommunicatorException e) {
            fail("shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        } catch (final IdentityManagementServiceException e) {
            assertTrue("testRemovePosixAttributesMethodNoSuchUser ", e.getError() == IdentityManagementServiceException.Error.ENTRY_NOT_FOUND);
        }
    }

    @Test
    public void testRemovePosixAttributesMethodUniqueMemberNoSuchAttribute() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser4";
        final String userDN = makeUserDN(userName);
        final ArrayList<String> membersOfGrop = new ArrayList<String>();
        membersOfGrop.add(userDN);
        final HashMap<String, ArrayList<String>> avPairsToModify = new HashMap<String, ArrayList<String>>();
        avPairsToModify.put(IdmConstants.LDAP_OBJECTCLASS, POSIXACCOUNT_OBJECTCLASS_LIST);
        avPairsToModify.put(IdmConstants.LDAP_UID_NUMBER, getArrayList(NEW_UID_NUMBER));
        avPairsToModify.put(IdmConstants.LDAP_GID_NUMBER, GID_NUMBER_STRING_LIST);
        avPairsToModify.put(IdmConstants.LDAP_HOME_DIRECTORY, HOME_DIRECTORY_LIST);
        avPairsToModify.put(IdmConstants.LDAP_LOGIN_SHELL, LOGIN_SHELL_LIST);

        try {
            when(communicator.querySingleAttribute(makeGroupDN(), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER, null))
                    .thenReturn(membersOfGrop);
            when(communicator.queryMultipleAttributes(userDN, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(avPairsToModify);
            doThrow(new DSCommunicatorException("removing unique member is not performed", IdmConstants.LDAP_ERROR_NO_SUCH_ATTRIBUTE))
                    .when(communicator).modifyEntryDelete(makeGroupDN(), IdmConstants.LDAP_UNIQUE_MEMBER, userDN);
            doNothing().when(communicator).modifyEntryRemoveMultipleAttributes(userDN, avPairsToModify);
            final String result = secManager.removePosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME);
            assertNull(result);
        } catch (final DSCommunicatorException e) {
            fail("shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        } catch (final IdentityManagementServiceException e) {
            fail("testRemovePosixAttributesMethodUniqueMemberNoSuchAttribute: should not throw exception :" + e.getMessage());
        }
    }

    @Test
    public void testRemovePosixAttributesMethodUniqueMemberError() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser5";
        final String userDN = makeUserDN(userName);
        final ArrayList<String> membersOfGrop = new ArrayList<String>();
        membersOfGrop.add(userDN);
        final HashMap<String, ArrayList<String>> avPairsToModify = new HashMap<String, ArrayList<String>>();
        avPairsToModify.put(IdmConstants.LDAP_OBJECTCLASS, POSIXACCOUNT_OBJECTCLASS_LIST);
        avPairsToModify.put(IdmConstants.LDAP_UID_NUMBER, getArrayList(NEW_UID_NUMBER));
        avPairsToModify.put(IdmConstants.LDAP_GID_NUMBER, GID_NUMBER_STRING_LIST);
        avPairsToModify.put(IdmConstants.LDAP_HOME_DIRECTORY, HOME_DIRECTORY_LIST);
        avPairsToModify.put(IdmConstants.LDAP_LOGIN_SHELL, LOGIN_SHELL_LIST);

        try {
            when(communicator.querySingleAttribute(makeGroupDN(), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER, null))
                    .thenReturn(membersOfGrop);
            when(communicator.queryMultipleAttributes(userDN, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(avPairsToModify);
            doThrow(new DSCommunicatorException("removing unique member is not performed", IdmConstants.LDAP_ERROR_CONNECTION_FAILURE))
                    .when(communicator).modifyEntryDelete(makeGroupDN(), IdmConstants.LDAP_UNIQUE_MEMBER, userDN);
            doNothing().when(communicator).modifyEntryRemoveMultipleAttributes(userDN, avPairsToModify);
            final String result = secManager.removePosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME);
            assertNotNull(result);
        } catch (final DSCommunicatorException e) {
            fail("shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        } catch (final IdentityManagementServiceException e) {
            fail("testRemovePosixAttributesMethodUniqueMemberError: should not throw exception :" + e.getMessage());
        }
    }

    @Test(expected = IdentityManagementServiceException.class)
    public void testRemovePosixAttributesMethodConnectionFailure() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser1";
        final String userDN = makeUserDN(userName);
        final ArrayList<String> membersOfGrop = new ArrayList<String>();
        membersOfGrop.add(userDN);

        final HashMap<String, ArrayList<String>> expectedAttributeToReplace = new HashMap<String, ArrayList<String>>();
        expectedAttributeToReplace.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(IdmConstants.LDAP_NO_LOGIN_SHELL));
        expectedAttributeToReplace.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(IdmConstants.LDAP_NULL_POSIX_GROUP_NUMBER));
        expectedAttributeToReplace.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(IdmConstants.LDAP_NULL_HOME_DIRECTORY));

        when(communicator.querySingleAttribute(makeGroupDN(), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER, null))
                .thenReturn(membersOfGrop);

        doThrow(new DSCommunicatorException("test remove posix attributes connection failure", IdmConstants.LDAP_ERROR_CONNECTION_FAILURE))
                .when(communicator).queryMultipleAttributes(userDN, SearchControls.OBJECT_SCOPE, attrList);

        secManager.removePosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME);
    }


    @Test(expected = IdentityManagementServiceException.class)
    public void testRemovePosixAttributesMethodHomeDirNull() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "fieldTechUser5";
        final String userDN = makeUserDN(userName);
        final ArrayList<String> membersOfGrop = new ArrayList<String>();
        membersOfGrop.add(userDN);
        final HashMap<String, ArrayList<String>> avPairsToModify = new HashMap<String, ArrayList<String>>();
        avPairsToModify.put(IdmConstants.LDAP_OBJECTCLASS, POSIXACCOUNT_OBJECTCLASS_LIST);
        avPairsToModify.put(IdmConstants.LDAP_UID_NUMBER, getArrayList(NEW_UID_NUMBER));
        avPairsToModify.put(IdmConstants.LDAP_GID_NUMBER, GID_NUMBER_STRING_LIST);
        avPairsToModify.put(IdmConstants.LDAP_LOGIN_SHELL, LOGIN_SHELL_LIST);

        try {
            when(communicator.querySingleAttribute(makeGroupDN(), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER, null))
                    .thenReturn(membersOfGrop);
            when(communicator.queryMultipleAttributes(userDN, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(avPairsToModify);
            secManager.removePosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME);
        } catch (final DSCommunicatorException e) {
            fail("shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Test
    public void testAddPosixAttributes_addToComGroup_notAssignedToAnyGroupsBefore_ShouldAddShadowAccountObjectClass() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final ArrayList<String> emptyList = new ArrayList<String>();
        final String userName = "any_name";

        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UID_NUMBER, null))
                .thenReturn(emptyList);
        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                .thenReturn(emptyList);
        when(communicator.querySingleAttribute(makeGroupDN(IdmConstants.COM_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER,
                null)).thenReturn(getArrayList(IdmConstants.COM_GROUP_ID));
        doNothing().when(communicator).modifyEntryAddMultipleAttributes(anyString(), any(HashMap.class));
        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_OBJECTCLASS, null))
                .thenReturn(getArrayList("not_shadow_account"));
        doNothing().when(communicator).modifyEntryAdd(anyString(), anyString(), anyString());

        final String result = secManager.addPosixAttributes(PEOPLE_CONTAINER, userName, IdmConstants.COM_GROUP_NAME,
                IdmConstants.LDAP_NULL_HOME_DIRECTORY, IdmConstants.LDAP_NO_LOGIN_SHELL);

        assertNull(result);
        verify(communicator).modifyEntryAdd(makeGroupDN(IdmConstants.COM_GROUP_NAME), IdmConstants.LDAP_MEMBER_UID, userName);
        verify(communicator).modifyEntryAdd(makeUserDN(userName), IdmConstants.LDAP_OBJECTCLASS, IdmConstants.LDAP_SHADOWACCOUNT);
    }

    @Test
    public void testAddPosixAttributes_addToAnyGroup_userOnlyInComGroupBefore_ShouldModifyHomeDirAndLoginShell() throws DSCommunicatorException {
        logger.info("testAddPosixAttributes_addToAnyGroup_userOnlyInComGroupBefore_ShouldModifyHomeDirAndLoginShell");
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "any_name";
        final HashMap<String, ArrayList<String>> attributesToReplace = new HashMap<String, ArrayList<String>>();
        attributesToReplace.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(FIELD_TECH_USER_HOME));
        attributesToReplace.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(IdmConstants.LDAP_NO_LOGIN_SHELL));

        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UID_NUMBER, null))
                .thenReturn(getArrayList(ANY_UID.toString()));
        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                .thenReturn(getArrayList(IdmConstants.COM_GROUP_ID));
        when(communicator.querySingleAttribute(makeGroupDN(FIELD_TECH_USER_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER,
                null)).thenReturn(getArrayList(GID_NUMBER));
        when(communicator.querySingleAttribute(makeGroupDN(IdmConstants.COM_GROUP_NAME), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_MEMBER_UID,
                null)).thenReturn(getArrayList(userName));
        doNothing().when(communicator).modifyEntryAddMultipleAttributes(anyString(), any(HashMap.class));
        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_OBJECTCLASS, null))
                .thenReturn(getArrayList(IdmConstants.LDAP_SHADOWACCOUNT));
        doNothing().when(communicator).modifyEntryAdd(anyString(), anyString(), anyString());

        final String result = secManager.addPosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME, FIELD_TECH_USER_HOME,
                IdmConstants.LDAP_NO_LOGIN_SHELL);

        assertNull(result);
        verify(communicator).modifyEntryReplaceMultipleAttributes(makeUserDN(userName), attributesToReplace);
        verify(communicator).modifyEntryAdd(makeGroupDN(FIELD_TECH_USER_GROUP_NAME), IdmConstants.LDAP_UNIQUE_MEMBER, makeUserDN(userName));
    }

    @Test
    public void testAddPosixAttributes_addToComGroup_userInNotComGroupBefore_ShouldNotModifyHomeDirAndLoginShell() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "any_name";

        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UID_NUMBER, null))
                .thenReturn(getArrayList(ANY_UID.toString()));
        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                .thenReturn(getArrayList(GID_NUMBER));
        when(communicator.querySingleAttribute(makeGroupDN(IdmConstants.COM_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER,
                null)).thenReturn(getArrayList(IdmConstants.COM_GROUP_ID));
        when(communicator.querySingleAttribute(makeGroupDN(FIELD_TECH_USER_GROUP_NAME), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER,
                null)).thenReturn(getArrayList(makeUserDN(userName)));
        doNothing().when(communicator).modifyEntryAddMultipleAttributes(anyString(), any(HashMap.class));
        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_OBJECTCLASS, null))
                .thenReturn(getArrayList(IdmConstants.LDAP_SHADOWACCOUNT));
        doNothing().when(communicator).modifyEntryAdd(anyString(), anyString(), anyString());

        final String result = secManager.addPosixAttributes(PEOPLE_CONTAINER, userName, IdmConstants.COM_GROUP_NAME,
                IdmConstants.LDAP_NULL_HOME_DIRECTORY, IdmConstants.LDAP_NO_LOGIN_SHELL);

        assertNull(result);
        verify(communicator, times(0)).modifyEntryReplaceMultipleAttributes(anyString(), any(HashMap.class));
    }

    @Test(expected = IdentityManagementServiceException.class)
    public void testAddPosixAttributes_addToThirdGroup_userHasComGid_ShouldThrowException() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "any_name";
        final HashMap<String, ArrayList<String>> attributes = new HashMap<>();
        attributes.put(IdmConstants.LDAP_UID_NUMBER, getArrayList(ANY_UID.toString()));
        attributes.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(IdmConstants.COM_GROUP_ID));
        attributes.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(FIELD_TECH_USER_HOME));
        attributes.put(IdmConstants.LDAP_LOGIN_SHELL, LOGIN_SHELL_LIST);

        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UID_NUMBER, null))
                .thenReturn(getArrayList(ANY_UID.toString()));
        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                .thenReturn(getArrayList(IdmConstants.COM_GROUP_ID));
        when(communicator.querySingleAttribute(makeGroupDN(IdmConstants.COM_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER,
                null)).thenReturn(getArrayList(IdmConstants.COM_GROUP_ID));
        when(communicator.querySingleAttribute(makeGroupDN(FIELD_TECH_USER_GROUP_NAME), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER,
                null)).thenReturn(getArrayList(makeUserDN(userName)));
        when(communicator.querySingleAttribute(makeGroupDN(IdmConstants.COM_GROUP_NAME), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_MEMBER_UID,
                null)).thenReturn(getArrayList(userName));
        when(communicator.querySingleAttribute(makeGroupDN(AMOS_USER_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                .thenReturn(getArrayList(AMOS_GID_NUMBER));
        when(communicator.queryMultipleAttributes(makeUserDN(userName), SearchControls.OBJECT_SCOPE, attrList)).thenReturn(attributes);
        doNothing().when(communicator).modifyEntryAddMultipleAttributes(anyString(), any(HashMap.class));
        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_OBJECTCLASS, null))
                .thenReturn(getArrayList(IdmConstants.LDAP_SHADOWACCOUNT));
        doNothing().when(communicator).modifyEntryAdd(anyString(), anyString(), anyString());

        try {
            secManager.addPosixAttributes(PEOPLE_CONTAINER, userName, AMOS_USER_GROUP_NAME, AMOS_HOME_DIR_PREFIX + userName, AMOS_LOGIN_SHELL);
            fail();
        } catch (final IdentityManagementServiceException e) {
            assertTrue(e.getMessage().contains("Assigned attributes are different from the new role attributes"));
            throw e;

        }
    }

    @Test(expected = IdentityManagementServiceException.class)
    public void testAddPosixAttributes_addToSmrsGroup_userInAmosGroupBefore_ShouldThrowException() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "any_name";
        final HashMap<String, ArrayList<String>> attributes = new HashMap<>();
        attributes.put(IdmConstants.LDAP_UID_NUMBER, getArrayList(ANY_UID.toString()));
        attributes.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(AMOS_GID_NUMBER));
        attributes.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(AMOS_HOME_DIR_PREFIX + userName));
        attributes.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(AMOS_LOGIN_SHELL));

        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UID_NUMBER, null))
                .thenReturn(getArrayList(ANY_UID.toString()));
        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                .thenReturn(getArrayList(AMOS_GID_NUMBER));
        when(communicator.querySingleAttribute(makeGroupDN(AMOS_USER_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                .thenReturn(getArrayList(AMOS_GID_NUMBER));
        when(communicator.querySingleAttribute(makeGroupDN(FIELD_TECH_USER_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER,
                null)).thenReturn(getArrayList(GID_NUMBER));
        when(communicator.queryMultipleAttributes(makeUserDN(userName), SearchControls.OBJECT_SCOPE, attrList)).thenReturn(attributes);
        doNothing().when(communicator).modifyEntryAddMultipleAttributes(anyString(), any(HashMap.class));
        when(communicator.querySingleAttribute(makeUserDN(userName), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_OBJECTCLASS, null))
                .thenReturn(getArrayList("not_shadow_account"));
        doNothing().when(communicator).modifyEntryAdd(anyString(), anyString(), anyString());

        try {
            secManager.addPosixAttributes(FIELD_TECH_USER_CONTAINER, userName, FIELD_TECH_USER_GROUP_NAME, FIELD_TECH_USER_HOME,
                    IdmConstants.LDAP_NO_LOGIN_SHELL);
            fail();
        } catch (final IdentityManagementServiceException e) {
            assertTrue(e.getMessage().contains("Assigned attributes are different from the new role attributes"));
            throw e;
        }
    }

    @Test
    public void testRemovePosixAttributes_removeFromComGroup_HadOnlyOneGroup_ShouldRemoveShadowAccountObjectClass() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "any_name";
        final String userDN = makeUserDN(userName);

        final HashMap<String, ArrayList<String>> expectedAttributeToReplace = new HashMap<String, ArrayList<String>>();
        expectedAttributeToReplace.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(IdmConstants.LDAP_NO_LOGIN_SHELL));
        expectedAttributeToReplace.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(IdmConstants.LDAP_NULL_POSIX_GROUP_NUMBER));
        expectedAttributeToReplace.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(IdmConstants.LDAP_NULL_HOME_DIRECTORY));

        when(communicator.querySingleAttribute(makeGroupDN(IdmConstants.COM_GROUP_NAME), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_MEMBER_UID,
                null)).thenReturn(getArrayList(userName));
        attributeValuePairs.put(IdmConstants.LDAP_OBJECTCLASS, getArrayList(IdmConstants.LDAP_SHADOWACCOUNT));
        when(communicator.queryMultipleAttributes(userDN, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(attributeValuePairs);
        doNothing().when(communicator).modifyEntryReplaceMultipleAttributes(userDN, expectedAttributeToReplace);
        doNothing().when(communicator).modifyEntryDelete(userDN, IdmConstants.LDAP_OBJECTCLASS, IdmConstants.LDAP_SHADOWACCOUNT);

        final String result = secManager.removePosixAttributes(PEOPLE_CONTAINER, userName, IdmConstants.COM_GROUP_NAME);

        assertNull(result);
        verify(communicator).modifyEntryReplaceMultipleAttributes(userDN, expectedAttributeToReplace);
        verify(communicator).modifyEntryDelete(userDN, IdmConstants.LDAP_OBJECTCLASS, IdmConstants.LDAP_SHADOWACCOUNT);
        verify(communicator).modifyEntryDelete(makeGroupDN(IdmConstants.COM_GROUP_NAME), IdmConstants.LDAP_MEMBER_UID, userName);
    }

    @Test
    public void testRemovePosixAttributes_removeFromComGroup_HadMultipleGroups_ShouldRemoveShadowAccountObjectClass() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "any_name";
        final String userDN = makeUserDN(userName);

        final HashMap<String, ArrayList<String>> expectedAttributeToReplace = new HashMap<String, ArrayList<String>>();
        expectedAttributeToReplace.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(IdmConstants.LDAP_NO_LOGIN_SHELL));
        expectedAttributeToReplace.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(IdmConstants.LDAP_NULL_POSIX_GROUP_NUMBER));
        expectedAttributeToReplace.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(IdmConstants.LDAP_NULL_HOME_DIRECTORY));

        when(communicator.querySingleAttribute(makeGroupDN(IdmConstants.COM_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER,
                null)).thenReturn(getArrayList(IdmConstants.COM_GROUP_ID));
        when(communicator.querySingleAttribute(makeGroupDN(AMOS_USER_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                .thenReturn(getArrayList(AMOS_GID_NUMBER));
        when(communicator.querySingleAttribute(makeGroupDN(IdmConstants.COM_GROUP_NAME), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_MEMBER_UID,
                null)).thenReturn(getArrayList(userName));
        when(communicator.querySingleAttribute(makeGroupDN(AMOS_USER_GROUP_NAME), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER, null))
                .thenReturn(getArrayList(userDN));
        attributeValuePairs.put(IdmConstants.LDAP_OBJECTCLASS, getArrayList(IdmConstants.LDAP_SHADOWACCOUNT));
        attributeValuePairs.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(IdmConstants.COM_GROUP_ID));
        when(communicator.queryMultipleAttributes(userDN, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(attributeValuePairs);
        doNothing().when(communicator).modifyEntryReplaceMultipleAttributes(userDN, expectedAttributeToReplace);
        doNothing().when(communicator).modifyEntryDelete(userDN, IdmConstants.LDAP_OBJECTCLASS, IdmConstants.LDAP_SHADOWACCOUNT);

        final String result = secManager.removePosixAttributes(PEOPLE_CONTAINER, userName, IdmConstants.COM_GROUP_NAME);

        assertNull(result);
        verify(communicator).modifyEntryDelete(userDN, IdmConstants.LDAP_OBJECTCLASS, IdmConstants.LDAP_SHADOWACCOUNT);
        verify(communicator).modifyEntryDelete(makeGroupDN(IdmConstants.COM_GROUP_NAME), IdmConstants.LDAP_MEMBER_UID, userName);

        verify(communicator).querySingleAttribute(makeGroupDN(IdmConstants.COM_GROUP_NAME), SearchControls.SUBTREE_SCOPE,
                IdmConstants.LDAP_GID_NUMBER, null);
        verify(communicator).querySingleAttribute(makeGroupDN(AMOS_USER_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER,
                null);
    }

    @Test
    public void testRemovePosixAttributes_removeFromNotComGroup_HadTwoGroups_OneOfThemWasComGroup_ShouldModifyHomeDirAndLoginShell()
            throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "any_name";
        final String userDN = makeUserDN(userName);

        final HashMap<String, ArrayList<String>> attributesToModify = new HashMap<>();
        attributesToModify.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(IdmConstants.LDAP_NO_LOGIN_SHELL));
        attributesToModify.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(IdmConstants.COM_GROUP_ID));
        attributesToModify.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(IdmConstants.LDAP_NULL_HOME_DIRECTORY));

        final HashMap<String, ArrayList<String>> expectedAttributeToReplace = new HashMap<String, ArrayList<String>>();
        expectedAttributeToReplace.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(IdmConstants.LDAP_NO_LOGIN_SHELL));
        expectedAttributeToReplace.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(IdmConstants.LDAP_NULL_POSIX_GROUP_NUMBER));
        expectedAttributeToReplace.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(IdmConstants.LDAP_NULL_HOME_DIRECTORY));

        when(communicator.querySingleAttribute(makeGroupDN(IdmConstants.COM_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER,
                null)).thenReturn(getArrayList(IdmConstants.COM_GROUP_ID));
        when(communicator.querySingleAttribute(makeGroupDN(AMOS_USER_GROUP_NAME), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                .thenReturn(getArrayList(AMOS_GID_NUMBER));
        when(communicator.querySingleAttribute(makeGroupDN(IdmConstants.COM_GROUP_NAME), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_MEMBER_UID,
                null)).thenReturn(getArrayList(userName));
        when(communicator.querySingleAttribute(makeGroupDN(AMOS_USER_GROUP_NAME), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UNIQUE_MEMBER, null))
                .thenReturn(getArrayList(userDN));
        attributeValuePairs.put(IdmConstants.LDAP_OBJECTCLASS, getArrayList(IdmConstants.LDAP_SHADOWACCOUNT));
        attributeValuePairs.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(IdmConstants.COM_GROUP_ID));
        when(communicator.queryMultipleAttributes(userDN, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(attributeValuePairs);
        doNothing().when(communicator).modifyEntryReplaceMultipleAttributes(userDN, expectedAttributeToReplace);
        doNothing().when(communicator).modifyEntryDelete(userDN, IdmConstants.LDAP_OBJECTCLASS, IdmConstants.LDAP_SHADOWACCOUNT);

        final String result = secManager.removePosixAttributes(PEOPLE_CONTAINER, userName, AMOS_USER_GROUP_NAME);

        assertNull(result);
        verify(communicator).modifyEntryReplaceMultipleAttributes(userDN.toString(), attributesToModify);
        verify(communicator, times(0)).modifyEntryDelete(userDN, IdmConstants.LDAP_OBJECTCLASS, IdmConstants.LDAP_SHADOWACCOUNT);
        verify(communicator).modifyEntryDelete(makeGroupDN(AMOS_USER_GROUP_NAME), IdmConstants.LDAP_UNIQUE_MEMBER, userDN);
        verify(communicator, times(0)).querySingleAttribute(makeGroupDN(IdmConstants.COM_GROUP_NAME), SearchControls.SUBTREE_SCOPE,
                IdmConstants.LDAP_GID_NUMBER, null);
        verify(communicator, times(0)).querySingleAttribute(makeGroupDN(AMOS_USER_GROUP_NAME), SearchControls.SUBTREE_SCOPE,
                IdmConstants.LDAP_GID_NUMBER, null);
    }

    private ArrayList<String> getArrayList(final String value) {
        final ArrayList<String> valueList = new ArrayList<String>();
        valueList.add(value);
        return valueList;
    }

    private String makeGroupDN() {

        return makeGroupDN(FIELD_TECH_USER_GROUP_NAME);
    }

    private String makeGroupDN(final String groupName) {
        final StringBuilder groupDN = new StringBuilder("cn=" + groupName);
        groupDN.append(",ou=Groups");
        groupDN.append(getDnPostfix());
        return groupDN.toString();
    }

    private String makeUserDN(final String name) {
        final StringBuffer userDN = new StringBuffer("uid=" + name);
        userDN.append(",ou=" + FIELD_TECH_USER_CONTAINER);
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
}
