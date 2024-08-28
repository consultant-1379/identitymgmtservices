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
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.statistics;

import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class ProxyAccountGlobalCountersEvent {
    public static final String IDMS_SYSTEM_RECORDER_TAG = "IDENTITY_MGMT_SERVICES";
    public static final String IDMS_PROXY_GLB_CNT_EVT_DATA_TYPE = "PROXY_ACCOUNT_GLOBAL_COLLECT";

    private static final String NUM_TOT_PROXY_ACCOUNT_LOCKABLE = "NUM_TOTAL_PROXY_ACCOUNT_LOCKABLE";
    private static final String NUM_TOT_PROXY_ACCOUNT_LEGACY = "NUM_TOTAL_PROXY_ACCOUNT_LEGACY";
    private static final String NUM_TOT_PROXY_ACCOUNT_EN_LOCKABLE = "NUM_TOTAL_PROXY_ACCOUNT_ENABLED_LOCKABLE";
    private static final String NUM_TOT_PROXY_ACCOUNT_EN_LEGACY = "NUM_TOTAL_PROXY_ACCOUNT_ENABLED_LEGACY";
    private static final String NUM_TOT_PROXY_ACCOUNT_DIS_LOCKABLE = "NUM_TOTAL_PROXY_ACCOUNT_DISABLED_LOCKABLE";
    private static final String NUM_TOT_PROXY_ACCOUNT_DIS_LEGACY = "NUM_TOTAL_PROXY_ACCOUNT_DISABLED_LEGACY";
    private static final String NUM_TOT_PROXY_ACCOUNT_INACTIVE_LOCKABLE = "NUM_TOTAL_PROXY_ACCOUNT_INACTIVE_LOCKABLE_BY_30DAYS";
    private static final String NUM_TOT_PROXY_ACCOUNT_INACTIVE_LEGACY = "NUM_TOTAL_PROXY_ACCOUNT_INACTIVE_LEGACY_BY_30DAYS";

    private static final String MAX_NUM_TOT_PROXY_ACCOUNT_THRESHOLD = "MAX_NUM_TOT_PROXY_ACCOUNT_THRESHOLD";

    @Inject
    private SystemRecorder systemRecorder;

    private final Map<String, Object> eventData = new HashMap<>();

    private Integer numOfProxyAccount;
    private Integer numOfProxyAccountEnabled;
    private Integer numOfProxyAccountDisabled;
    private Integer numOfProxyAccountInactive;

    private Integer numOfProxyAccountLegacy;
    private Integer numOfProxyAccountEnabledLegacy;
    private Integer numOfProxyAccountDisabledLegacy;
    private Integer numOfProxyAccountInactiveLegacy;

    private Integer maxProxyAccountEntriesThreshold;

    public void setNumOfProxyAccount(Integer numOfProxyAccount) {
        this.numOfProxyAccount = numOfProxyAccount;
    }

    public void setNumOfProxyAccountEnabled(Integer numOfProxyAccountEnabled) {
        this.numOfProxyAccountEnabled = numOfProxyAccountEnabled;
    }

    public void setNumOfProxyAccountDisabled(Integer numOfProxyAccountDisabled) {
        this.numOfProxyAccountDisabled = numOfProxyAccountDisabled;
    }

    public void setNumOfProxyAccountInactive(Integer numOfProxyAccountInactive) {
        this.numOfProxyAccountInactive = numOfProxyAccountInactive;
    }

    public void setNumOfProxyAccountLegacy(Integer numOfProxyAccountLegacy) {
        this.numOfProxyAccountLegacy = numOfProxyAccountLegacy;
    }

    public void setNumOfProxyAccountEnabledLegacy(Integer numOfProxyAccountEnabledLegacy) {
        this.numOfProxyAccountEnabledLegacy = numOfProxyAccountEnabledLegacy;
    }

    public void setNumOfProxyAccountDisabledLegacy(Integer numOfProxyAccountDisabledLegacy) {
        this.numOfProxyAccountDisabledLegacy = numOfProxyAccountDisabledLegacy;
    }

    public void setNumOfProxyAccountInactiveLegacy(Integer numOfProxyAccountInactiveLegacy) {
        this.numOfProxyAccountInactiveLegacy = numOfProxyAccountInactiveLegacy;
    }

    public void setMaxProxyAccountEntriesThreshold(Integer maxProxyAccountEntriesThreshold) {
        this.maxProxyAccountEntriesThreshold = maxProxyAccountEntriesThreshold;
    }

    public Integer getNumOfProxyAccountLockable() {
        return (Integer)eventData.get(NUM_TOT_PROXY_ACCOUNT_LOCKABLE);
    }

    public Integer getNumOfProxyAccountLegacy() {
        return (Integer)eventData.get(NUM_TOT_PROXY_ACCOUNT_LEGACY);
    }

    public Integer getNumOfProxyAccountEnabledLockable() {
        return (Integer)eventData.get(NUM_TOT_PROXY_ACCOUNT_EN_LOCKABLE);
    }
    public Integer getNumOfProxyAccountEnabledLegacy() {
        return (Integer)eventData.get(NUM_TOT_PROXY_ACCOUNT_EN_LEGACY);
    }

    public Integer getNumOfProxyAccountDisabledLockable() {
        return (Integer)eventData.get(NUM_TOT_PROXY_ACCOUNT_DIS_LOCKABLE);
    }

    public Integer getNumOfProxyAccountDisabledLegacy() {
        return (Integer)eventData.get(NUM_TOT_PROXY_ACCOUNT_DIS_LEGACY);
    }

    public Integer getNumOfProxyAccountInactiveLockable() {
        return (Integer)eventData.get(NUM_TOT_PROXY_ACCOUNT_INACTIVE_LOCKABLE);
    }
    public Integer getNumOfProxyAccountInactiveLegacy() {
        return (Integer)eventData.get(NUM_TOT_PROXY_ACCOUNT_INACTIVE_LEGACY);
    }

    public Integer getMaxProxyAccountEntriesThreshold() {
        return maxProxyAccountEntriesThreshold;
    }

    public void sendProxyAccountGlbCntsEventData() {
        eventData.put(NUM_TOT_PROXY_ACCOUNT_LOCKABLE, numOfProxyAccount - numOfProxyAccountLegacy);
        eventData.put(NUM_TOT_PROXY_ACCOUNT_LEGACY, numOfProxyAccountLegacy);

        eventData.put(NUM_TOT_PROXY_ACCOUNT_EN_LOCKABLE, numOfProxyAccountEnabled - numOfProxyAccountEnabledLegacy);
        eventData.put(NUM_TOT_PROXY_ACCOUNT_EN_LEGACY, numOfProxyAccountEnabledLegacy);

        eventData.put(NUM_TOT_PROXY_ACCOUNT_DIS_LOCKABLE, numOfProxyAccountDisabled - numOfProxyAccountDisabledLegacy);
        eventData.put(NUM_TOT_PROXY_ACCOUNT_DIS_LEGACY, numOfProxyAccountDisabledLegacy);

        eventData.put(NUM_TOT_PROXY_ACCOUNT_INACTIVE_LOCKABLE, numOfProxyAccountInactive- numOfProxyAccountInactiveLegacy);
        eventData.put(NUM_TOT_PROXY_ACCOUNT_INACTIVE_LEGACY, numOfProxyAccountInactiveLegacy);

        eventData.put(MAX_NUM_TOT_PROXY_ACCOUNT_THRESHOLD, maxProxyAccountEntriesThreshold);

        final String eventType = String.format("%s.%s", IDMS_SYSTEM_RECORDER_TAG, IDMS_PROXY_GLB_CNT_EVT_DATA_TYPE);
        systemRecorder.recordEventData(eventType, eventData);
    }
}
