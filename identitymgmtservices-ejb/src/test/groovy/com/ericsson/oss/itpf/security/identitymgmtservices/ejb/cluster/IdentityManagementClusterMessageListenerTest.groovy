package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.cluster

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.sdk.cluster.MembershipChangeEvent
import com.ericsson.oss.itpf.security.cryptography.CryptographyService

class IdentityManagementClusterMessageListenerTest extends CdiSpecification {

    @ObjectUnderTest
    IdentityManagementClusterMessageListener idmsClusterMessageListener

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup (){}

    @ImplementationInstance
    MembershipChangeEvent membershipChangeEvent = [
            isMaster: {
                return true
            },
            getCurrentNumberOfMembers: {
                return 2;
            },
            getAllClusterMembers: {
                MembershipChangeEvent.ClusterMemberInfo clusterMemberInfo = new MembershipChangeEvent.ClusterMemberInfo (
                      "svc-3-secserv", "identitymgmtservices", "1.59.2-SNAPSHOT")
                List<MembershipChangeEvent.ClusterMemberInfo> memberInfoList = new ArrayList<>();
                memberInfoList.add(clusterMemberInfo);
                return memberInfoList
            }
    ] as MembershipChangeEvent

    def 'listen for membership change'(){
        given:
        when:
        idmsClusterMessageListener.listenForMembershipChange(membershipChangeEvent)
        def isMaster = idmsClusterMessageListener.master
        then:
        isMaster
    }
}
