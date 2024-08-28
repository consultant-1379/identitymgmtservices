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

import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementService;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountGetData;
import com.ericsson.oss.itpf.security.identitymgmtservices.ejb.IdentityManagementListener;
import com.ericsson.oss.itpf.security.identitymgmtservices.ejb.cluster.IdentityManagementClusterMessageListener;
import com.ericsson.oss.itpf.security.identitymgmtservices.ejb.timers.*;

import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.Calendar;

import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.timers.IdentityManagementTimerConstants.ERR_HANDLING_TIMER;

@Singleton
@Startup
public class ProxyAccountGlobalCountersManager implements IdentityManagementTimerCallback {
    private static final Logger logger = LoggerFactory.getLogger(ProxyAccountGlobalCountersManager.class);
    private static final String IDMS_GLB_CNTS_EXC_TAG = "IDMS_GLB_CNTS_PROXYACCOUNT";

    private static final String GLB_CNTS_EXCEPTION = "Exception in Global Counter Proxy Account Manager";
    private static final String IDMS_GLB_CNTS_PROXYACCOUNT_NAME = "GlobalCountersProxyAccount";
    private static final long IDMS_GLB_CNTS_PROXYACCOUNT_INTERVAL = (15*60*1000L); //15 min interval
    public static final Long IDMS_GLB_CNT_INACTIVE_PERIOD = (30 * 24 * 60 * 60 * 1000L); //30 days

    @Inject
    private IdentityManagementTimerManager idmsTimerManager;

    @EJB
    private IdentityManagementService identityManagementService;

    @Inject
    private IdentityManagementClusterMessageListener idmsClusterMessageListener;

    @Inject
    private ProxyAccountGlobalCountersEvent proxyAccountGlobalCountersEvent;

    @Inject
    private IdentityManagementListener identityManagementListener;

    @Inject
    private ProxyAccountMonitoredData proxyAccountMonData;

    @PostConstruct
    private void initTimer() {
        logger.info("{}: {}", IDMS_GLB_CNTS_EXC_TAG, "initialize timer");
        try {
            IdentityManagementTimerConfig idmsTimerConfig = new IdentityManagementTimerConfig(
                    IDMS_GLB_CNTS_PROXYACCOUNT_NAME, IdentityManagementTimerType.INTERVAL,
                    idmsTimerManager.calcInitialDuration(IDMS_GLB_CNTS_PROXYACCOUNT_INTERVAL),
                    IDMS_GLB_CNTS_PROXYACCOUNT_INTERVAL, this);
            idmsTimerManager.addTimer(idmsTimerConfig);
        } catch (Exception e) {
            final String errMsg = String.format("%s: %s, excp_msg=[%s],excp_cause=[%s]", IDMS_GLB_CNTS_EXC_TAG,
                    ERR_HANDLING_TIMER, e.getMessage(), e.getCause());
            logger.error(errMsg, e.getClass().getSimpleName());
        }
    }

    @Override
    public void onTimerEvent() {
        logger.debug("{}: callback invoked=[{}]",IDMS_GLB_CNTS_EXC_TAG, this.getClass().getSimpleName());
        try {
            if (idmsClusterMessageListener.isMaster()) {
                getProxyAccountGlbCounters();
            } else {
                logger.debug("{}: I'm slave and not updating proxy account global counters", IDMS_GLB_CNTS_EXC_TAG);
            }
        } catch (Exception e) {
            String errMsg = String.format("%s:%s %s", IDMS_GLB_CNTS_EXC_TAG, GLB_CNTS_EXCEPTION, e.getMessage());
            logger.error(errMsg, e.getClass().getSimpleName());
            throw new IdentityManagementTimerException(errMsg);
        }
    }

