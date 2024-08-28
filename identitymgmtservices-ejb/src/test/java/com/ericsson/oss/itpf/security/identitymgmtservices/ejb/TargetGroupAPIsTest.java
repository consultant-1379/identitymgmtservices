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

import static com.ericsson.oss.itpf.security.identitymgmtservices.IdmConstants.LDAP_ERROR_CONNECTION_FAILURE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.security.identitymgmtservices.*;

/**
 * 
 * This class is a test case to test the Target Group APIs defined in IdentityManagementService.java. Below are the APIs tested
 * 1.getAllTargetGroups(); 2.getDefaultTargetGroup(); 3.validateTargetGroups();
 * 
 */

public class TargetGroupAPIsTest {
    private Logger logger = LoggerFactory.getLogger(TargetGroupAPIsTest.class);
    private SecurityManagerBean secManager = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.setProperty(IdmConstants.CONFIGURATION_PROPERTY, "./src/test/resources/datastore.properties");
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        secManager = new SecurityManagerBean();
        secManager.logger = logger;
    }

    @After
    public void tearDown() {
        secManager.communicator = null;
    }

    /**
     * This test case is to test the getAllTargetGroups() API of SecurityManagerBean. It tests an ERROR scenario where in a DSCommunicatorException of
     * NO_SUCH_ENTRY is thrown back to the caller when there is no such entry
     */
    @Test
    public void testGetAllTargetGroupsNoEntryExists() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        //mock the exception
        try {
            logger.debug("TEST testGetAllTargetGroupsNoEntryExists");
            final String tgDN = getDN();
            //prepare the exception to be thrown
            final DSCommunicatorException dsException = new DSCommunicatorException("Target entry does not exist",
                    IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY);
            //throw the exception when the below method is called
            when(communicator.querySingleAttribute(tgDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_ORGANISATIONALUNIT, null)).thenThrow(
                    dsException);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from communicator.querySingleAttribute(): " + e1.getMessage());
            e1.printStackTrace();
        }
        //call to api to be tested
        try {
            secManager.getAllTargetGroups();
            fail("fail in testGetAllTargetGroupsNoEntryExists(), getAllTargetGroups should throw exception with ENTRY_NOT_FOUND");
        } catch (final IdentityManagementServiceException e) {
            //expected exception, assert it.
            assertEquals(IdentityManagementServiceException.Error.ENTRY_NOT_FOUND, e.getError());
        } catch (final Exception e) {
            fail("fail in testGetAllTargetGroupsNoEntryExists(), Error " + e.getMessage());
        }
    }

    /**
     * This test case is to test the getAllTargetGroups() API of SecurityManagerBean. It tests an ERROR scenario where in a DSCommunicatorException of
     * NO_SUCH_ATTRIBUTE is thrown back to the caller when there are no children under the target group "ou=TargetGroups"
     */
    @Test
    public void testGetAllTargetGroupsNoAttributeOrValue() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        //mock the exception
        try {
            logger.debug("TEST testGetAllTargetGroupsNoAttributeOrValue");
            final String tgDN = getDN();
            //prepare the exception to be thrown
            final List<String> returnAllTargetGroups = new ArrayList<String>();
            //throw the exception when the below method is called
            when(communicator.querySingleAttribute(tgDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_ORGANISATIONALUNIT, null)).thenReturn(
                    (ArrayList<String>) returnAllTargetGroups);
        } catch (final Exception e1) {
            fail("fail in testGetAllTargetGroupsNoAttributeOrValue(), shouldn't throw exception from communicator.querySingleAttribute(): "
                    + e1.getMessage());
            e1.printStackTrace();
        }
        //call to api to be tested
        try {
            final List<String> returnedAllTargetGroups = secManager.getAllTargetGroups();
            assertEquals(0, returnedAllTargetGroups.size());
        } catch (final IdentityManagementServiceException e) {
            //expected exception, assert it.
            fail("fail in testGetAllTargetGroupsNoAttributeOrValue(), getAllTargetGroups() shouldn't throw exception from secManager.getAllTargetGroups(): "
                    + e.getMessage());
        } catch (final Exception e) {
            fail("fail in testGetAllTargetGroupsNoAttributeOrValue(), Error " + e.getMessage());
        }
    }

    /**
     * This test case is to test the getAllTargetGroups() API of SecurityManagerBean. It tests the positive scenario where in the method returns the
     * expected list of target groups.
     */
    @Test
    public void testGetAllTargetGroupsSuccess() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        //mocking the list to be returned
        final List<String> allTargetGroups = prepareTargetGroupsMockList();
        try {
            logger.debug("TEST testGetAllTargetGroupsSuccess");
            final String tgDN = getDN();
            //return it, on the method call
            when(communicator.querySingleAttribute(tgDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_ORGANISATIONALUNIT, null)).thenReturn(
                    (ArrayList<String>) allTargetGroups);
        } catch (final Exception e1) {
            fail("fail in testGetAllTargetGroupsSuccess(), shouldn't throw exception from communicator.querySingleAttribute(): " + e1.getMessage());
            e1.printStackTrace();
        }
        //call to api to be tested
        try {
            final List<String> returnedTargetGroups = secManager.getAllTargetGroups();
            //assert the returned list, since we mocked the returned list which is not null and has size 3.
            assertNotNull(returnedTargetGroups);
            assertEquals(3, returnedTargetGroups.size());
            assertEquals(true, returnedTargetGroups.contains(allTargetGroups.get(0)));
            assertEquals(true, returnedTargetGroups.contains(allTargetGroups.get(1)));
            assertEquals(true, returnedTargetGroups.contains(allTargetGroups.get(2)));
        } catch (final IdentityManagementServiceException e) {
            fail("fail in testGetAllTargetGroupsSuccess(), shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        } catch (final Exception e) {
            fail("fail in testGetAllTargetGroupsSuccess(), Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This test case is to test the getDefaultTargetGroup() API of SecurityManagerBean. The API implementation only returns a String constant, so
     * there is no mocking of the response as such to test it. Once the implementation changes to dynamic behavior, this test case has to be changes
     * accordingly.
     */
    @Test
    public void testGetDefaultTargetGroupsSuccess() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        //TODO: no request mocking is added since a string constant is returned from the secured manager bean
        //call the API, assert the constant.
        try {
            final String defaultTargetGroup = secManager.getDefaultTargetGroup();
            assertNotNull(defaultTargetGroup);
            assertEquals(IdmConstants.LDAP_DEFAULTTARGETGROUP, defaultTargetGroup);
        } catch (final IdentityManagementServiceException e) {
            fail("fail in testGetDefaultTargetGroupsSuccess(), shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        } catch (final Exception e) {
            fail("fail in testGetDefaultTargetGroupsSuccess(), Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This test case is to test the validateTargetGroups() API of SecurityManagerBean. As mentioned in the API, it returns the list of not
     * known/valid target groups. In this test case we test the scenario where in the target groups to be validated are all valid, in which case the
     * API would return a empty list with size 0.
     */
    @Test
    public void testValidateTargetGroupsAllValidCase() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        //in this test case there are two List<String> to be prepared as a part of mocking
        //1. List<String> which come from the UI which are to be validated
        //2. List<String> which are returned from the getAllTargetGroups()
        //2 is used to validate 1
        //since this test case is to for scenario in which all are valid, both 1 and 2 are same
        final List<String> allTargetGroups = prepareTargetGroupsMockList();
        //mock the return list at getAllTargetGroups()
        try {
            logger.debug("TEST testValidateTargetGroupsAllValidCase");
            final String tgDN = getDN();
            when(communicator.querySingleAttribute(tgDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_ORGANISATIONALUNIT, null)).thenReturn(
                    (ArrayList<String>) allTargetGroups);
        } catch (final Exception e1) {
            fail("fail in testValidateTargetGroupsAllValidCase(), shouldn't throw exception from communicator.querySingleAttribute(): "
                    + e1.getMessage());
            e1.printStackTrace();
        }
        //call the api to be tested and pass the List<String> as an input, in this case it is the same @allTargetGroups
        try {
            final List<String> returnedInvalidTargetGroups = secManager.validateTargetGroups(allTargetGroups);
            assertNotNull(returnedInvalidTargetGroups);
            assertEquals(0, returnedInvalidTargetGroups.size());
        } catch (final IdentityManagementServiceException e) {
            fail("fail in testValidateTargetGroupsAllValidCase(), shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        } catch (final Exception e) {
            fail("fail in testValidateTargetGroupsAllValidCase(), Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This test case is to test the validateTargetGroups() API of SecurityManagerBean. As mentioned in the API, it returns the list of not
     * known/valid target groups. In this test case we test the scenario where in the target groups to be validated are not valid, in which case the
     * API would return a list of invalid target groups.
     */
    @Test
    public void testValidateTargetGroupsAllInValidCase() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        //in this test case there are two List<String> to be prepared as a part of mocking
        //1. List<String> which come from the UI which are to be validated
        //2. List<String> which are returned from the getAllTargetGroups()
        //2 is used to validate 1
        //since this test case is to for scenario in which all are invalid, both 1 and 2 are completely different
        final List<String> targetGroupsSentFromUI = new ArrayList<String>();
        targetGroupsSentFromUI.add("tGroup4");
        targetGroupsSentFromUI.add("tGroup5");
        targetGroupsSentFromUI.add("tGroup6");
        //mock the return list at getAllTargetGroups()
        try {
            logger.debug("TEST testValidateTargetGroupsAllInValidCase");
            //mocking the list to be returned
            final List<String> allTargetGroups = prepareTargetGroupsMockList();
            final String tgDN = getDN();
            when(communicator.querySingleAttribute(tgDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_ORGANISATIONALUNIT, null)).thenReturn(
                    (ArrayList<String>) allTargetGroups);
        } catch (final Exception e1) {
            fail("fail in testValidateTargetGroupsAllInValidCase(), shouldn't throw exception from communicator.querySingleAttribute(): "
                    + e1.getMessage());
            e1.printStackTrace();
        }
        //call the api to be tested and pass the List<String> as an input, in this case it is @targetGroupsSentFromUI
        //since none of the groups from allTargetGroups and targetGroupsSentFromUI match the returned list should be of size 3.
        try {
            final List<String> returnedInvalidTargetGroups = secManager.validateTargetGroups(targetGroupsSentFromUI);
            assertEquals(0, returnedInvalidTargetGroups.size(), 3);
            assertEquals(true, returnedInvalidTargetGroups.contains(targetGroupsSentFromUI.get(0)));
            assertEquals(true, returnedInvalidTargetGroups.contains(targetGroupsSentFromUI.get(1)));
            assertEquals(true, returnedInvalidTargetGroups.contains(targetGroupsSentFromUI.get(2)));
        } catch (final IdentityManagementServiceException e) {
            fail("fail in testValidateTargetGroupsAllInValidCase(), shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        } catch (final Exception e) {
            fail("fail in testValidateTargetGroupsAllInValidCase(), Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This test case is to test the validateTargetGroups() API of SecurityManagerBean. As mentioned in the API, it returns the list of not
     * known/valid target groups. In this test case we test the scenario where in the target groups to be validated are few not valid and few are
     * valid, in which case the API would return a list of invalid target groups.
     */
    @Test
    public void testValidateTargetGroupsFewValidCase() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        //in this test case there are two List<String> to be prepared as a part of mocking
        //1. List<String> which come from the UI which are to be validated
        //2. List<String> which are returned from the getAllTargetGroups()
        //2 is used to validate 1
        //since this test case is to for scenario in which few are invalid, there is a new target in 1 "cn=tGroup6".
        final List<String> targetGroupsSentFromUI = new ArrayList<String>();
        targetGroupsSentFromUI.add("tGroup1");
        targetGroupsSentFromUI.add("tGroup2");
        targetGroupsSentFromUI.add("tGroup6");
        //mocking the list to be returned
        try {
            logger.debug("TEST testValidateTargetGroupsFewValidCase");
            final List<String> allTargetGroups = prepareTargetGroupsMockList();
            final String tgDN = getDN();
            //mock the list to be returned on call of getAllTargetGroups()
            when(communicator.querySingleAttribute(tgDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_ORGANISATIONALUNIT, null)).thenReturn(
                    (ArrayList<String>) allTargetGroups);
        } catch (final Exception e1) {
            fail("fail in testValidateTargetGroupsFewValidCase(), shouldn't throw exception from communicator.querySingleAttribute(): "
                    + e1.getMessage());
            e1.printStackTrace();
        }
        //call the api to be tested and pass the List<String> as an input, in this case it is @targetGroupsSentFromUI
        //since 2 of the groups from allTargetGroups and targetGroupsSentFromUI match and 1 does not match the returned list should be of size 1.
        try {
            final List<String> returnedInvalidTargetGroups = secManager.validateTargetGroups(targetGroupsSentFromUI);
            assertNotNull(returnedInvalidTargetGroups);
            assertEquals(1, returnedInvalidTargetGroups.size());
            assertEquals("tGroup6", returnedInvalidTargetGroups.get(0));
        } catch (final IdentityManagementServiceException e) {
            fail("fail in testValidateTargetGroupsFewValidCase(), shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        } catch (final Exception e) {
            fail("fail in testValidateTargetGroupsFewValidCase(), Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This test case is to test the validateTargetGroups() API of SecurityManagerBean. As mentioned in the API. it returns the list of not
     * known/valid target groups. In this test case we test the scenario where in the target groups to be validated are empty, in which case the API
     * would throw an UNEXPECTED_ERROR exception.
     */
    @Test
    public void testValidateTargetGroupsEmptyListFromUI() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        //in this test case there are two List<String> to be prepared as a part of mocking
        //1. List<String> which come from the UI which are to be validated
        //2. List<String> which are returned from the getAllTargetGroups()
        //2 is used to validate 1
        //since this test case is to for scenario in which few are invalid, there is a new target in 1 "cn=tGroup6".
        final List<String> targetGroupsSentFromUI = new ArrayList<String>();
        //mocking the list to be returned
        try {
            logger.debug("TEST testValidateTargetGroupsEmptyListFromUI");
            final List<String> allTargetGroups = prepareTargetGroupsMockList();
            //get the target DN
            final String tgDN = getDN();
            //mock the list to be returned on call of getAllTargetGroups()
            when(communicator.querySingleAttribute(tgDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_ORGANISATIONALUNIT, null)).thenReturn(
                    (ArrayList<String>) allTargetGroups);
        } catch (final Exception e1) {
            fail("fail in testValidateTargetGroupsEmptyListFromUI(), shouldn't throw exception from communicator.querySingleAttribute(): "
                    + e1.getMessage());
            e1.printStackTrace();
        }
        //call the api to be tested and pass the List<String> as an input, in this case it is @targetGroupsSentFromUI and it is empty
        try {
            secManager.validateTargetGroups(targetGroupsSentFromUI);
            fail("fail in testValidateTargetGroupsEmptyListFromUI(), validateTargetGroups should throw exception with UNEXPECTED_ERROR");
        } catch (final IdentityManagementServiceException e) {
            //expected exception, assert it
            assertEquals(IdentityManagementServiceException.Error.UNEXPECTED_ERROR, e.getError());
        } catch (final Exception e) {
            fail("fail in testValidateTargetGroupsEmptyListFromUI(), Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This test case is to test the validateTargetGroups() API of SecurityManagerBean. As mentioned in the API. it returns the list of not
     * known/valid target groups. In this test case we test the scenario where in the target groups to be validated is null, in which case the API
     * would throw an UNEXPECTED_ERROR exception.
     */
    @Test
    public void testValidateTargetGroupsNullFromUI() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        //in this test case there are two List<String> to be prepared as a part of mocking
        //1. List<String> which come from the UI which are to be validated
        //2. List<String> which are returned from the getAllTargetGroups()
        //2 is used to validate 1
        //since this test case is to for scenario in which few are invalid, there is a new target in 1 "cn=tGroup6".
        final List<String> targetGroupsSentFromUI = null;
        //mocking the list to be returned
        try {
            logger.debug("TEST testValidateTargetGroupsNullFromUI");
            final List<String> allTargetGroups = prepareTargetGroupsMockList();
            //get the target DN
            final String tgDN = getDN();
            //mock the list to be returned on call of getAllTargetGroups()
            when(communicator.querySingleAttribute(tgDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_ORGANISATIONALUNIT, null)).thenReturn(
                    (ArrayList<String>) allTargetGroups);
        } catch (final Exception e1) {
            fail("fail in testValidateTargetGroupsNullFromUI(), shouldn't throw exception from communicator.querySingleAttribute(): "
                    + e1.getMessage());
            e1.printStackTrace();
        }
        //call the api to be tested and pass the List<String> as an input, in this case it is @targetGroupsSentFromUI and it is null
        try {
            secManager.validateTargetGroups(targetGroupsSentFromUI);
            fail("fail in testValidateTargetGroupsNullFromUI(), validateTargetGroups should throw exception with UNEXPECTED_ERROR");
        } catch (final IdentityManagementServiceException e) {
            //expected exception, assert it
            assertEquals(IdentityManagementServiceException.Error.UNEXPECTED_ERROR, e.getError());
        } catch (final Exception e) {
            fail("fail in testValidateTargetGroupsNullFromUI(), Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test validateTargetGroups method, connection failure
     */
    @Test
    public void testValidateTargetGroupsConnectionFailure() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser3";
        final String tgDN = "ou=TargetGroups" + "," + ConfigurationBean.get(IdmConstants.COM_INF_LDAP_ROOT_SUFFIX_PROPERTY);

        final int errorCode = LDAP_ERROR_CONNECTION_FAILURE;

        final List<String> groupList = new ArrayList<>();
        groupList.add("group1");
        groupList.add("group2");
        groupList.add("group3");

        try {
            final DSCommunicatorException dsException = new DSCommunicatorException("Query " + userName + " is failing due to ", errorCode);
            when(communicator.querySingleAttribute(tgDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_ORGANISATIONALUNIT,
                    null)).thenThrow(dsException);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.validateTargetGroups(groupList);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.DATA_STORE_CONNECTION_FAILURE, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * prepares the mock list of target groups used in different test cases
     * 
     * @return
     */
    private List<String> prepareTargetGroupsMockList() {
        final List<String> allTargetGroups = new ArrayList<String>();
        allTargetGroups.add("tGroup1");
        allTargetGroups.add("tGroup2");
        allTargetGroups.add("tGroup3");
        return allTargetGroups;
    }

    /**
     * Prepares the DN and returns it which is used in all the test cases
     * 
     * @return
     */
    private String getDN() {
        return "ou=TargetGroups," + ConfigurationBean.get(IdmConstants.COM_INF_LDAP_ROOT_SUFFIX_PROPERTY);
    }

}
