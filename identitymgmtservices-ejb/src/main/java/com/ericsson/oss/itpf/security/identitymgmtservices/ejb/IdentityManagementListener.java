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

package com.ericsson.oss.itpf.security.identitymgmtservices.ejb;

import com.ericsson.oss.itpf.sdk.config.annotation.ConfigurationChangeNotification;
import com.ericsson.oss.itpf.sdk.config.annotation.Configured;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@Singleton
@Startup
public class IdentityManagementListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityManagementListener.class);

    @Inject
    @Configured(propertyName = "proxyAccountPwdLen")
    private int proxyAccountPwdLen;

    @Inject
    @Configured(propertyName = "proxyAccountExcessiveEntriesThreshold")
    private int proxyAccountExcessiveEntriesThreshold;

    @Configured(propertyName = "switchOnLegacySubtree")
    private Boolean switchOnLegacySubtree;

    @PostConstruct
    public void init() {
        LOGGER.info("Post Construct IdentityManagementListener called");
    }

    public int getPasswordLength() {
        return proxyAccountPwdLen;
    }

    public void listenForPasswordLengthChange(
            @Observes @ConfigurationChangeNotification(propertyName = "proxyAccountPwdLen") final int newProxyAccountPwdLen) {
        LOGGER.info("new passwordLength for proxy account = {}", newProxyAccountPwdLen);
        proxyAccountPwdLen = newProxyAccountPwdLen;
    }

    public int getProxyAccountExcessiveEntriesThreshold() {
        return proxyAccountExcessiveEntriesThreshold;
    }

    public void listenForProxyAccountExcessiveEntries(
            @Observes @ConfigurationChangeNotification(propertyName = "proxyAccountExcessiveEntriesThreshold")
            final int newProxyAccountExcessiveEntriesThreshold) {
        LOGGER.info("new threshold for excessive proxy account entries = {}", newProxyAccountExcessiveEntriesThreshold);
        proxyAccountExcessiveEntriesThreshold = newProxyAccountExcessiveEntriesThreshold;
    }

    public Boolean getProxyAccountRdnSubTree() {
        return switchOnLegacySubtree;
    }

    public void listenForProxyAccountRdnSubTree(
            @Observes @ConfigurationChangeNotification(propertyName = "switchOnLegacySubtree") final Boolean newSwitchOnLegacySubtree) {
        LOGGER.info("new rdn subtree for proxy account = {}", newSwitchOnLegacySubtree);
        switchOnLegacySubtree = newSwitchOnLegacySubtree;
    }
}
