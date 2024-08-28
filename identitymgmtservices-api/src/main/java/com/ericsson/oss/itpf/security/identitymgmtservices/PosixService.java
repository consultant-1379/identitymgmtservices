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
package com.ericsson.oss.itpf.security.identitymgmtservices;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;

import javax.ejb.Remote;

/**
 * This service allows to add and remove posix attributes to user and to check if user is in com_users group
 *
 * Created by xkrzsal on 10/29/2015.
 *
 * Deprecated but used now by:
 *      identitymgmt-services
 *      generic-identity-mgmt-services
 */
@EService
@Remote
public interface PosixService {

    /**
     * @deprecated   Replaced by GIM application
     * @param userName userName
     * @param groupName groupName
     * @param homeDirectory homeDirectory
     * @param loginShell loginShell
     */
    @Deprecated
    void addPosixAttributes(String userName, String groupName, String homeDirectory, String loginShell);

    /**
     * @deprecated   Replaced by GIM application
     * @param userName userName
     * @param groupName groupName
     */
    @Deprecated
    void removePosixAttributes(String userName, String groupName);

    /**
     * check if userName belongs to ComUser Group
     * @param userName userName
     * @return if comUser
     */
    boolean isComUser(String userName);

    /**
     * @deprecated not longer implemented
     * @param memberUid
     */
    @Deprecated
    void removeMemberUidFromComUsersGroup(String memberUid);
}