    private void getProxyAccountGlbCounters() {
        // Get number of Proxy Account in enabled state for each subtree
        ProxyAgentAccountAdminStatus proxyAgentAccountAdminStatus = ProxyAgentAccountAdminStatus.ENABLED;
        ProxyAgentAccountGetData proxyAgentAccountGetDataByAdminStatus = identityManagementService.getProxyAgentAccountByAdminStatus(
                proxyAgentAccountAdminStatus, false,true);

        final Integer numOfProxyAccountEnabled = proxyAgentAccountGetDataByAdminStatus.getProxyAgentAccountCounters().getNumOfRequestedProxyAccount();
        final Integer numOfProxyAccountEnabledLegacy = proxyAgentAccountGetDataByAdminStatus.getProxyAgentAccountCounters().getNumOfRequestedProxyAccountLegacy();
        proxyAccountGlobalCountersEvent.setNumOfProxyAccountEnabled(numOfProxyAccountEnabled);
        proxyAccountGlobalCountersEvent.setNumOfProxyAccountEnabledLegacy(numOfProxyAccountEnabledLegacy);

        // Get number of Proxy Account in disabled state for each subtree
        proxyAgentAccountAdminStatus = ProxyAgentAccountAdminStatus.DISABLED;
        proxyAgentAccountGetDataByAdminStatus = identityManagementService.getProxyAgentAccountByAdminStatus(
                proxyAgentAccountAdminStatus, false,true);
        final Integer numOfProxyAccountDisabled = proxyAgentAccountGetDataByAdminStatus.getProxyAgentAccountCounters().getNumOfRequestedProxyAccount();
        final Integer numOfProxyAccountDisabledLegacy = proxyAgentAccountGetDataByAdminStatus.getProxyAgentAccountCounters().getNumOfRequestedProxyAccountLegacy();
        proxyAccountGlobalCountersEvent.setNumOfProxyAccountDisabled(numOfProxyAccountDisabled);
        proxyAccountGlobalCountersEvent.setNumOfProxyAccountDisabledLegacy(numOfProxyAccountDisabledLegacy);

        // Get number of total Proxy Account for each subtree
        final Integer numOfProxyAccount = proxyAgentAccountGetDataByAdminStatus.getProxyAgentAccountCounters().getNumOfProxyAccount();
        final Integer numOfProxyAccountLegacy = proxyAgentAccountGetDataByAdminStatus.getProxyAgentAccountCounters().getNumOfProxyAccountLegacy();
        proxyAccountGlobalCountersEvent.setNumOfProxyAccount(numOfProxyAccount);
        proxyAccountGlobalCountersEvent.setNumOfProxyAccountLegacy(numOfProxyAccountLegacy);

        // Get number of Proxy Account inactive for GLB_CNTS_PROXYACCOUNT_INACTIVE_PERIOD
        final Calendar now = Calendar.getInstance();
        final long nowMillis = now.getTimeInMillis();
        final Long inactivityPeriod = nowMillis - IDMS_GLB_CNT_INACTIVE_PERIOD;

        ProxyAgentAccountGetData proxyAgentAccountGetDataByInactivityPeriod = identityManagementService.getProxyAgentAccountByInactivityPeriod(
                inactivityPeriod, false,true);
        final Integer numOfProxyAccountInactive = proxyAgentAccountGetDataByInactivityPeriod.getProxyAgentAccountCounters().getNumOfRequestedProxyAccount();
        final Integer numOfProxyAccountInactiveLegacy = proxyAgentAccountGetDataByInactivityPeriod.getProxyAgentAccountCounters().getNumOfRequestedProxyAccountLegacy();
        proxyAccountGlobalCountersEvent.setNumOfProxyAccountInactive(numOfProxyAccountInactive);
        proxyAccountGlobalCountersEvent.setNumOfProxyAccountInactiveLegacy(numOfProxyAccountInactiveLegacy);

        // Get Threshold for Max Proxy Account Entries
        final Integer maxProxyAccountEntriesThreshold = identityManagementListener.getProxyAccountExcessiveEntriesThreshold();
        proxyAccountGlobalCountersEvent.setMaxProxyAccountEntriesThreshold(maxProxyAccountEntriesThreshold);

        // Update mBean with numTotalProxyAccount and maxThreshold
        proxyAccountMonData.setNumberOfTotalProxyAccount(numOfProxyAccount);
        proxyAccountMonData.setMaxNumberOfTotalProxyAccountThreshold(maxProxyAccountEntriesThreshold);

        // send record data
        proxyAccountGlobalCountersEvent.sendProxyAccountGlbCntsEventData();
    }
}
