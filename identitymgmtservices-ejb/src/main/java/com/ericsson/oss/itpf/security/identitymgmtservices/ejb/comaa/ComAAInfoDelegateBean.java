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
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa;

import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ComAAInfo;
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ConnectionData;
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.dto.ComAAInfoDto;
import com.ericsson.oss.services.security.genericidentity.commons.exceptions.handlers.ApplicationExceptionHandled;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandInfo;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandResource;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandSource;
import com.ericsson.oss.services.security.genericidentity.commons.recording.RecordedCommand;

import javax.ejb.Stateless;

@Stateless
public class ComAAInfoDelegateBean implements  ComAAInfoDelegate {

    private static final String IDENTITY_MANAGEMENT_SETTINGS = "identitymgmt_settings_taf";
    private static final String EXECUTE = "execute";

    @EServiceRef
    private ComAAInfo comAAInfo;

    @Override
    @Authorize(resource = IDENTITY_MANAGEMENT_SETTINGS, action = EXECUTE)
    @ApplicationExceptionHandled
    @RecordedCommand
    public ComAAInfoDto getComAAInfoConnectionData(@CommandSource final String source, @CommandResource final String resource, @CommandInfo final String info) {

        /* Perform action command*/
        ConnectionData connectionData = comAAInfo.getConnectionData();

        /* fill egress dto chain with proper parameters retrieved from command response */
        return new ComAAInfoDto(connectionData.getIpv4AddressData().getPrimary(),
                connectionData.getIpv4AddressData().getFallback(),
                connectionData.getIpv6AddressData().getPrimary(),
                connectionData.getIpv6AddressData().getFallback(),
                connectionData.getLdapTlsPort(),
                connectionData.getLdapsPort());
    }
}
