package com.ericsson.oss.itpf.security.identitymgmtservices.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class IdmsGeneralConfigM2MUserDtoTest extends CdiSpecification {

    def username = "fooUserName"
    def groupName = "mm-smrsusers"
    def homeDir = "/home/foousername"

    def "no-args constructor" () {
        given:
        when:
        IdmsGeneralConfigM2MUserDto idmsGeneralConfigM2MUserDto = new IdmsGeneralConfigM2MUserDto()
        then:
        idmsGeneralConfigM2MUserDto !=null
        and:
        idmsGeneralConfigM2MUserDto.getUserName() == null &&
                idmsGeneralConfigM2MUserDto.getGroupName() == null &&
                idmsGeneralConfigM2MUserDto.getHomeDir() == null
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsGeneralConfigM2MUserDto idmsGeneralConfigM2MUserDto = new IdmsGeneralConfigM2MUserDto(username, groupName, homeDir)
        then:
        idmsGeneralConfigM2MUserDto.getUserName() == username &&
                idmsGeneralConfigM2MUserDto.getGroupName() == groupName &&
                idmsGeneralConfigM2MUserDto.getHomeDir() == homeDir
    }

    def "set parameters" () {
        given:
        IdmsGeneralConfigM2MUserDto idmsGeneralConfigM2MUserDto = new IdmsGeneralConfigM2MUserDto()
        when:
        idmsGeneralConfigM2MUserDto.setUserName(username)
        idmsGeneralConfigM2MUserDto.setGroupName(groupName)
        idmsGeneralConfigM2MUserDto.setHomeDir(homeDir)
        then:
        idmsGeneralConfigM2MUserDto.getUserName() == username &&
                idmsGeneralConfigM2MUserDto.getGroupName() == groupName &&
                idmsGeneralConfigM2MUserDto.getHomeDir() == homeDir
    }
}
