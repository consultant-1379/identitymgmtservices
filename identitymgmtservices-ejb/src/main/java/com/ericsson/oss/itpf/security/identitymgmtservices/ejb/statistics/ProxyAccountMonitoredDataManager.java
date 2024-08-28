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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.SecurityManagerConstants.LEGACY_SUBTREE_ID;
import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.SecurityManagerConstants.LOCKABLE_SUBTREE_ID;
import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.statistics.ProxyAccountMonitoredDataConstants.*;


@Singleton
@Startup
public class ProxyAccountMonitoredDataManager {
    private static final Logger logger = LoggerFactory.getLogger(ProxyAccountMonitoredDataManager.class);
    private static final String IDMS_MON_DATA_TAG = "IDMS_MON_DATA_PROXYACCOUNT";

    private final Map<String , ProxyAccountMonitoredDataCommand> incMonitoredDataMap = new HashMap<>();

    @Inject
    ProxyAccountMonitoredData proxyAccountMonitoredData;

    @PostConstruct
    void init () {
        fillMonitoredDataCommand();
    }

    public void updateMonitoredDataCounters (final String userDN, final String command) {
        String subtreeType = INVALID_SUBTREE;
        if(userDN.contains(LOCKABLE_SUBTREE_ID)) {
            subtreeType = LOCKABLE_SUBTREE;
        } else if (userDN.contains(LEGACY_SUBTREE_ID)) {
            subtreeType = LEGACY_SUBTREE;
        }

        logger.debug("{}: subtreeType=[{}]", IDMS_MON_DATA_TAG, subtreeType);
        final String key = String.format("%s_%s",command, subtreeType);
        if(!incMonitoredDataMap.containsKey(key)) {
            logger.error("{}: key=[{}] not existent", IDMS_MON_DATA_TAG, key);
            return;
        }

        incMonitoredDataMap.get(key).updateMonitoredData();
    }

    void fillMonitoredDataCommand () {
        incMonitoredDataMap.put("CREATE_LOCKABLE", () -> proxyAccountMonitoredData.increaseNumberOfCreatedProxyAccountOnLockableSubtree());
        incMonitoredDataMap.put("DELETE_LOCKABLE", () -> proxyAccountMonitoredData.increaseNumberOfDeletedProxyAccountOnLockableSubtree());
        incMonitoredDataMap.put("ENABLED_LOCKABLE", () -> proxyAccountMonitoredData.increaseNumberOfEnabledProxyAccountOnLockableSubtree());
        incMonitoredDataMap.put("DISABLED_LOCKABLE", () -> proxyAccountMonitoredData.increaseNumberOfDisabledProxyAccountOnLockableSubtree());
        incMonitoredDataMap.put("CREATE_LEGACY", () -> proxyAccountMonitoredData.increaseNumberOfCreatedProxyAccountOnLegacySubtree());
        incMonitoredDataMap.put("DELETE_LEGACY", () -> proxyAccountMonitoredData.increaseNumberOfDeletedProxyAccountOnLegacySubtree());
        incMonitoredDataMap.put("ENABLED_LEGACY", () -> proxyAccountMonitoredData.increaseNumberOfEnabledProxyAccountOnLegacySubtree());
        incMonitoredDataMap.put("DISABLED_LEGACY", () -> proxyAccountMonitoredData.increaseNumberOfDisabledProxyAccountOnLegacySubtree());
    }
}
