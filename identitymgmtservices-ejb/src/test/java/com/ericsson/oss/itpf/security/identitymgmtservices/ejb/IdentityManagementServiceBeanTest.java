/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ericsson.oss.itpf.security.identitymgmtservices.M2MUserPassword;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException;
import com.ericsson.oss.itpf.security.identitymgmtservices.M2MUser;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountData;

@RunWith(MockitoJUnitRunner.class)
public class IdentityManagementServiceBeanTest {

    @Mock
    private SecurityManagerBean securityManagerBean;

    @InjectMocks
    private IdentityManagementServiceBean identityManagementService;

    private final M2MUser m2user = new M2MUser("name1", "groupName", 1, 1, "homeDir", "123");

    private final M2MUser m2userSpy = spy(m2user);

    private final ProxyAgentAccountData proxyUser = new ProxyAgentAccountData("userDN", "userPassword");

    @Test
    public void createM2MUserTest() {
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                return invocation;
            }
        }).when(securityManagerBean).addM2MUser(anyString(), anyString(), anyString(), anyInt());
        doReturn(m2userSpy).when(securityManagerBean).getM2MUser(anyString());
        final M2MUser m2mUser = identityManagementService.createM2MUser("name1", "groupName", "homeDir", 123);
        assertEquals(m2mUser, m2userSpy);
    }

    @Test(expected = IdentityManagementServiceException.class)
    public void createM2MUserException() {
        final IdentityManagementServiceException idmException = new IdentityManagementServiceException("Error",
                IdentityManagementServiceException.Error.UNEXPECTED_ERROR);
        final IdentityManagementServiceException idmExceptionSpy = spy(idmException);
        doThrow(idmExceptionSpy).when(securityManagerBean).addM2MUser(anyString(), anyString(), anyString(), anyInt());
        identityManagementService.createM2MUser("name1", "groupName", "homeDir", 123);
    }

    @Test
    public void isExistingM2MUserTest() {
        // check if exists
        doReturn(true).when(securityManagerBean).isExistingM2MUser(anyString());
        assertEquals(true, identityManagementService.isExistingM2MUser("name1"));

        // check if don't exists
        doReturn(false).when(securityManagerBean).isExistingM2MUser(anyString());
        assertEquals(false, identityManagementService.isExistingM2MUser("name2"));
    }

    @Test
    public void removeM2MUser() {
        // remove user
        doReturn(true).when(securityManagerBean).removeM2MUser(anyString());
        assertEquals(true, identityManagementService.deleteM2MUser("name1"));

        // remove user
        doReturn(false).when(securityManagerBean).removeM2MUser(anyString());
        assertEquals(false, identityManagementService.deleteM2MUser("name1"));
    }

    @Test
    public void getM2MUser() {
        doReturn(m2userSpy).when(securityManagerBean).getM2MUser(anyString());
        assertEquals(m2userSpy, identityManagementService.getM2MUser("user1"));
    }

    @Test
    public void createM2MUserPasswordTest() {
        M2MUserPassword m2MUserPassword = new M2MUserPassword(m2user, "password");
        doReturn(m2MUserPassword).when(securityManagerBean).createM2MUser(anyString(), anyString(), anyString(), anyInt());
        M2MUserPassword m2MUserPasswordSet = identityManagementService.createM2MUserPassword("name1", "groupName", "homeDir", 1);
        assertEquals(m2MUserPassword.getPassword(), m2MUserPasswordSet.getPassword());
    }

    @Test
    public void getM2MUserPasswordTest() {
        doReturn("password").when(securityManagerBean).getM2MPassword(anyString());
        final char[] m2mPassword = identityManagementService.getM2MPassword("user1");
        final char[] password = "password".toCharArray();
        assertTrue(Arrays.equals(password, m2mPassword));
    }

    @Test
    public void getM2MUserPasswordTestNull() {
        doReturn(null).when(securityManagerBean).getM2MPassword(anyString());
        final char[] m2mPassword = identityManagementService.getM2MPassword("user1");
        assertEquals(null, m2mPassword);
    }

    @Test
    public void updateM2MUserPasswordTest() {
        doReturn("password").when(securityManagerBean).updateM2MPassword(anyString());
        final char[] m2mPassword = identityManagementService.updateM2MPassword("user1");
        final char[] password = "password".toCharArray();
        assertTrue(Arrays.equals(password, m2mPassword));
    }

    @Test
    public void updateM2MUserPasswordTestNull() {
        doReturn(null).when(securityManagerBean).updateM2MPassword(anyString());
        final char[] m2mPassword = identityManagementService.updateM2MPassword("user1");
        assertEquals(null,m2mPassword);
    }

    @Test
    public void getAllTargetGroupTest() {
        final List<String> list = new ArrayList<String>();
        list.add("group1");
        list.add("group2");
        list.add("group3");
        doReturn(list).when(securityManagerBean).getAllTargetGroups();
        final List<String> groups = identityManagementService.getAllTargetGroups();
        assertTrue(Arrays.equals(list.toArray(), groups.toArray()));
    }

    @Test
    public void getDefaultTargetGroupTest() {
        doReturn("defaultTargetGroup").when(securityManagerBean).getDefaultTargetGroup();
        final String defaultTargetGroup = identityManagementService.getDefaultTargetGroup();
        assertTrue("defaultTargetGroup".equals(defaultTargetGroup));
    }

    @Test
    public void validateTargetGroupsTest() {
        final List<String> list = new ArrayList<String>();
        list.add("group1");
        list.add("group2");
        list.add("group3");
        doReturn(list).when(securityManagerBean).validateTargetGroups(list);
        final List<String> validatedTargetGroups = identityManagementService.validateTargetGroups(list);
        assertTrue(Arrays.equals(list.toArray(), validatedTargetGroups.toArray()));
    }

    @Test
    public void createProxyAgentAccountTest() {

        when(securityManagerBean.addProxyAccount()).thenReturn(proxyUser);
        assertEquals(identityManagementService.createProxyAgentAccount(), proxyUser);
    }

    @Test
    public void deleteProxyAgentAccountTest() {
        // remove user
        when(securityManagerBean.removeProxyAccount("userDN")).thenReturn(true);
        assertEquals(true, identityManagementService.deleteProxyAgentAccount("userDN"));

        // remove user
        when(securityManagerBean.removeProxyAccount("userDN")).thenReturn(false);
        assertEquals(false, identityManagementService.deleteProxyAgentAccount("userDN"));
    }

    @Test(expected = IdentityManagementServiceException.class)
    public void createProxyAgentAccountException() {
        final IdentityManagementServiceException idmException = new IdentityManagementServiceException();

        when(securityManagerBean.addProxyAccount()).thenThrow(idmException);
        identityManagementService.createProxyAgentAccount();
    }

    @Test(expected = IdentityManagementServiceException.class)
    public void deleteProxyAgentAccountException() {
        final IdentityManagementServiceException idmException = new IdentityManagementServiceException();

        when(securityManagerBean.removeProxyAccount("userDN")).thenThrow(idmException);
        identityManagementService.deleteProxyAgentAccount("userDN");
    }
}
