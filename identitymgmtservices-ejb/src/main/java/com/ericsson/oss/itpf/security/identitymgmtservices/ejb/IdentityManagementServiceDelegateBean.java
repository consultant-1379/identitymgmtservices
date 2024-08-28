/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2021
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb;

import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.itpf.security.identitymgmtservices.*;
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.*;
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.IdmsUserState;
import com.ericsson.oss.services.security.genericidentity.commons.exceptions.handlers.ApplicationExceptionHandled;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandInfo;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandResource;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandSource;
import com.ericsson.oss.services.security.genericidentity.commons.recording.RecordedCommand;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Arrays;

@Stateless
public class IdentityManagementServiceDelegateBean implements IdentityManagementServiceDelegate {

    private static final String IDENTITY_MANAGEMENT_SETTINGS = "identitymgmt_settings_taf";
    private static final String EXECUTE = "execute";
    private static final String DELETE = "delete";
    private static final String UPDATE = "update";
    private static final String READ = "read";

    @EServiceRef
    private IdentityManagementService identityManagementService;

    @EServiceRef
    private PosixService posixService;

    @Inject
    private PasswordHelper passwordHelper;

    @Override
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = EXECUTE)
    @ApplicationExceptionHandled
    @RecordedCommand
    public IdmsReadM2MUserExtDto configM2MUserPassword(IdmsConfigM2MUserDto idmsConfigM2MUser, @CommandSource final String source, @CommandResource final String resource, @CommandInfo final String inf) {

        /* Perform creation of M2M user by extracting parameters from ingress dto */
        String userName = idmsConfigM2MUser.getUserName();
        String groupName = idmsConfigM2MUser.getGroupName();
        String homeDir = idmsConfigM2MUser.getHomeDir();
        int validDays = idmsConfigM2MUser.getValidDays();

        /* Perform action command*/
        M2MUserPassword m2MUserPassword = identityManagementService.createM2MUserPassword(userName, groupName, homeDir, validDays);

        /* fill egress dto chain with proper parameters retrieved from command response */
        String encryptedPassword = passwordHelper.encryptEncode(m2MUserPassword.getPassword());

        IdmsReadM2MUserExtDto idmsReadM2MUserExtDto = new IdmsReadM2MUserExtDto(m2MUserPassword.getUserName(),
                IdmsUserState.IDMS_USER_EXISTING.toString(),
                m2MUserPassword.getGroupName(),
                m2MUserPassword.getHomeDir(),
                m2MUserPassword.getUidNumber(),
                m2MUserPassword.getGidNumber(),
                m2MUserPassword.getExpiryTimestamp());

        /* To Overcome Sonar cube Issue (max constructor must have 7 parameters) */
        idmsReadM2MUserExtDto.setAdditionalParameters(encryptedPassword);

        return idmsReadM2MUserExtDto;
    }

    @Override
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = DELETE)
    @ApplicationExceptionHandled
    @RecordedCommand
    public IdmsUserStateDto deleteM2MUser(IdmsUserDto idmsUser, @CommandSource final String source, @CommandResource final String resource, @CommandInfo final String inf) {

        /* Perform action command*/
        boolean isDeleted = identityManagementService.deleteM2MUser(idmsUser.getUserName());

        /* fill egress dto chain with proper parameters retrieved from command response */
        String idmsUserState = ( isDeleted ) ? IdmsUserState.IDMS_USER_DELETED.toString():
                IdmsUserState.IDMS_USER_NOT_DELETED.toString();

        return new IdmsUserStateDto(idmsUser.getUserName(), idmsUserState);
    }

    @Override
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = READ)
    @ApplicationExceptionHandled
    @RecordedCommand
    public IdmsReadM2MUserDto getM2MUser(IdmsUserDto idmsUser, @CommandSource final String source, @CommandResource final String resource, @CommandInfo final String inf) {

        /* Perform action command*/
        M2MUser m2mUser = identityManagementService.getM2MUser(idmsUser.getUserName());

        return new IdmsReadM2MUserDto(m2mUser.getUserName(),
                IdmsUserState.IDMS_USER_EXISTING.toString(),
                m2mUser.getGroupName(),
                m2mUser.getHomeDir(),
                m2mUser.getUidNumber(),
                m2mUser.getGidNumber(),
                m2mUser.getExpiryTimestamp());
    }

    @Override
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = READ)
    @ApplicationExceptionHandled
    @RecordedCommand
    public IdmsUserStateDto isExistingM2MUser(IdmsUserDto idmsUser, @CommandSource final String source, @CommandResource final String resource, @CommandInfo final String info) {

        /* Perform action command*/
        boolean isExitingUser = identityManagementService.isExistingM2MUser(idmsUser.getUserName());

        String idmsUserState = ( isExitingUser ) ? IdmsUserState.IDMS_USER_EXISTING.toString():
                IdmsUserState.IDMS_USER_DELETED.toString();

        /* fill egress dto chain with proper parameters retrieved from command response */
        return new IdmsUserStateDto(idmsUser.getUserName(),
                idmsUserState);
    }

    @Override
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = READ)
    @ApplicationExceptionHandled
    @RecordedCommand
    public IdmsPasswordDto getM2MPassword(IdmsUserDto idmsUser, @CommandSource final String source, @CommandResource final String resource, @CommandInfo final String info) {

        /* Perform action command*/
        char [] m2mPassword = identityManagementService.getM2MPassword(idmsUser.getUserName());

        /* fill egress dto chain with proper parameters retrieved from command response */
        String encryptedPassword = passwordHelper.encryptEncode(Arrays.toString(m2mPassword));

        /* build egress dto chain */
        IdmsPasswordDto idmsPasswordDto = new IdmsPasswordDto();
        idmsPasswordDto.setEncryptedPwd(encryptedPassword);

        return idmsPasswordDto;
    }

    @Override
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = UPDATE)
    @ApplicationExceptionHandled
    @RecordedCommand
    public IdmsPasswordDto updateM2MPassword(IdmsUserDto idmsUser, @CommandSource final String source, @CommandResource final String resource, @CommandInfo final String info) {

        /* Perform action command*/
        char [] m2mPassword = identityManagementService.updateM2MPassword(idmsUser.getUserName());

        /* build egress dto chain */
        IdmsPasswordDto idmsPasswordDto = new IdmsPasswordDto();

        /* fill egress dto chain with proper parameters retrieved from command response */
        String encryptedPassword = passwordHelper.encryptEncode(Arrays.toString(m2mPassword));
        idmsPasswordDto.setEncryptedPwd(encryptedPassword);

        return idmsPasswordDto;
    }

}
