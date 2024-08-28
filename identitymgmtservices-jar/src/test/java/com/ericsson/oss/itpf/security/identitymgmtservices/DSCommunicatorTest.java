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

package com.ericsson.oss.itpf.security.identitymgmtservices;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchControls;

import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.Entry;
import org.forgerock.opendj.ldap.ErrorResultException;
import org.forgerock.opendj.ldap.Modification;
import org.forgerock.opendj.ldap.ModificationType;
import org.forgerock.opendj.ldap.ResultCode;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.requests.ModifyRequest;
import org.forgerock.opendj.ldap.requests.Requests;
import org.forgerock.opendj.ldap.responses.Responses;
import org.forgerock.opendj.ldap.responses.Result;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.forgerock.opendj.ldif.ConnectionEntryReader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.slf4j.Logger;

import com.ericsson.oss.itpf.security.identitymgmtservices.datastore.DataStoreManager;
import com.ericsson.oss.services.security.keymgmt.command.RetrieveENMPasswordImpl;

/**
 *
 * @author xrafkar
 *
 */
// NOTE: the values below will need to be aligned with the values in clientConfig entry in LDAP
// M2M user container: ou=M2MUsers
// group container for M2MUser: cn=mm-smrsusers

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ DSCommunicator.class, DataStoreManager.class, Connection.class })
public class DSCommunicatorTest {

    @Mock
    private DSCommunicator dSCommunicator;

    @Mock
    private DSCommunicator dSCommunicatorWithNullConnectionField;

    @Mock
    private Connection connection;

    @Mock
    private Logger logger;

    @Mock
    private Result result;

    @Mock
    RetrieveENMPasswordImpl retrieveENMPasswordImplMock;

    @Mock
    ConnectionEntryReader connReader;

