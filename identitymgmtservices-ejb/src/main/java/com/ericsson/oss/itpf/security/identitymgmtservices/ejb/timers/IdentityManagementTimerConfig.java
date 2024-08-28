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

public class IdentityManagementTimerConfig {

    private final String name;
    private final IdentityManagementTimerType type;
    private final long initialDuration;
    private final long  duration;
    private final IdentityManagementTimerCallback handle;

    public IdentityManagementTimerConfig(String name, IdentityManagementTimerType type, long initialDuration, long duration,
                                         IdentityManagementTimerCallback handle) {
        this.name = name;
        this.type = type;
        this.initialDuration = initialDuration;
        this.duration = duration;
        this.handle = handle;
    }

    public String getName() {
        return name;
    }

    public IdentityManagementTimerType getType() {
        return type;
    }

    public long getInitialDuration() {
        return initialDuration;
    }

    public long getDuration() {
        return duration;
    }

    public IdentityManagementTimerCallback getHandle() {
        return handle;
    }
}
