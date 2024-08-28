/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.timers;

public enum IdentityManagementTimerType {

    INTERVAL("Interval"),
    SINGLE_ACTION("SingleAction");

    public final String timerType ;

    IdentityManagementTimerType(String timerType) {
        this.timerType = timerType;
    }
}
