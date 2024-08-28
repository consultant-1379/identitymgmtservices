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

import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.dto.ComAAInfoDto;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandInfo;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandResource;
import com.ericsson.oss.services.security.genericidentity.commons.recording.CommandSource;

/**
 * Interface invoked by REST interface.
 * It's wrapper of ComAAInfo interface, introduced to easily manage @Authorize annotation
 */

public interface ComAAInfoDelegate {

    ComAAInfoDto getComAAInfoConnectionData(@CommandSource String source, @CommandResource
            String resource, @CommandInfo String info);
}
