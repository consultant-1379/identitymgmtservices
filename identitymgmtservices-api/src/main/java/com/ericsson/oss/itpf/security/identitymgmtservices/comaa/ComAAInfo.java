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

package com.ericsson.oss.itpf.security.identitymgmtservices.comaa;

import javax.ejb.Remote;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;

/**
 * This interface allows to get COM AA Service information. For now IP addresses of two service instances can be
 * retrieved.
 *
 * Now this interface is implemented by :
 *      identitymgmt-services
 *      node-security
 *      ap-workflow-ecim
 */
@EService
@Remote
public interface ComAAInfo {

    /**
     * This method is deprecated. Use getConnectionData() instead.This method returns IP address of COM AA Service. This
     * address could be used by node to communicate with COM AA Service.
     * @deprecated   Replaced by  {@link #getConnectionData()
     *
     * @return - IP address of COM AA Service
     *
     */
    @Deprecated
    String getCOMAAIpAddress();

    /**
     * This method is deprecated. Use getConnectionData() instead. This method returns fallback IP address of COM AA
     * Service. This address could be used by node to communicate with COM AA Service when other instance of jboss is .
     * @deprecated   Replaced by  {@link #getConnectionData()
     * @return - IP address of COM AA Service
     *
     */
    @Deprecated
    String getCOMAAFallbackIPAddress();

    /**
     * This method returns ConnectionData which contains IPs and ports required to connect with with com-aa-service
     * 
     * @return - IPs and ports required to connect with with com-aa-service
     */
    ConnectionData getConnectionData();
}
