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
package com.ericsson.oss.itpf.security.identitymgmtservices.proxyaccount;

import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountCounters;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountDetails;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountGetData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus.valueOfProxyAgentAccoountAdminStatus;

public class ProxyAgentAccountGetDataManager {
    private static final Logger logger = LoggerFactory.getLogger(ProxyAgentAccountGetDataManager.class);
    private ProxyAgentAccountGetData proxyAgentAccountGetData;
    private ProxyAgentAccountCounterManager proxyAgentAccountCounterManager;
    private ProxyAgentAccountBuilder proxyAgentAccountBuilder;

    public ProxyAgentAccountGetData getProxyAgentAccountGetData() {
        return proxyAgentAccountGetData;
    }

    public ProxyAgentAccountBuilder getProxyAgentAccountBuilder() {
        return proxyAgentAccountBuilder;
    }

    public void initializeProxyAgentAccountGetData(Boolean isLegacy, Boolean isSummary, List<String> proxyAgentAccountSubtreeList) {

        /* build object to support the get operations based on input parameters */
        proxyAgentAccountBuilder = new ProxyAgentAccountBuilder(isLegacy, isSummary, proxyAgentAccountSubtreeList);

        List<String> proxyAccountSubtrees = proxyAgentAccountBuilder.getProxyAccountSubtreesActive();
        /* initialize proxy account counter manager */
        proxyAgentAccountCounterManager = new ProxyAgentAccountCounterManager();
        proxyAgentAccountCounterManager.initialize(proxyAccountSubtrees);

        proxyAgentAccountGetData = new ProxyAgentAccountGetData();
        ProxyAgentAccountCounters proxyAgentAccountCounters = new ProxyAgentAccountCounters();
        List<ProxyAgentAccountDetails> proxyAgentAccountDetailsList = new ArrayList<>();
        proxyAgentAccountGetData.setProxyAgentAccountCounters(proxyAgentAccountCounters);
        proxyAgentAccountGetData.setProxyAgentAccountDetailsList(proxyAgentAccountDetailsList);
    }

    public void updateProxyAgentAccountDetails(String userDn, String createTimeStamp,
                                               String lastLoginTime, String adminStatus) {
        ProxyAgentAccountDetails proxyAgentAccountDetails = new ProxyAgentAccountDetails();
        proxyAgentAccountDetails.setUserDn(userDn);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        if (createTimeStamp !=null) {
            try {
                Long dateInMsec = formatter.parse(createTimeStamp).getTime();
                proxyAgentAccountDetails.setCreateTimestamp(dateInMsec);
            } catch (ParseException e) {
                logger.error("updateProxyAgentAccountDetails createTimeStamp parser error");
                proxyAgentAccountDetails.setCreateTimestamp(null);
            }
        } else {
            proxyAgentAccountDetails.setCreateTimestamp(null);
        }

        if (lastLoginTime !=null) {
            try {
                Long dateInMsec = formatter.parse(lastLoginTime).getTime();
                proxyAgentAccountDetails.setLastLoginTime(dateInMsec);
            } catch (ParseException e) {
                logger.error("updateProxyAgentAccountDetails lastLoginTime parser error");
                proxyAgentAccountDetails.setCreateTimestamp(null);
            }
        } else {
            proxyAgentAccountDetails.setLastLoginTime(null);
        }

        if(adminStatus !=null) {
            String localAdminStatus = adminStatus.equals("true") ? "disabled" : "enabled";
            proxyAgentAccountDetails.setAdminStatus(valueOfProxyAgentAccoountAdminStatus(localAdminStatus));
        } else {
            proxyAgentAccountDetails.setAdminStatus(null);
        }
        proxyAgentAccountGetData.addToList(proxyAgentAccountDetails);
    }

    public void updateProxyAgentAccountRequestedCounters() {
        proxyAgentAccountCounterManager.updateProxyAccountRequestedCounters();
        proxyAgentAccountGetData.getProxyAgentAccountCounters().setNumOfRequestedProxyAccount(proxyAgentAccountCounterManager.getNumOfTotalRequestedProxyAccountEntries());
        proxyAgentAccountGetData.getProxyAgentAccountCounters().setNumOfRequestedProxyAccountLegacy(proxyAgentAccountCounterManager.getNumOfTotalRequestedProxyAccountLegacyEntries());
    }

    public void updateProxyAgentAccountCounters() {
        proxyAgentAccountCounterManager.updateProxyAccountCounters();
        proxyAgentAccountGetData.getProxyAgentAccountCounters().setNumOfProxyAccount(proxyAgentAccountCounterManager.getNumOfTotalProxyAccountEntries());
        proxyAgentAccountGetData.getProxyAgentAccountCounters().setNumOfProxyAccountLegacy(proxyAgentAccountCounterManager.getNumOfTotalProxyAccountLegacyEntries());
    }

    public void updateRequestedSubtreeEntries(final String proxyAccountSubtree, final Integer size) {
        proxyAgentAccountCounterManager.updateRequestedSubtreeEntries(proxyAccountSubtree, size);
    }

    public void updateTotalSubtreeEntries(final String proxyAccountSubtree, final Integer size) {
        proxyAgentAccountCounterManager.updateTotalSubtreeEntries(proxyAccountSubtree, size);
    }
}
