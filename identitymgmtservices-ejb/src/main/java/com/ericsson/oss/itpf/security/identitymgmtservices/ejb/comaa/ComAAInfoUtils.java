/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa;

public class ComAAInfoUtils {

    /**
     * getSystemParemeters: method that hides details of getting system parameters.
     *                      They can be get from system environment(immutable) or from
     *                      system properties(changeable at runtime)
     * @param key : the parameters stored in the system map
     * @param sysParamType : type of system parameters, now it can be
     *                       COM_AA_INFO_SYSTEM_ENV for environment parameters
     *                       COM_AA_INFO_SYSTEM_PROPERTIES for system properties
     * @return the parameter addressed by key input
     */
    public String getSystemParemeters ( final String key, final ComAAInfoSystemType sysParamType) {
        switch (sysParamType) {
            case COM_AA_INFO_SYSTEM_ENV:
                return System.getenv(key);
            case COM_AA_INFO_SYSTEM_PROPERTIES:
            default:
                return System.getProperty(key);
        }
    }
}
