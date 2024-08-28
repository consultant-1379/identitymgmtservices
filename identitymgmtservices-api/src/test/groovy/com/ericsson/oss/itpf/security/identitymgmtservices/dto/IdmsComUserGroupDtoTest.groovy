package com.ericsson.oss.itpf.security.identitymgmtservices.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class IdmsComUserGroupDtoTest extends CdiSpecification {

    def username = "fooUserName"
    def comUserGroup = "fooComUserGrp"

    def "no-args constructor" () {
        given:
        when:
        IdmsComUserGroupDto idmsComUserGroupDto = new IdmsComUserGroupDto()
        then:
        idmsComUserGroupDto !=null
        and:
        idmsComUserGroupDto.getUserName() == null &&
                idmsComUserGroupDto.getComUserGroup() == null
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsComUserGroupDto idmsComUserGroupDto = new IdmsComUserGroupDto(username, comUserGroup)
        then:
        idmsComUserGroupDto.getUserName() == username &&
                idmsComUserGroupDto.getComUserGroup() == comUserGroup
    }

    def "set parameters" () {
        given:
        IdmsComUserGroupDto idmsComUserGroupDto = new IdmsComUserGroupDto()
        when:
        idmsComUserGroupDto.setUserName(username)
        idmsComUserGroupDto.setComUserGroup(comUserGroup)
        then:
        idmsComUserGroupDto.getUserName() == username &&
                idmsComUserGroupDto.getComUserGroup() == comUserGroup
    }

}
