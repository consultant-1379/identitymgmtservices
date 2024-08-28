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
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.*;
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.IdmsUserState;
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus;
import com.ericsson.oss.services.security.genericidentity.commons.exceptions.handlers.ApplicationExceptionHandled;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandInfo;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandResource;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandSource;
import com.ericsson.oss.services.security.genericidentity.commons.recording.RecordedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus.valueOfProxyAgentAccoountAdminStatus;

@Stateless
public class PosixServiceDelegateBean implements PosixServiceDelegate {

    private static final Logger logger = LoggerFactory.getLogger(PosixServiceDelegateBean.class);
    private static final String IDENTITY_MANAGEMENT_SETTINGS = "identitymgmt_settings_taf";
    private static final String READ = "read";
    private static final String EXECUTE = "execute";
    private static final String DELETE = "delete";

    @EServiceRef
    private PosixService posixService;

    @EServiceRef
    private IdentityManagementService identityManagementService;
    
    @Inject
    private PasswordHelper passwordHelper;

    @Override
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = READ)
    @ApplicationExceptionHandled
    @RecordedCommand
    public IdmsComUserGroupDto isComUser(IdmsUserDto idmsUserDto, @CommandSource final String source, @CommandResource final String resource, @CommandInfo final String info) {

        /* Perform action command*/
        Boolean isComUser = posixService.isComUser(idmsUserDto.getUserName());

        /* fill egress dto chain with proper parameters retrieved from command response */
        return new IdmsComUserGroupDto(idmsUserDto.getUserName(), Boolean.toString(isComUser));
    }

    @Override
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = EXECUTE)
    @ApplicationExceptionHandled
    @RecordedCommand
    public IdmsReadProxyAccountUserDto createProxyAgentAccount(@CommandSource final String source, @CommandResource final String resource, @CommandInfo final String info) {

        /* Perform action command*/
        ProxyAgentAccountData proxyAgentAccountData = identityManagementService.createProxyAgentAccount();

        /* fill egress dto chain with proper parameters retrieved from command response */
        String encryptedPassword = passwordHelper.encryptEncode(proxyAgentAccountData.getUserPassword());
        return  new IdmsReadProxyAccountUserDto(proxyAgentAccountData.getUserDN(),
                IdmsUserState.IDMS_USER_EXISTING.toString(),
                encryptedPassword
        );
    }

    @Override
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = DELETE)
    @ApplicationExceptionHandled
    @RecordedCommand
    public IdmsUserDnStateDto deleteProxyAgentAccount(IdmsUserDnDto idmsUserDnDto, @CommandSource final String source, @CommandResource final String resource, @CommandInfo final String info) {

        /* Perform action command*/
        boolean isDeleted = identityManagementService.deleteProxyAgentAccount(idmsUserDnDto.getUserNameDn());

        /* fill egress dto chain with proper parameters retrieved from command response */
        String idmsUserDnStateDto = ( isDeleted ) ? IdmsUserState.IDMS_USER_DELETED.toString():
                IdmsUserState.IDMS_USER_NOT_DELETED.toString();

        return  new IdmsUserDnStateDto(idmsUserDnDto.getUserNameDn(), idmsUserDnStateDto);
    }

    @Override
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = READ)
    @ApplicationExceptionHandled
    @RecordedCommand
    public IdmsGetAllProxyAccountGetDataDto getAllProxyAgentAccount(final IdmsConfigGetProxyAccountBaseDto idmsConfigGetProxyAccountBaseDto,
                                                                    @CommandSource final String source, @CommandResource final String resource,
                                                                    @CommandInfo final String info) {

        Boolean isLegacy = Boolean.valueOf(idmsConfigGetProxyAccountBaseDto.getIsLegacy());
        Boolean isSummary = Boolean.valueOf(idmsConfigGetProxyAccountBaseDto.getIsSummary());

        ProxyAgentAccountGetData allProxyAccountsGetData = identityManagementService.getProxyAgentAccount(isLegacy, isSummary);
        return getAllProxyAgentAccountGeneric(allProxyAccountsGetData);
    }

    @Override
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = READ)
    @ApplicationExceptionHandled
    @RecordedCommand
    public IdmsGetAllProxyAccountGetDataDto getAllProxyAgentAccountByAdminStatus(final IdmsConfigGetProxyAccountByAdminStatusDto idmsConfigGetProxyAccountByAdminStatusDto,
                                                                                 @CommandSource final String source, @CommandResource final String resource,
                                                                                 @CommandInfo final String info) {

        Boolean isLegacy = Boolean.valueOf(idmsConfigGetProxyAccountByAdminStatusDto.getIsLegacy());
        Boolean isSummary = Boolean.valueOf(idmsConfigGetProxyAccountByAdminStatusDto.getIsSummary());
        ProxyAgentAccountAdminStatus adminStatus = valueOfProxyAgentAccoountAdminStatus(idmsConfigGetProxyAccountByAdminStatusDto.getAdminStatus());

        ProxyAgentAccountGetData allProxyAccountsGetData = identityManagementService.getProxyAgentAccountByAdminStatus(adminStatus, isLegacy, isSummary);
        return getAllProxyAgentAccountGeneric(allProxyAccountsGetData);
    }

    @ApplicationExceptionHandled
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = READ)
    @RecordedCommand
    public IdmsGetAllProxyAccountGetDataDto getAllProxyAgentAccountByInactivityPeriod(final IdmsConfigGetProxyAccountByInactivityDateDto idmsConfigGetProxyAccountByInactivityDateDto,
                                                                                      @CommandSource final String source,
                                                                                      @CommandResource final String resource,
                                                                                      @CommandInfo final String info) {

        Boolean isLegacy = Boolean.valueOf(idmsConfigGetProxyAccountByInactivityDateDto.getIsLegacy());
        Boolean isSummary = Boolean.valueOf(idmsConfigGetProxyAccountByInactivityDateDto.getIsSummary());

        if(idmsConfigGetProxyAccountByInactivityDateDto.getInactivityDate() == null) {
            final String errMsg = "Inactivity Date is null";
            throw new IdentityManagementServiceException(errMsg);
        }

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(idmsConfigGetProxyAccountByInactivityDateDto.getInactivityDate());
            Long inactivityPeriod = date.getTime();
            ProxyAgentAccountGetData allProxyAccountsGetData = identityManagementService.getProxyAgentAccountByInactivityPeriod(inactivityPeriod, isLegacy, isSummary);
            return getAllProxyAgentAccountGeneric(allProxyAccountsGetData);
        } catch (ParseException e) {
            final String errMsg = "Problem Parsing Inactivity Date, error " + e.getMessage();
            logger.error(errMsg, e);
            throw new IdentityManagementServiceException(errMsg);
        }
    }

    private IdmsGetAllProxyAccountGetDataDto getAllProxyAgentAccountGeneric(final ProxyAgentAccountGetData allProxyAccountsGetData) {

        List<ProxyAgentAccountDetails> proxyAgentAccountDetailsList = allProxyAccountsGetData.getProxyAgentAccountDetailsList();

        IdmsGetAllProxyAccountGetDataDto idmsGetAllProxyAccountGetDataDto = new IdmsGetAllProxyAccountGetDataDto();
        List<IdmsGetAllProxyAccountDto> idmsGetAllProxyAccountDto = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
        for (ProxyAgentAccountDetails proxyAccount : proxyAgentAccountDetailsList) {
            logger.trace("ProxyAgentAccountDetails: {} {} {} {}", proxyAccount.getUserDn(),
                    proxyAccount.getCreateTimestamp(),  proxyAccount.getLastLoginTime(), proxyAccount.getAdminStatus());
            IdmsGetAllProxyAccountDto idmsGetAllProxyAccount = new IdmsGetAllProxyAccountDto();
            idmsGetAllProxyAccount.setIdmsUserDn(proxyAccount.getUserDn());

            if(proxyAccount.getCreateTimestamp() !=null) {
                Date createTimestampDate = new Date(proxyAccount.getCreateTimestamp());
                idmsGetAllProxyAccount.setIdmsCreateTimestamp(formatter.format(createTimestampDate));
            }

            if(proxyAccount.getLastLoginTime() !=null) {
                Date lastLoginTimeDate = new Date(proxyAccount.getLastLoginTime());
                idmsGetAllProxyAccount.setIdmsLastLoginTime(formatter.format(lastLoginTimeDate));
            }

            if(proxyAccount.getAdminStatus() !=null) {
                idmsGetAllProxyAccount.setIdmsAccountDisabled(proxyAccount.getAdminStatus().toString());
            }

            idmsGetAllProxyAccountDto.add(idmsGetAllProxyAccount);
        }
        idmsGetAllProxyAccountGetDataDto.setIdmsGetAllProxyAccountDtos(idmsGetAllProxyAccountDto);

        idmsGetAllProxyAccountGetDataDto.setNumOfGetDataProxyAccount(allProxyAccountsGetData.getProxyAgentAccountCounters().getNumOfProxyAccount());
        idmsGetAllProxyAccountGetDataDto.setNumOfGetDataProxyAccountLegacy(allProxyAccountsGetData.getProxyAgentAccountCounters().getNumOfProxyAccountLegacy());
        idmsGetAllProxyAccountGetDataDto.setNumOfGetDataRequestedProxyAccount(allProxyAccountsGetData.getProxyAgentAccountCounters().getNumOfRequestedProxyAccount());
        idmsGetAllProxyAccountGetDataDto.setNumOfGetDataRequestedProxyAccountLegacy(allProxyAccountsGetData.getProxyAgentAccountCounters().getNumOfRequestedProxyAccountLegacy());

        return idmsGetAllProxyAccountGetDataDto;
    }

    @Override
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = EXECUTE)
    @ApplicationExceptionHandled
    @RecordedCommand
    public Boolean updateProxyAgentAccountAdminStatus(final String userNameDn,
                                                      final IdmsConfigUpdateProxyAccountDto idmsConfigUpdateProxyAccountDto,
                                                      @CommandSource final String source, @CommandResource final String resource,
                                                      @CommandInfo final String info) {

        ProxyAgentAccountAdminStatus adminStatus = valueOfProxyAgentAccoountAdminStatus(idmsConfigUpdateProxyAccountDto.getAdminStatus());
        return identityManagementService.updateProxyAgentAccountAdminStatus(userNameDn, adminStatus);
    }

    @Override
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = READ)
    @ApplicationExceptionHandled
    @RecordedCommand
    public IdmsGetAllProxyAccountDto getProxyAgentAccountDetails(final String userDn, @CommandSource final String source, @CommandResource final String resource, @CommandInfo final String info) {

        ProxyAgentAccountDetails proxyAgentAccountDetails = identityManagementService.getProxyAgentAccountDetails(userDn);

        IdmsGetAllProxyAccountDto idmsGetAllProxyAccount = new IdmsGetAllProxyAccountDto();
        idmsGetAllProxyAccount.setIdmsUserDn(proxyAgentAccountDetails.getUserDn());

        if(proxyAgentAccountDetails.getAdminStatus() !=null) {
            idmsGetAllProxyAccount.setIdmsAccountDisabled(proxyAgentAccountDetails.getAdminStatus().toString());
        }
        return idmsGetAllProxyAccount;
    }
}