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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.timers.IdentityManagementTimerConstants.*;

@Singleton
public class IdentityManagementTimerManager {
    private static final Logger logger = LoggerFactory.getLogger(IdentityManagementTimerManager.class);
    private static final String IDMS_TIMER_MANGER_TAG = "IDMS_TIMER_MANAGER";

    @Resource
    private TimerService timerService;

    private final Map<String, IdentityManagementTimerConfig> idmsTimerConfigMap = new HashMap<>();
    private final Map<String, Timer> idmsTimers = new HashMap<>();

    @Timeout
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void onExpired(final Timer timer) {
        try {
            if(timer == null) {
                final String errMsg = String.format("%s: %s", IDMS_TIMER_MANGER_TAG, NULL_TIMER_CREATED);
                logger.error(errMsg);
                throw new IdentityManagementTimerException(errMsg);
            }

            final String name = (String) timer.getInfo();
            logger.debug("IDMS_TIMER_MANAGER_TAG: info=[{}] ", timer.getInfo());

            final IdentityManagementTimerCallback idmsTimerCallback = idmsTimerConfigMap.get(name).getHandle();
            final IdentityManagementTimerType idmsTimerType = idmsTimerConfigMap.get(name).getType();

            switch (idmsTimerType) {
                case SINGLE_ACTION:
                    idmsTimers.put(name, null);
                    idmsTimerCallback.onTimerEvent();
                    addTimer(idmsTimerConfigMap.get(name));
                    break;
                default:
                case INTERVAL:
                    idmsTimerCallback.onTimerEvent();
                    break;
            }
        } catch (Exception e) {
            final String errMsg = String.format("%s: %s, excp_msg=[%s],excp_cause=[%s]", IDMS_TIMER_MANGER_TAG,
                    ERR_HANDLING_TIMER, e.getMessage(), e.getCause());
            logger.error(errMsg, e.getClass().getSimpleName());
        }
    }

    private Boolean validateConfig (IdentityManagementTimerConfig identityManagementTimerConfig) {
        return (identityManagementTimerConfig != null) &&
                identityManagementTimerConfig.getName() != null &&
                !identityManagementTimerConfig.getName().isEmpty() &&
                identityManagementTimerConfig.getHandle() != null;
    }

    public void addTimer(IdentityManagementTimerConfig identityManagementTimerConfig) {

        if (!validateConfig(identityManagementTimerConfig)) {
            final String errMsg = String.format("%s: %s", IDMS_TIMER_MANGER_TAG, WRONG_TIMER_CFG);
            logger.error(errMsg);
            throw new IdentityManagementTimerException(errMsg);
        }
        if (this.timerService == null) {
            final String errMsg = String.format("%s: %s", IDMS_TIMER_MANGER_TAG, TIMER_SERVICE_UNAV);
            logger.error(errMsg);
            throw new IdentityManagementTimerException(errMsg);
        }

        final String timerName = identityManagementTimerConfig.getName();
        if (idmsTimers.get(timerName) != null) {
            final String errMsg = String.format("%s: %s", IDMS_TIMER_MANGER_TAG, TIMER_ALREADY_INIT);
            logger.error(errMsg);
            throw new IdentityManagementTimerException(errMsg);
        }

        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        timerConfig.setInfo(timerName);
        final Timer idmsTimerCreated = createTimer(identityManagementTimerConfig, timerConfig);

        idmsTimers.put(timerName, idmsTimerCreated);
        idmsTimerConfigMap.put(timerName, identityManagementTimerConfig);

    }

    private Timer createTimer (final IdentityManagementTimerConfig identityManagementTimerConfig,
                              final TimerConfig timerConfig) {
        Timer idmsTimer;
        IdentityManagementTimerType idmsTimerType = identityManagementTimerConfig.getType();
        long duration = identityManagementTimerConfig.getDuration();
        long initialDuration = identityManagementTimerConfig.getInitialDuration();
        switch (idmsTimerType) {
            case SINGLE_ACTION:
                idmsTimer = timerService.createSingleActionTimer(duration, timerConfig);
                logger.debug("IDMS_TIMER_MANAGER_TAG: Created Single action timer Name=[{}], Duration=[{}]",
                        identityManagementTimerConfig.getName(), identityManagementTimerConfig.getDuration());
                break;
            default:
            case INTERVAL:
                idmsTimer = timerService.createIntervalTimer(initialDuration, duration, timerConfig);
                logger.debug("IDMS_TIMER_MANAGER_TAG: Created Interval timer Name=[{}], Initial Duration=[{}], Duration=[{}]",
                        identityManagementTimerConfig.getName(), identityManagementTimerConfig.getInitialDuration(),
                        identityManagementTimerConfig.getDuration());
                break;
        }
        return idmsTimer;
    }

    public long calcInitialDuration(final long interval ) {
        long ms = new Date().getTime();
        long stepMs = interval;
        long initialDuration = ((ms / stepMs) * stepMs + stepMs) - ms;
        logger.info("{}: initial duration=[{}], msNow=[{}]",IDMS_TIMER_MANGER_TAG, initialDuration, ms);
        return  initialDuration;
    }
}