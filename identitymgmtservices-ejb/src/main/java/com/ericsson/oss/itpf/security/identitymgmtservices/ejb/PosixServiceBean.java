/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.security.identitymgmtservices.*;

@Stateless
public class PosixServiceBean implements PosixService {

    private static final String CONTAINER = "People";

    @Inject
    protected Logger logger;

    @Inject
    private SecurityManagerBean securityManagerBean;

    @Override
    public void addPosixAttributes(final String userName, final String groupName, final String homeDirectory,
                                   final String loginShell) {
        try {
            securityManagerBean.addPosixAttributes(CONTAINER, userName, groupName, homeDirectory, loginShell);
        } catch (final IdentityManagementServiceException e) {
            throw new PosixServiceException(e);
        }
    }

    @Override
    public void removePosixAttributes(final String userName, final String groupName) {
        try {
            securityManagerBean.removePosixAttributes(CONTAINER, userName, groupName);
        } catch (final IdentityManagementServiceException e) {
            throw new PosixServiceException(e);
        }
    }

    @Override
    public boolean isComUser(final String userName) {
        logger.debug("isComUser userName: {}", userName);
        try {
            final List<String> comUsers = securityManagerBean.getComUsers();
            return comUsers.contains(userName);
        } catch (final IdentityManagementServiceException e) {
            throw new PosixServiceException(e);
        }
    }

    @Override
    public void removeMemberUidFromComUsersGroup(final String memberUid) {
        try {
            securityManagerBean.removeMemberUidFromComUsersGroup(memberUid);
        } catch (final IdentityManagementServiceException e) {
            throw new PosixServiceException(e);
        }
    }
}
