/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException;
import com.ericsson.oss.itpf.security.identitymgmtservices.PosixServiceException;

@RunWith(MockitoJUnitRunner.class)
public class PosixServiceTest {

    private static final String CORRECT_NAME = "correct_name";
    private static final String WRONG_NAME = "wrong_name";

    private static final String CONTAINER = "People";
    private static final String USER_NAME = "UserName";
    private static final String GROUP_NAME = "GroupName";
    private static final String HOME_DIR = "Homedir";
    private static final String SHELL= "/bin/sh";

    @InjectMocks
    PosixServiceBean posixService;

    @Mock
    SecurityManagerBean securityManagerBean;

    @Mock
    Logger logger;

    @Test
    public void testIsComUserSuccess() {

        List<String> users = new ArrayList<String>();
        users.add(CORRECT_NAME);
        when(securityManagerBean.getComUsers()).thenReturn(users);

        boolean isComUser = posixService.isComUser(CORRECT_NAME);
        assertTrue(isComUser);
        verify(securityManagerBean).getComUsers();
    }

    @Test
    public void testIsComUserFail() {

        List<String> users = new ArrayList<String>();
        users.add(CORRECT_NAME);
        when(securityManagerBean.getComUsers()).thenReturn(users);

        boolean isComUser = posixService.isComUser(WRONG_NAME);
        assertFalse(isComUser);
        verify(securityManagerBean).getComUsers();
    }

    @Test
    public void testIsComUserThrowException() {

        String message = "message";

        when(securityManagerBean.getComUsers()).thenThrow(new IdentityManagementServiceException(message));

        try {
            posixService.isComUser(WRONG_NAME);
            fail();
        } catch (PosixServiceException e) {
            assertEquals(message, e.getMessage());
        }
        verify(securityManagerBean).getComUsers();
    }

    @Test
    public void testRemoveMemberUidFromComUsersGroup() {

        String memberUid = "some_uid";
        doNothing().when(securityManagerBean).removeMemberUidFromComUsersGroup(memberUid);

        posixService.removeMemberUidFromComUsersGroup(memberUid);

        verify(securityManagerBean).removeMemberUidFromComUsersGroup(memberUid);

    }

    @Test(expected = PosixServiceException.class)
    public void testRemoveMemberUidFromComUsersGroupException() {

        String memberUid = "some_uid";
        String message = "message";
        Throwable toBeThrown = new IdentityManagementServiceException(message);
        doThrow(toBeThrown).when(securityManagerBean).removeMemberUidFromComUsersGroup(memberUid);

        posixService.removeMemberUidFromComUsersGroup(memberUid);
    }

    @Test
    public void testAddPosixAttributes() {
        when(securityManagerBean.addPosixAttributes(CONTAINER,USER_NAME,GROUP_NAME, HOME_DIR, SHELL)).thenReturn("ok");
        posixService.addPosixAttributes(USER_NAME,GROUP_NAME, HOME_DIR, SHELL);
        verify(securityManagerBean).addPosixAttributes(CONTAINER,USER_NAME,GROUP_NAME, HOME_DIR, SHELL);
    }

    @Test(expected = PosixServiceException.class)
    public void testAddPosixAttributesException() {
        IdentityManagementServiceException identityManagementServiceException = new IdentityManagementServiceException("message");
        Throwable toBeThrown = new PosixServiceException(identityManagementServiceException);

        doThrow(toBeThrown).when(securityManagerBean).addPosixAttributes(CONTAINER,USER_NAME,GROUP_NAME, HOME_DIR, SHELL);
        posixService.addPosixAttributes(USER_NAME,GROUP_NAME, HOME_DIR, SHELL);
    }

    @Test
    public void removeAddPosixAttributes() {
        when(securityManagerBean.removePosixAttributes(CONTAINER,USER_NAME,GROUP_NAME)).thenReturn(null);
        posixService.removePosixAttributes(USER_NAME,GROUP_NAME);
        verify(securityManagerBean).removePosixAttributes(CONTAINER,USER_NAME,GROUP_NAME);
    }

    @Test(expected = PosixServiceException.class)
    public void testRemovePosixAttributesException() {
        IdentityManagementServiceException identityManagementServiceException = new IdentityManagementServiceException("message");
        Throwable toBeThrown = new PosixServiceException(identityManagementServiceException);

        doThrow(toBeThrown).when(securityManagerBean).removePosixAttributes(CONTAINER,USER_NAME,GROUP_NAME);
        posixService.removePosixAttributes(USER_NAME,GROUP_NAME);
    }
}