    private final String fullDNPath = "uid=userjohn,ou=People,dc=apache,dc=com";
    private final String attributeName = "emailaddress";
    private final String attributeValue = "userjohn@ericsson.com";
    private final String newm2mUserDN = "uid=m2muser11,ou=M2MUsers,dc=vts,dc=com";
    private final ArrayList<String> retAttList = new ArrayList<String>();
    private final HashMap<String, ArrayList<String>> attributesToModify = new HashMap<String, ArrayList<String>>();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.setProperty(IdmConstants.CONFIGURATION_PROPERTY, "./src/test/resources/datastore.properties");
    }

    @Before
    public void setup() throws Exception {

        final Field privateFieldConnection = DSCommunicator.class.getDeclaredField("connection");
        privateFieldConnection.setAccessible(true);
        privateFieldConnection.set(dSCommunicator, connection);

        final Field privateFieldLogger = DSCommunicator.class.getDeclaredField("logger");
        privateFieldLogger.setAccessible(true);
        privateFieldLogger.set(dSCommunicator, logger);
        privateFieldLogger.set(dSCommunicatorWithNullConnectionField, logger);

        final Field privateFieldConnectionException = DSCommunicator.class.getDeclaredField("connection");
        privateFieldConnectionException.setAccessible(true);
        privateFieldConnectionException.set(dSCommunicatorWithNullConnectionField, null);
        attributesToModify.put("attributes", retAttList);

        retAttList.add("email");
        retAttList.add("password");
        retAttList.add("username");
        retAttList.add("phone");

        doCallRealMethod().when(dSCommunicator).modifyEntryReplace(anyString(), anyString(), anyString());
        doCallRealMethod().when(dSCommunicator).deleteEntry(anyString());
        doReturn(false).when(connection).isClosed();
        doReturn(true).when(connection).isValid();
        doReturn(true).when(result).isSuccess();
        final Field retrieveENMPassword = DataStoreManager.class.getDeclaredField("retrieveENMPassword");
        retrieveENMPassword.setAccessible(true);
        retrieveENMPassword.set(DataStoreManager.class, retrieveENMPasswordImplMock);
        doThrow(new IllegalStateException("Error occurred")).when(retrieveENMPasswordImplMock).retrieveLdapAdminPassword();
    }

    /**
     * This test checks if querySingleAttribute method is handled properly when attribute is not found in ldap when there is unknown search scope
     *
     * @throws DSCommunicatorException
     */
    @Test
    public void querySingleAttributeVerifyAttributeNotFoundOtherScopeTest() throws DSCommunicatorException {
        doCallRealMethod().when(dSCommunicator).querySingleAttribute(anyString(), anyInt(), anyString(), anyString());
        dSCommunicator.modifyEntryReplace(fullDNPath, attributeName, attributeValue);
        final List<String> retMap = dSCommunicator.querySingleAttribute(fullDNPath, SearchScope.SUBORDINATES.intValue(), attributeName,
                "(objectclass=*)");
        assertTrue(retMap.isEmpty());
    }

    /**
     * This test checks if querySingleAttribute method is handled properly when attribute is not found in ldap when search scope equals to
     * SUBTREE_SCOPE
     *
     * @throws DSCommunicatorException
     */
    @Test
    public void querySingleAttributeVerifyAttributeNotFoundSubtreeScopeTest() throws DSCommunicatorException {
        doCallRealMethod().when(dSCommunicator).querySingleAttribute(anyString(), eq(SearchControls.SUBTREE_SCOPE), anyString(), anyString());
        dSCommunicator.modifyEntryReplace(fullDNPath, attributeName, attributeValue);
        final List<String> retMap = dSCommunicator.querySingleAttribute(fullDNPath, SearchControls.SUBTREE_SCOPE, attributeName,
                "(objectclass=*)");
        assertTrue(retMap.isEmpty());
    }

    /**
     * This test checks if querySingleAttribute method is handled properly when attribute is not found in ldap when search scope equals to
     * ONELEVEL_SCOPE
     *
     * @throws DSCommunicatorException
     */
    @Test
    public void querySingleAttributeVerifyAttributeNotFoundOneLevelScopeTest() throws DSCommunicatorException {
        doCallRealMethod().when(dSCommunicator).querySingleAttribute(anyString(), eq(SearchControls.ONELEVEL_SCOPE), anyString(), anyString());
        dSCommunicator.modifyEntryReplace(fullDNPath, attributeName, attributeValue);
        final List<String> retMap = dSCommunicator.querySingleAttribute(fullDNPath, SearchControls.ONELEVEL_SCOPE, attributeName,
                "(objectclass=*)");
        assertTrue(retMap.isEmpty());
    }

    /**
     * This test checks if querySingleAttribute method is handled properly when attribute is not found in ldap when search scope equals to
     * OBJECT_SCOPE
     *
     * @throws DSCommunicatorException
     */
    @Test
    public void querySingleAttributeVerifyAttributeNotFoundObjectScopeTest() throws DSCommunicatorException {
        doCallRealMethod().when(dSCommunicator).querySingleAttribute(anyString(), eq(SearchControls.OBJECT_SCOPE), anyString(), anyString());
        dSCommunicator.modifyEntryReplace(fullDNPath, attributeName, attributeValue);
        final List<String> retMap = dSCommunicator.querySingleAttribute(fullDNPath, SearchControls.OBJECT_SCOPE, attributeName,
                "(objectclass=*)");
        assertTrue(retMap.isEmpty());
    }

    /** This test checks if DSCommunicatorException is properly handled for searchEntry when there is problem with connection to ldap */
    @Test(expected = DSCommunicatorException.class)
    public void searchEntryVerifyDSCommunicatorExceptionTest() throws DSCommunicatorException {
        doCallRealMethod().when(dSCommunicatorWithNullConnectionField).searchEntry(anyString());
        dSCommunicatorWithNullConnectionField.searchEntry(fullDNPath);
    }

    /**
     * This test checks if EntryNotFoundException is properly handled for searchEntry method with Result Code NO_SUCH_OBJECT
     *
     * @throws ErrorResultException
     */
    @Test(expected = DSCommunicatorException.class)
    public void searchEntryVerifyConnectionExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.CLIENT_SIDE_SERVER_DOWN)).when(connection).readEntry(anyString());
        doCallRealMethod().when(dSCommunicator).searchEntry(anyString());
        dSCommunicator.searchEntry(fullDNPath);
    }

    /**
     * This test checks if EntryNotFoundException is properly handled for searchEntry method with Result Code NO_SUCH_OBJECT
     *
     * @throws ErrorResultException
     */
    @Test
    public void searchEntryVerifyEntryNotFoundExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.NO_SUCH_OBJECT)).when(connection).readEntry(anyString());
        doCallRealMethod().when(dSCommunicator).searchEntry(anyString());
        dSCommunicator.searchEntry(fullDNPath);
    }


    /**
     * This test checks when entry is found
     *
     */
    @Test
    public void searchEntryVerifyEntryFound() throws DSCommunicatorException, ErrorResultException {
        SearchResultEntry entry = Responses.newSearchResultEntry(fullDNPath);
        when(connection.readEntry(anyString())).thenReturn(entry);
        doCallRealMethod().when(dSCommunicator).searchEntry(anyString());
        assertTrue(dSCommunicator.searchEntry(fullDNPath));
    }

    /**
     * This test checks if searchEntry returns fail when there is problem with reading entry. Type of method: boolean. Throws exception if operation
     * fails
     *
     * @throws DSCommunicatorException
     */
    @Test
    public void searchEntryVerifyIsFalseTest() throws DSCommunicatorException {
        doCallRealMethod().when(dSCommunicator).searchEntry(anyString());
        assertFalse(dSCommunicator.searchEntry(fullDNPath));
    }

    /** This test checks if DSCommunicatorException is properly handled for addEntry when there is problem with connection to ldap */
    @SuppressWarnings("unchecked")
    @Test(expected = DSCommunicatorException.class)
    public void addEntryVerifyDSCommunicatorExceptionTest() throws DSCommunicatorException {
        doCallRealMethod().when(dSCommunicatorWithNullConnectionField).addEntry(anyString(), any(HashMap.class));
        dSCommunicatorWithNullConnectionField.addEntry(fullDNPath, attributesToModify);
    }

    /** This test checks if DSCommunicatorException is properly handled for addEntry when there is problem with connection to ldap */
    @SuppressWarnings("unchecked")
    @Test(expected = DSCommunicatorException.class)
    public void addEntryVerifyDSCommunicatorExceptionTest1() throws DSCommunicatorException {
        doReturn("password").when(retrieveENMPasswordImplMock).retrieveLdapAdminPassword();
        doCallRealMethod().when(dSCommunicatorWithNullConnectionField).addEntry(anyString(), any(HashMap.class));
        dSCommunicatorWithNullConnectionField.addEntry(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if ErrorResultException is properly handled for addEntry method with unknown Result Code
     *
     * @throws ErrorResultException
     */
    @SuppressWarnings("unchecked")
    @Test(expected = DSCommunicatorException.class)
    public void addEntryVerifyUnknownExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.OTHER)).when(connection).add(any(Entry.class));
        doCallRealMethod().when(dSCommunicator).addEntry(anyString(), any(HashMap.class));
        dSCommunicator.addEntry(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if ConnectionException is properly handled for addEntry method with Result Code CLIENT_SIDE_SERVER_DOWN
     *
     * @throws ErrorResultException
     */
    @SuppressWarnings("unchecked")
    @Test(expected = DSCommunicatorException.class)
    public void addEntryVerifyConnectionExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.CLIENT_SIDE_SERVER_DOWN)).when(connection).add(any(Entry.class));
        doCallRealMethod().when(dSCommunicator).addEntry(anyString(), any(HashMap.class));
        dSCommunicator.addEntry(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if ConstraintViolationException is properly handled for addEntry method with unknown Result Code
     *
     * @throws ErrorResultException
     */
    @SuppressWarnings("unchecked")
    @Test(expected = DSCommunicatorException.class)
    public void addEntryVerifyConstrainViolationExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.CONSTRAINT_VIOLATION)).when(connection).add(any(Entry.class));
        doCallRealMethod().when(dSCommunicator).addEntry(anyString(), any(HashMap.class));
        dSCommunicator.addEntry(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if ConstraintViolationException is properly handled for addEntry method with Result Code ENTRY_ALREADY_EXISTS
     *
     * @throws ErrorResultException
     */
    @SuppressWarnings("unchecked")
    @Test(expected = DSCommunicatorException.class)
    public void addEntryVerifyEntryAlreadyExistsExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.ENTRY_ALREADY_EXISTS)).when(connection).add(any(Entry.class));
        doCallRealMethod().when(dSCommunicator).addEntry(anyString(), any(HashMap.class));
        dSCommunicator.addEntry(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if ConstraintViolationException is properly handled for addEntry method with Result Code ATTRIBUTE_OR_VALUE_EXISTS
     *
     * @throws ErrorResultException
     */
    @SuppressWarnings("unchecked")
    @Test(expected = DSCommunicatorException.class)
    public void addEntryVerifyAttributeOrValueExistsExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.ATTRIBUTE_OR_VALUE_EXISTS)).when(connection).add(any(Entry.class));
        doCallRealMethod().when(dSCommunicator).addEntry(anyString(), any(HashMap.class));
        dSCommunicator.addEntry(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if addEntry is properly handled. Type of method: void. Throws exception if operation fails
     *
     * @throws ErrorResultException
     * @throws DSCommunicatorException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void addEntryVerifyIsSuccessTest() throws ErrorResultException, DSCommunicatorException {
        doReturn(result).when(connection).add(any(Entry.class));
        doCallRealMethod().when(dSCommunicator).addEntry(anyString(), any(HashMap.class));
        dSCommunicator.addEntry(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if ErrorResultException is properly handled for modifyEntryRemoveMultipleAttributes method with unknown Result Code
     *
     * @throws ErrorResultException
     */
    @SuppressWarnings("unchecked")
    @Test(expected = DSCommunicatorException.class)
    public void modifyEntryRemoveMulitpleAttributesVerifyUnknownExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.OTHER)).when(connection).modify(any(ModifyRequest.class));
        doCallRealMethod().when(dSCommunicator).modifyEntryRemoveMultipleAttributes(anyString(), any(HashMap.class));
        dSCommunicator.modifyEntryRemoveMultipleAttributes(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if ConnectionException is properly handled for modifyEntryRemoveMultipleAttributes method with Result Code
     * CLIENT_SIDE_SERVER_DOWN
     *
     * @throws ErrorResultException
     */
    @SuppressWarnings("unchecked")
    @Test(expected = DSCommunicatorException.class)
    public void modifyEntryRemoveMultipleAttributesVerifyConnectionExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.CLIENT_SIDE_SERVER_DOWN)).when(connection).modify(any(ModifyRequest.class));
        doCallRealMethod().when(dSCommunicator).modifyEntryRemoveMultipleAttributes(anyString(), any(HashMap.class));
        dSCommunicator.modifyEntryRemoveMultipleAttributes(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if EntryNotFoundException is properly handled for modifyEntryRemoveMultipleAttributes method with Result Code NO_SUCH_OBJECT
     *
     * @throws ErrorResultException
     */
    @SuppressWarnings("unchecked")
    @Test(expected = DSCommunicatorException.class)
    public void modifyEntryRemoveMultipleAttributesVerifyEntryNotFoundExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.NO_SUCH_OBJECT)).when(connection).modify(any(ModifyRequest.class));
        doCallRealMethod().when(dSCommunicator).modifyEntryRemoveMultipleAttributes(anyString(), any(HashMap.class));
        dSCommunicator.modifyEntryRemoveMultipleAttributes(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if ConstraintViolationException is properly handled for modifyEntryRemoveMultipleAttributes method with unknown Result Code
     *
     * @throws ErrorResultException
     */
    @SuppressWarnings("unchecked")
    @Test(expected = DSCommunicatorException.class)
    public void modifyEntryRemoveMultipleAttributesVerifyConstraintViolationExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.CONSTRAINT_VIOLATION)).when(connection).modify(any(ModifyRequest.class));
        doCallRealMethod().when(dSCommunicator).modifyEntryRemoveMultipleAttributes(anyString(), any(HashMap.class));
        dSCommunicator.modifyEntryRemoveMultipleAttributes(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if ConstraintViolationException is properly handled for modifyEntryRemoveMultipleAttributes method with Result Code
     * ENTRY_ALREADY_EXISTS
     *
     * @throws ErrorResultException
     */
    @SuppressWarnings("unchecked")
    @Test(expected = DSCommunicatorException.class)
    public void modifyEntryRemoveMultipleAttributesVerifyEntryAlreadyExistsExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.ENTRY_ALREADY_EXISTS)).when(connection).modify(any(ModifyRequest.class));
        doCallRealMethod().when(dSCommunicator).modifyEntryRemoveMultipleAttributes(anyString(), any(HashMap.class));
        dSCommunicator.modifyEntryRemoveMultipleAttributes(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if ConstraintViolationException is properly handled for modifyEntryRemoveMultipleAttributes method with Result Code
     * ATTRIBUTE_OR_VALUE_EXISTS
     *
     * @throws ErrorResultException
     */
    @SuppressWarnings("unchecked")
    @Test(expected = DSCommunicatorException.class)
    public void modifyEntryRemoveMultipleAttributesVerifyAttributeOrValueExistsExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.ATTRIBUTE_OR_VALUE_EXISTS)).when(connection).modify(any(ModifyRequest.class));
        doCallRealMethod().when(dSCommunicator).modifyEntryRemoveMultipleAttributes(anyString(), any(HashMap.class));
        dSCommunicator.modifyEntryRemoveMultipleAttributes(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if modifyEntryAddMultipleAttributes is handled properly by adding attributes successfully. Void type method. Returns exception
     * when operation fails
     *
     * @throws ErrorResultException
     * @throws DSCommunicatorException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void modifyEntryAddMultipleAttributesVerifyIsSuccessTest() throws ErrorResultException, DSCommunicatorException {
        doReturn(result).when(connection).modify(any(ModifyRequest.class));
        doCallRealMethod().when(dSCommunicator).modifyEntryAddMultipleAttributes(anyString(), any(HashMap.class));
        dSCommunicator.modifyEntryAddMultipleAttributes(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if modifyEntryRemoveMultipleAttributes is handled properly by removing attributes successfully. Void type method. Returns
     * exception when operation fails
     *
     * @throws ErrorResultException
     * @throws DSCommunicatorException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void modifyEntryRemoveMultipleAttributesVerifyIsSuccessTest() throws ErrorResultException, DSCommunicatorException {
        doReturn(result).when(connection).modify(any(ModifyRequest.class));
        doCallRealMethod().when(dSCommunicator).modifyEntryRemoveMultipleAttributes(anyString(), any(HashMap.class));
        dSCommunicator.modifyEntryRemoveMultipleAttributes(fullDNPath, attributesToModify);
    }

    /**
     * This test checks if queryMultipleAttribute method is handled properly when attribute is not found in ldap when search scope equals to unknown
     * unknown scope
     *
     * @throws DSCommunicatorException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void queryMultipleAttributeVerifyAttributeNotFoundOtherScopeTest() throws DSCommunicatorException {
        doCallRealMethod().when(dSCommunicator).queryMultipleAttributes(anyString(), anyInt(), any(ArrayList.class));
        dSCommunicator.modifyEntryReplace(fullDNPath, attributeName, attributeValue);
        final Map<String, ArrayList<String>> retMap = dSCommunicator.queryMultipleAttributes(fullDNPath, SearchScope.SUBORDINATES.intValue(),
                retAttList);
        assertTrue(retMap.isEmpty());
    }

    /**
     * This test checks if queryMultipleAttribute method is handled properly when attribute is not found in ldap when search scope equals to
     * SUBTREE_SCOPE
     *
     * @throws DSCommunicatorException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void queryMultipleAttributeVerifyAttributeNotFoundSubtreeScopeTest() throws DSCommunicatorException {
        doCallRealMethod().when(dSCommunicator).queryMultipleAttributes(anyString(), eq(SearchControls.SUBTREE_SCOPE), any(ArrayList.class));
        dSCommunicator.modifyEntryReplace(fullDNPath, attributeName, attributeValue);
        final Map<String, ArrayList<String>> retMap = dSCommunicator.queryMultipleAttributes(fullDNPath, SearchControls.SUBTREE_SCOPE,
                retAttList);
        assertTrue(retMap.isEmpty());
    }

    /**
     * This test checks if queryMultipleAttribute method is handled properly when attribute is not found in ldap when search scope equals to
     * ONELEVEL_SCOPE
     *
     * @throws DSCommunicatorException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void queryMultipleAttributeVerifyAttributeNotFoundOneLevelScopeTest() throws DSCommunicatorException {
        doCallRealMethod().when(dSCommunicator).queryMultipleAttributes(anyString(), eq(SearchControls.ONELEVEL_SCOPE), any(ArrayList.class));
        dSCommunicator.modifyEntryReplace(fullDNPath, attributeName, attributeValue);
        final Map<String, ArrayList<String>> retMap = dSCommunicator.queryMultipleAttributes(fullDNPath, SearchControls.ONELEVEL_SCOPE,
                retAttList);
        assertTrue(retMap.isEmpty());
    }

    /**
     * This test checks if queryMultipleAttribute method is handled properly when attribute is not found in ldap when search scope equals to
     * OBJECT_SCOPE
     *
     * @throws DSCommunicatorException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void queryMultipleAttributeVerifyAttributeNotFoundObjectScopeTest() throws DSCommunicatorException {
        doCallRealMethod().when(dSCommunicator).queryMultipleAttributes(anyString(), eq(SearchControls.OBJECT_SCOPE), any(ArrayList.class));
        dSCommunicator.modifyEntryReplace(fullDNPath, attributeName, attributeValue);
        final Map<String, ArrayList<String>> retMap = dSCommunicator.queryMultipleAttributes(fullDNPath, SearchControls.OBJECT_SCOPE, retAttList);
        assertTrue(retMap.isEmpty());
    }

    /** This test checks if DSCommunicatorException is properly handled for modifyEntryReplace when there is problem with connection to ldap */
    @Test(expected = DSCommunicatorException.class)
    public void modifyEntryReplaceVerifyDSCommunicatorExceptionTest() throws DSCommunicatorException {
        doCallRealMethod().when(dSCommunicatorWithNullConnectionField).modifyEntryReplace(anyString(), anyString(), anyString());
        dSCommunicatorWithNullConnectionField.modifyEntryReplace(fullDNPath, attributeName, attributeValue);
    }

    /**
     * This test checks if Unexpected Exception is properly handled for modifyEntryReplace method with unknown Result Code
     *
     * @throws ErrorResultException
     */
    @Test(expected = DSCommunicatorException.class)
    public void modifyEntryReplaceVerifyUnexpectedExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.OTHER)).when(connection).modify(any(ModifyRequest.class));
        dSCommunicator.modifyEntryReplace(fullDNPath, attributeName, attributeValue);
    }

    /**
     * This test checks if ConnectionException is properly handled for modifyEntryReplace method with Result Code CLIENT_SIDE_SERVER_DOWN
     *
     * @throws ErrorResultException
     */
    @Test(expected = DSCommunicatorException.class)
    public void modifyEntryReplaceVerifyConnectionException() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.CLIENT_SIDE_SERVER_DOWN)).when(connection).modify(any(ModifyRequest.class));
        dSCommunicator.modifyEntryReplace(fullDNPath, attributeName, attributeValue);
    }

    /**
     * This test checks if EntryNotFoundException is properly handled for modifyEntryReplace method with Result Code NO_SUCH_OBJECT
     *
     * @throws ErrorResultException
     */
    @Test(expected = DSCommunicatorException.class)
    public void modifyEntryReplaceVerifyEntryNotFoundExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.NO_SUCH_OBJECT)).when(connection).modify(any(ModifyRequest.class));
        dSCommunicator.modifyEntryReplace(fullDNPath, attributeName, attributeValue);
    }

    /**
     * This test checks if modifyEntryReplace is properly handled. Type of method: void. Throws exception if operation fails
     *
     * @throws ErrorResultException
     * @throws DSCommunicatorException
     */
    @Test
    public void modifyEntryReplaceVerifyIsSuccessTest() throws ErrorResultException, DSCommunicatorException {
        doReturn(result).when(connection).modify(any(ModifyRequest.class));
        dSCommunicator.modifyEntryReplace(fullDNPath, attributeName, attributeValue);
    }

    /**
     * This test checks if modifyEntryAdd is properly handled. Type of method: void. Throws exception if operation fails
     *
     * @throws ErrorResultException
     * @throws DSCommunicatorException
     */
    @Test
    public void testModifyEntryAddVerifyIsSuccess() throws ErrorResultException, DSCommunicatorException {
        doReturn(result).when(connection).modify(any(ModifyRequest.class));
        doCallRealMethod().when(dSCommunicator).modifyEntryAdd(anyString(), anyString(), anyString());
        dSCommunicator.modifyEntryAdd(fullDNPath, attributeName, attributeValue);
    }

    /**
     * This test checks if modifyDeleteEntry is properly handled. Type of method: void. Throws exception if operation fails
     *
     * @throws ErrorResultException
     * @throws DSCommunicatorException
     */
    @Test
    public void modifyEntryDeleteVerifyIsSuccessTest() throws ErrorResultException, DSCommunicatorException {
        doReturn(result).when(connection).modify(any(ModifyRequest.class));
        doCallRealMethod().when(dSCommunicator).modifyEntryDelete(anyString(), anyString(), anyString());
        dSCommunicator.modifyEntryDelete(fullDNPath, attributeName, attributeValue);
    }

    /**
     * This test checks if deleteEntry is properly handled. Type of method: void. Throws exception if operation fails
     *
     * @throws ErrorResultException
     */
    @Test
    public void deleteEntryVerifyIsSuccessTest() throws DSCommunicatorException, ErrorResultException {
        doReturn(result).when(connection).delete(newm2mUserDN);
        dSCommunicator.deleteEntry(newm2mUserDN);
    }

    /** This test checks if DSCommunicatorException is properly handled for deleteEntry when there is problem with connection to ldap */
    @Test(expected = DSCommunicatorException.class)
    public void deleteEntryVerifyDSCommunicatorExceptionTest() throws DSCommunicatorException {
        doCallRealMethod().when(dSCommunicatorWithNullConnectionField).deleteEntry(anyString());
        dSCommunicatorWithNullConnectionField.deleteEntry(newm2mUserDN);
    }

    /**
     * This test checks if EntryNotFoundException is properly handled for deleteEntry method with Result Code NO_SUCH_OBJECT
     *
     * @throws ErrorResultException
     */
    @Test(expected = DSCommunicatorException.class)
    public void deleteEntryVerifyEntryNotFoundExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.NO_SUCH_OBJECT)).when(connection).delete(anyString());
        dSCommunicator.deleteEntry(newm2mUserDN);
    }

    /**
     * This test checks if ConnectionException is properly handled for deleteEntry method with Result Code CLIENT_SIDE_SERVER_DOWN
     *
     * @throws ErrorResultException
     */
    @Test(expected = DSCommunicatorException.class)
    public void deleteEntryVerifyConnectionExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.CLIENT_SIDE_SERVER_DOWN)).when(connection).delete(anyString());
        dSCommunicator.deleteEntry(newm2mUserDN);
    }

    /**
     * This test checks if UnexpectedException is properly handled for deleteEntry method with Result Code OTHER
     *
     * @throws ErrorResultException
     */
    @Test(expected = DSCommunicatorException.class)
    public void deleteEntryVerifyUnexpectedExceptionTest() throws DSCommunicatorException, ErrorResultException {
        doThrow(ErrorResultException.newErrorResult(ResultCode.OTHER)).when(connection).delete(anyString());
        dSCommunicator.deleteEntry(newm2mUserDN);
    }

    /**
     * This test checks if exception is thrown when incorrect data is handled
     *
     * @throws DSCommunicatorException
     */
    @Test
    public void getPasswordFormatVerifyExceptionTest() throws DSCommunicatorException {
        final ArrayList<String> testoutput = new ArrayList<String>();
        testoutput.add("StringException");
        doReturn(null).when(dSCommunicator).querySingleAttribute(IdmConstants.LDAP_CONFIG_CN, SearchControls.SUBTREE_SCOPE,
                IdmConstants.LDAP_CFG_CRED_FORMAT, IdmConstants.LDAP_CRED_GENERATOR_FILTER);
        when(dSCommunicator.getPasswordFormat()).thenCallRealMethod();
        assertNull(dSCommunicator.getPasswordFormat());
    }

    /**
     * This test retrieves Password Format
     *
     * @throws DSCommunicatorException
     */
    @Test
    public void getPasswordFormatVerifyIsSuccessTest() throws DSCommunicatorException {
        final ArrayList<String> testoutput = new ArrayList<String>();
        testoutput.add("letters:8");
        doReturn(testoutput).when(dSCommunicator).querySingleAttribute(IdmConstants.LDAP_CONFIG_CN, SearchControls.SUBTREE_SCOPE,
                IdmConstants.LDAP_CFG_CRED_FORMAT, IdmConstants.LDAP_CRED_GENERATOR_FILTER);
        when(dSCommunicator.getPasswordFormat()).thenCallRealMethod();
        assertEquals("letters:8",dSCommunicator.getPasswordFormat());
    }

    /**
     * This test checks if exception is thrown when incorrect data is handled
     *
     * @throws DSCommunicatorException
     */
    @Test
    public void getPasswordCharacterSetVerifyExceptionTest() throws DSCommunicatorException {
        final ArrayList<String> testoutput = new ArrayList<String>();
        testoutput.add("ExceptionString");
        doReturn(testoutput).when(dSCommunicator).querySingleAttribute(IdmConstants.LDAP_CONFIG_CN, SearchControls.SUBTREE_SCOPE,
                IdmConstants.LDAP_CFG_CRED_CHARACTER_SET, IdmConstants.LDAP_CRED_GENERATOR_FILTER);
        when(dSCommunicator.getPasswordCharacterSet()).thenCallRealMethod();
        assertTrue(dSCommunicator.getPasswordCharacterSet().isEmpty());
    }

    /**
     * This test checks if Password Character Set is correctly retrieved
     *
     * @throws DSCommunicatorException
     */
    @Test
    public void getPasswordCharacterSetVerifyIsSuccessTest() throws DSCommunicatorException {
        final ArrayList<String> testoutput = new ArrayList<String>();
        testoutput.add("alphabet:abcdefghijklmnopqrstuwxyz");
        doReturn(testoutput).when(dSCommunicator).querySingleAttribute(IdmConstants.LDAP_CONFIG_CN, SearchControls.SUBTREE_SCOPE,
                IdmConstants.LDAP_CFG_CRED_CHARACTER_SET, IdmConstants.LDAP_CRED_GENERATOR_FILTER);
        when(dSCommunicator.getPasswordCharacterSet()).thenCallRealMethod();
        final Map<String, NamedCharacterSet> actual = dSCommunicator.getPasswordCharacterSet();
        assertArrayEquals("abcdefghijklmnopqrstuwxyz".toCharArray(), (actual.get("alphabet")).getCharacters());
    }

    /**
     * This test checks if exception will be thrown when incorrect data is handled
     */
    @Test
    public void getRandomPasswordVerifyExceptionTest() {
        final Map<String, NamedCharacterSet> outputGetPasswordCharacterSet = new HashMap<String, NamedCharacterSet>();
        final NamedCharacterSet namedCharacterSet = new NamedCharacterSet("alphabet", new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' });
        outputGetPasswordCharacterSet.put("letters", namedCharacterSet);
        doReturn(outputGetPasswordCharacterSet).when(dSCommunicator).getPasswordCharacterSet();
        doReturn("ExceptionString").when(dSCommunicator).getPasswordFormat();
        when(dSCommunicator.getRandomPassword()).thenCallRealMethod();
        final String randomPassword = dSCommunicator.getRandomPassword();
        assertNull(randomPassword);
    }

    /**
     * This test checks if random password will be generated. Name of character set: alphabet, length of random password that will be generated: 6,
     * character used to generate password: characters
     */
    @Test
    public void getRandomPasswordVerifyIsSuccessTest() {
        final Map<String, NamedCharacterSet> outputGetPasswordCharacterSet = new HashMap<String, NamedCharacterSet>();
        final NamedCharacterSet namedCharacterSet = new NamedCharacterSet("alphabet", new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' });
        outputGetPasswordCharacterSet.put("letters", namedCharacterSet);
        doReturn(outputGetPasswordCharacterSet).when(dSCommunicator).getPasswordCharacterSet();
        doReturn("letters:8").when(dSCommunicator).getPasswordFormat();
        when(dSCommunicator.getRandomPassword()).thenCallRealMethod();
        final String randomPassword = dSCommunicator.getRandomPassword();
        assertNotNull(randomPassword);
    }

    @Test
    public void modifyEntryReplaceMultipleAtrritbutesSuccess() throws DSCommunicatorException, ErrorResultException {
        doReturn(result).when(connection).modify(any(ModifyRequest.class));
        doCallRealMethod().when(dSCommunicator).modifyEntryReplaceMultipleAttributes(anyString(), any(HashMap.class));
        dSCommunicator.modifyEntryReplaceMultipleAttributes(fullDNPath, attributesToModify);
        final ModifyRequest expectedModifyRequest = prepareExpectedModifyRequest(fullDNPath, attributesToModify, ModificationType.REPLACE);

        final ArgumentCaptor<ModifyRequest> argument = ArgumentCaptor.forClass(ModifyRequest.class);
        verify(connection).modify(argument.capture());
        final ModifyRequest realModifyRequest = argument.getValue();
        assertModifyRequest(expectedModifyRequest, realModifyRequest);

    }

    private void assertModifyRequest(final ModifyRequest expectedModifyRequest, final ModifyRequest realModifyRequest) {
        assertEquals(expectedModifyRequest.getName(), realModifyRequest.getName());
        assertEquals(expectedModifyRequest.getModifications().size(), realModifyRequest.getModifications().size());

        for (int i = 0; i < realModifyRequest.getModifications().size(); i++) {

            final Modification realModification = realModifyRequest.getModifications().get(i);
            final Modification expectedModification = expectedModifyRequest.getModifications().get(i);

            assertEquals(expectedModification.getAttribute(), realModification.getAttribute());
            assertEquals(expectedModification.getModificationType(), realModification.getModificationType());
        }
    }

    private ModifyRequest prepareExpectedModifyRequest(final String DN, final HashMap<String, ArrayList<String>> attributes,
                                                       final ModificationType modificationType) {
        final ModifyRequest expectedModifyRequest = Requests.newModifyRequest(DN);
        for (final String attrName : attributes.keySet()) {
            final ArrayList<String> values = attributes.get(attrName);
            for (final String value : values) {
                expectedModifyRequest.addModification(modificationType, attrName, value);
            }
        }
        return expectedModifyRequest;
    }
}
