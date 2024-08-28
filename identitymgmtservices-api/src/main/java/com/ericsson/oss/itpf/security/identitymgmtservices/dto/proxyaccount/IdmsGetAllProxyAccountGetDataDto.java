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
package com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IdmsGetAllProxyAccountGetDataDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer numOfGetDataProxyAccount;
    private Integer numOfGetDataRequestedProxyAccount;
    private Integer numOfGetDataProxyAccountLegacy;
    private Integer numOfGetDataRequestedProxyAccountLegacy;

    private List<IdmsGetAllProxyAccountDto> idmsGetAllProxyAccountDtos;

    public IdmsGetAllProxyAccountGetDataDto() {
        // Do nothing, set and get method are used instead
    }

    public Integer getNumOfGetDataProxyAccount() {
        return numOfGetDataProxyAccount;
    }
    public void setNumOfGetDataProxyAccount(Integer numOfGetDataProxyAccount) {
        this.numOfGetDataProxyAccount = numOfGetDataProxyAccount;
    }

    public Integer getNumOfGetDataRequestedProxyAccount() {
        return numOfGetDataRequestedProxyAccount;
    }
    public void setNumOfGetDataRequestedProxyAccount(Integer numOfGetDataRequestedProxyAccount) {
        this.numOfGetDataRequestedProxyAccount = numOfGetDataRequestedProxyAccount;
    }

    public Integer getNumOfGetDataProxyAccountLegacy() {
        return numOfGetDataProxyAccountLegacy;
    }
    public void setNumOfGetDataProxyAccountLegacy(Integer numOfGetDataProxyAccountLegacy) {
        this.numOfGetDataProxyAccountLegacy = numOfGetDataProxyAccountLegacy;
    }

    public Integer getNumOfGetDataRequestedProxyAccountLegacy() {
        return numOfGetDataRequestedProxyAccountLegacy;
    }
    public void setNumOfGetDataRequestedProxyAccountLegacy(Integer numOfGetDataRequestedProxyAccountLegacy) {
        this.numOfGetDataRequestedProxyAccountLegacy = numOfGetDataRequestedProxyAccountLegacy;
    }

    public List<IdmsGetAllProxyAccountDto> getIdmsGetAllProxyAccountDtos() {
        if (idmsGetAllProxyAccountDtos == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(idmsGetAllProxyAccountDtos);
    }

    public void setIdmsGetAllProxyAccountDtos(List<IdmsGetAllProxyAccountDto> idmsGetAllProxyAccountDtos) {
        if (idmsGetAllProxyAccountDtos == null) {
            this.idmsGetAllProxyAccountDtos = null;
        } else {
            this.idmsGetAllProxyAccountDtos = new ArrayList<>(idmsGetAllProxyAccountDtos);
        }
    }
}
