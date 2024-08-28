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

import javax.ejb.Local;

/**
 * Interface invoked by REST interface.
 * It's wrapper of IdentityManagementService interface (for m2muser), introduced to easily manage @Authorize annotation
 */

@EService
@Local
public interface IdentityManagementServiceDelegate {

    IdmsReadM2MUserExtDto configM2MUserPassword(final IdmsConfigM2MUserDto idmsConfigM2MUserDto, final String source,
                                                final String resource, final String info);

    IdmsUserStateDto deleteM2MUser(final IdmsUserDto idmsUserDto, final String source, final String resource, final String info);

    IdmsReadM2MUserDto getM2MUser(final IdmsUserDto idmsUserDto, final String source, final String resource, final String info);

    IdmsUserStateDto isExistingM2MUser(final IdmsUserDto idmsUserDto, final String source, final String resource, final String info);

    IdmsPasswordDto getM2MPassword(final IdmsUserDto idmsUserDto, final String source, final String resource, final String info);

    IdmsPasswordDto updateM2MPassword(final IdmsUserDto idmsUserDto, final String source, final String resource, final String info);
}
