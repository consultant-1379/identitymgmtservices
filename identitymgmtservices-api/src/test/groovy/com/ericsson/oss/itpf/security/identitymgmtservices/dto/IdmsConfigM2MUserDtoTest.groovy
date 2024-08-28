package com.ericsson.oss.itpf.security.identitymgmtservices.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class IdmsConfigM2MUserDtoTest extends CdiSpecification {

    def username = "fooUserName"
    def groupName = "mm-smrsusers"
    def homeDir = "/home/foousername"
    def validDays = 60

    def "no-args constructor" () {
        given:
        when:
        IdmsConfigM2MUserDto idmsConfigM2MUserDto = new IdmsConfigM2MUserDto()
        then:
        idmsConfigM2MUserDto !=null
        and:
        idmsConfigM2MUserDto.getUserName() == null &&
                idmsConfigM2MUserDto.getGroupName() == null &&
                idmsConfigM2MUserDto.getHomeDir() == null &&
                idmsConfigM2MUserDto.getValidDays() == 0
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsConfigM2MUserDto idmsConfigM2MUserDto = new IdmsConfigM2MUserDto(username, groupName, homeDir, validDays)
        then:
        idmsConfigM2MUserDto.getUserName() == username &&
                idmsConfigM2MUserDto.getGroupName() == groupName &&
                idmsConfigM2MUserDto.getHomeDir() == homeDir &&
                idmsConfigM2MUserDto.getValidDays() == validDays

    }

    def "set parameters" () {
        given:
        IdmsConfigM2MUserDto idmsConfigM2MUserDto = new IdmsConfigM2MUserDto()
        when:
        idmsConfigM2MUserDto.setUserName(username)
        idmsConfigM2MUserDto.setGroupName(groupName)
        idmsConfigM2MUserDto.setHomeDir(homeDir)
        idmsConfigM2MUserDto.setValidDays(validDays)
        then:
        idmsConfigM2MUserDto.getUserName() == username &&
                idmsConfigM2MUserDto.getGroupName() == groupName &&
                idmsConfigM2MUserDto.getHomeDir() == homeDir &&
                idmsConfigM2MUserDto.getValidDays() == validDays
    }
}
