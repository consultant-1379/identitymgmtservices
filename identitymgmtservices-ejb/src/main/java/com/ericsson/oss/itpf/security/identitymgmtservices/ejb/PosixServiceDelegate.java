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

import com.ericsson.oss.itpf.sdk.core.annotation.EService;
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.*;
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.*;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandInfo;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandResource;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandSource;

import javax.ejb.Local;
import java.text.ParseException;

/**
 * Interface invoked by REST interface.
 * It's wrapper of IdentityManagementService interface (for posix account), introduced to easily manage @Authorize annotation
 */

@EService
@Local
public interface PosixServiceDelegate {

    IdmsReadProxyAccountUserDto createProxyAgentAccount(final String source, final String resource, final String info);

    IdmsUserDnStateDto deleteProxyAgentAccount(final IdmsUserDnDto idmsUserDnDto, final String source,
                                               final String resource, final String info);

    IdmsComUserGroupDto isComUser(final IdmsUserDto idmsUserDto, final String source, final String resource, final String info);

    IdmsGetAllProxyAccountGetDataDto getAllProxyAgentAccount(final IdmsConfigGetProxyAccountBaseDto idmsConfigGetProxyAccountBaseDto,
                                                             final String source, final String resource, final String info);
    IdmsGetAllProxyAccountGetDataDto getAllProxyAgentAccountByAdminStatus(final IdmsConfigGetProxyAccountByAdminStatusDto idmsConfigGetProxyAccountByAdminStatusDto,
                                                                          final String source, final String resource, final String info);

    IdmsGetAllProxyAccountGetDataDto getAllProxyAgentAccountByInactivityPeriod(final IdmsConfigGetProxyAccountByInactivityDateDto idmsConfigGetProxyAccountByInactivityDateDto,
                                                                               final String source, final String resource, final String info) throws ParseException;


    IdmsGetAllProxyAccountDto getProxyAgentAccountDetails(final String userDn, @CommandSource final String source, @CommandResource final String resource, @CommandInfo final String info);

    Boolean updateProxyAgentAccountAdminStatus(final String userNameDn, final IdmsConfigUpdateProxyAccountDto idmsConfigGetProxyAccountDto,
                                               String source, String resource, String info);
}
