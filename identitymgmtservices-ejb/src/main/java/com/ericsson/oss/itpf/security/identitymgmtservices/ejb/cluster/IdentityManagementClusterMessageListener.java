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
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.cluster;

import com.ericsson.oss.itpf.sdk.cluster.MembershipChangeEvent;
import com.ericsson.oss.itpf.sdk.cluster.annotation.ServiceCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class IdentityManagementClusterMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(IdentityManagementClusterMessageListener.class);

    private boolean master = false;

    /* observer method will be invoked by ServiceFramework every time
     * there are membership changes in the required service cluster
     */
    void listenForMembershipChange(@Observes @ServiceCluster("IdentityMgmtServices") final MembershipChangeEvent mce) {
        logger.info("Catch MemberShip Change [isMaster = {}]", mce.isMaster());

        setMaster(mce.isMaster());
        final int numberOfMembers = mce.getCurrentNumberOfMembers();
        logger.info("MemberShip: {}", numberOfMembers);

        for (final MembershipChangeEvent.ClusterMemberInfo cmi : mce.getAllClusterMembers()) {
            logger.info("NodeId: {} ServiceId: {} Version: {}", cmi.getNodeId(), cmi.getServiceId(), cmi.getVersion());
        }
    }

    public boolean isMaster() {
        return master;
    }

    private void setMaster(final boolean master) {
        this.master = master;
    }

}
