package com.ericsson.oss.itpf.security.identitymgmtservices

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import org.forgerock.opendj.ldap.ByteString
import org.forgerock.opendj.ldap.Connection
import org.forgerock.opendj.ldap.ErrorResultException
import org.forgerock.opendj.ldap.ErrorResultIOException
import org.forgerock.opendj.ldap.ResultCode
import org.forgerock.opendj.ldap.controls.SimplePagedResultsControl
import org.forgerock.opendj.ldap.requests.SearchRequest
import org.forgerock.opendj.ldap.responses.Result
import org.forgerock.opendj.ldap.responses.SearchResultEntry
import org.forgerock.opendj.ldif.ConnectionEntryReader

import javax.naming.directory.SearchControls

class DSCommunicatorFcTest extends CdiSpecification {

    @ObjectUnderTest
    DSCommunicator dsCommunicator

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {
    }

    def setup() {}
    @ImplementationInstance
    SearchResultEntry searchResultEntry = [
            getName : {
                return  null
            }
    ] as SearchResultEntry

    @ImplementationInstance
    Result resultError = [
            isSuccess : {
                return  true
            },
            getControl : { var1, var2 ->
                return SimplePagedResultsControl.newControl(true, 100, ByteString.empty())
            },
            getResultCode : {
                return ResultCode.INVALID_CREDENTIALS
            },
            getCause : {
                return new Throwable("test")
            },
            getDiagnosticMessage : {
                return "TEST"
            }

    ] as Result

    @ImplementationInstance
    Result resultNoSuchEntry = [
            isSuccess : {
                return  true
            },
            getControl : { var1, var2 ->
                return SimplePagedResultsControl.newControl(true, 100, ByteString.empty())
            },
            getResultCode : {
                return ResultCode.NO_SUCH_OBJECT
            },
            getCause : {
                return new Throwable("test")
            },
            getDiagnosticMessage : {
                return "TEST"
            }

    ] as Result

    @ImplementationInstance
    Result resultConnectionError = [
            isSuccess : {
                return  true
            },
            getControl : { var1, var2 ->
                return SimplePagedResultsControl.newControl(true, 100, ByteString.empty())
            },
            getResultCode : {
                return ResultCode.CLIENT_SIDE_CONNECT_ERROR
            },
            getCause : {
                return new Throwable("test")
            },
            getDiagnosticMessage : {
                return "TEST"
            }

    ] as Result

    @ImplementationInstance
    SearchRequest searchRequest = [
            getAttributes : {
            }

    ] as SearchRequest

    @ImplementationInstance
    Connection connectionMock = [
            isValid : {
                return true
            },
            isClosed : {
                return false
            },
            search : {
                dnPostfix, searchScope, filter, attributes ->
                    ConnectionEntryReaderInternalMock connectionEntryReader = new ConnectionEntryReaderInternalMock(connectionMock, searchRequest)
                    return connectionEntryReader
            },
            searchAsync : {
                searchRequest, intermediateResponseHandler, searchResultHandler ->
                    return null
            },
            close : {}
    ] as Connection

    @ImplementationInstance
    Connection connectionMock2 = [
            isValid : {
                return true
            },
            isClosed : {
                return false
            },
            search : {
                dnPostfix, searchScope, filter, attributes ->
                    ConnectionEntryReaderMock2 connectionEntryReader = new ConnectionEntryReaderMock2(connectionMock2, searchRequest)
                    return connectionEntryReader
            },
            searchAsync : {
                searchRequest, intermediateResponseHandler, searchResultHandler ->
                    return null
            },
            close : {}
    ] as Connection

    @ImplementationInstance
    Connection connectionMock3 = [
            isValid : {
                return true
            },
            isClosed : {
                return false
            },
            search : {
                dnPostfix, searchScope, filter, attributes ->
                    ConnectionEntryReaderMock3 connectionEntryReader = new ConnectionEntryReaderMock3(connectionMock3, searchRequest)
                    return connectionEntryReader
            },
            searchAsync : {
                searchRequest, intermediateResponseHandler, searchResultHandler ->
                    return null
            },
            close : {}
    ] as Connection

    @ImplementationInstance
    Connection connectionMock4   = [
            isValid : {
                return true
            },
            isClosed : {
                return false
            },
            search : {
                dnPostfix, searchScope, filter, attributes ->
                    ConnectionEntryReaderMock4 connectionEntryReader = new ConnectionEntryReaderMock4(connectionMock4, searchRequest)
                    return connectionEntryReader
            },
            searchAsync : {
                searchRequest, intermediateResponseHandler, searchResultHandler ->
                    return null
            },
            close : {}
    ] as Connection

    @ImplementationInstance
    Connection connectionMock5   = [
            isValid : {
                return true
            },
            isClosed : {
                return false
            },
            search : {
                dnPostfix, searchScope, filter, attributes ->
                    ConnectionEntryReaderMock5 connectionEntryReader = new ConnectionEntryReaderMock5(connectionMock5, searchRequest)
                    return connectionEntryReader
            },
            searchAsync : {
                searchRequest, intermediateResponseHandler, searchResultHandler ->
                    return null
            },
            close : {}
    ] as Connection

    @ImplementationInstance
    Connection connectionMock6   = [
            isValid : {
                return true
            },
            isClosed : {
                return false
            },
            search : {
                dnPostfix, searchScope, filter, attributes ->
                    ConnectionEntryReaderMock6 connectionEntryReader = new ConnectionEntryReaderMock6(connectionMock6, searchRequest)
                    return connectionEntryReader
            },
            searchAsync : {
                searchRequest, intermediateResponseHandler, searchResultHandler ->
                    return null
            },
            close : {}
    ] as Connection

    def 'verify queryGenericMultipleAttributes with connection not null  ' () {
        given:
        final ArrayList<String> attributesList = new ArrayList<>()
        final String filterToApply = "(objectclass=*)"
        def proxyAccountSubtreeDnTest = "ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
        attributesList.add("+")

        dsCommunicator.connection = connectionMock
        when:
        List<SearchResultEntry> listOfEntries = dsCommunicator.queryGenericMultipleAttributes(proxyAccountSubtreeDnTest,SearchControls.ONELEVEL_SCOPE,attributesList,filterToApply)
        then:
        listOfEntries.size() == 1
    }

    def 'verify queryGenericMultipleAttributes raise DSCommunicatorException ' () {
        given:
        final ArrayList<String> attributesList = new ArrayList<>()
        final String filterToApply = "(objectclass=*)"
        def proxyAccountSubtreeDnTest = "ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
        attributesList.add("+")

        dsCommunicator.connection = connectionMock2
        when:
        dsCommunicator.queryGenericMultipleAttributes(proxyAccountSubtreeDnTest,SearchControls.ONELEVEL_SCOPE,attributesList,filterToApply)
        then:
        thrown DSCommunicatorException
    }

    def 'verify queryGenericMultipleAttributes raise ErrorResultIOException generic ' () {
        given:
        final ArrayList<String> attributesList = new ArrayList<>()
        final String filterToApply = "(objectclass=*)"
        def proxyAccountSubtreeDnTest = "ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
        attributesList.add("+")

        dsCommunicator.connection = connectionMock3
        when:
        dsCommunicator.queryGenericMultipleAttributes(proxyAccountSubtreeDnTest,SearchControls.ONELEVEL_SCOPE,attributesList,filterToApply)
        then:
        thrown DSCommunicatorException
    }

    def 'verify queryGenericMultipleAttributes raise ErrorResultIOException --> EntryNotFoundException ' () {
        given:
        final ArrayList<String> attributesList = new ArrayList<>()
        final String filterToApply = "(objectclass=*)"
        def proxyAccountSubtreeDnTest = "ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
        attributesList.add("+")

        dsCommunicator.connection = connectionMock4
        when:
        dsCommunicator.queryGenericMultipleAttributes(proxyAccountSubtreeDnTest,SearchControls.ONELEVEL_SCOPE,attributesList,filterToApply)
        then:
        thrown DSCommunicatorException
    }

    def 'verify queryGenericMultipleAttributes raise ErrorResultIOException --> ConnectionException ' () {
        given:
        final ArrayList<String> attributesList = new ArrayList<>()
        final String filterToApply = "(objectclass=*)"
        def proxyAccountSubtreeDnTest = "ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
        attributesList.add("+")

        dsCommunicator.connection = connectionMock5
        when:
        dsCommunicator.queryGenericMultipleAttributes(proxyAccountSubtreeDnTest,SearchControls.ONELEVEL_SCOPE,attributesList,filterToApply)
        then:
        thrown DSCommunicatorException
    }

    def 'verify queryGenericMultipleAttributes raise Exception' () {
        given:
        final ArrayList<String> attributesList = new ArrayList<>()
        final String filterToApply = "(objectclass=*)"
        def proxyAccountSubtreeDnTest = "ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
        attributesList.add("+")

        dsCommunicator.connection = connectionMock6
        when:
        dsCommunicator.queryGenericMultipleAttributes(proxyAccountSubtreeDnTest,SearchControls.ONELEVEL_SCOPE,attributesList,filterToApply)
        then:
        thrown DSCommunicatorException
    }

    class ConnectionEntryReaderInternalMock extends ConnectionEntryReaderMock1 {
        ConnectionEntryReaderInternalMock(Connection connection, SearchRequest searchRequest) {
            super(connection, searchRequest)
        }
    }

    class ConnectionEntryReaderMock1 extends ConnectionEntryReader {
        private Boolean hasNext = true

        ConnectionEntryReaderMock1(Connection connection, SearchRequest searchRequest) {
            super(connection, searchRequest)
        }

        boolean hasNext() {
            if (hasNext) {
                hasNext = false
                return true
            } else {
                hasNext = true
                return false
            }
        }

        boolean isReference() {
            return false
        }

        SearchResultEntry readEntry() {
            return searchResultEntry
        }
    }

    class ConnectionEntryReaderMock2 extends ConnectionEntryReader {
        ConnectionEntryReaderMock2(Connection connection, SearchRequest searchRequest) {
            super(connection, searchRequest)
        }

        boolean hasNext() {
            throw new DSCommunicatorException("Failed to get datastore for queryGenericMultipleAttributes operation",
                    IdmConstants.LDAP_ERROR_CONNECTION_FAILURE)
        }

        boolean isReference() {
            return false
        }

        SearchResultEntry readEntry() {
            return searchResultEntry
        }
    }

    class ConnectionEntryReaderMock3 extends ConnectionEntryReader {
        ConnectionEntryReaderMock3(Connection connection, SearchRequest searchRequest) {
            super(connection, searchRequest)
        }

        boolean hasNext() {
            ErrorResultException errorResultException = ErrorResultException.newErrorResult(resultError)
            ErrorResultIOException errorResultIOException = new ErrorResultIOException(errorResultException)
            throw errorResultIOException
        }

        boolean isReference() {
            return false
        }

        SearchResultEntry readEntry() {
            return searchResultEntry
        }
    }

    class ConnectionEntryReaderMock4 extends ConnectionEntryReader {
        ConnectionEntryReaderMock4(Connection connection, SearchRequest searchRequest) {
            super(connection, searchRequest)
        }

        boolean hasNext() {
            ErrorResultException errorResultException = ErrorResultException.newErrorResult(resultNoSuchEntry)
            ErrorResultIOException errorResultIOException = new ErrorResultIOException(errorResultException)
            throw errorResultIOException
        }

        boolean isReference() {
            return false
        }

        SearchResultEntry readEntry() {
            return searchResultEntry
        }
    }

    class ConnectionEntryReaderMock5 extends ConnectionEntryReader {
        ConnectionEntryReaderMock5(Connection connection, SearchRequest searchRequest) {
            super(connection, searchRequest)
        }

        boolean hasNext() {
            ErrorResultException errorResultException = ErrorResultException.newErrorResult(resultConnectionError)
            ErrorResultIOException errorResultIOException = new ErrorResultIOException(errorResultException)
            throw errorResultIOException
        }

        boolean isReference() {
            return false
        }

        SearchResultEntry readEntry() {
            return searchResultEntry
        }
    }

    class ConnectionEntryReaderMock6 extends ConnectionEntryReader {
        ConnectionEntryReaderMock6(Connection connection, SearchRequest searchRequest) {
            super(connection, searchRequest)
        }

        boolean hasNext() {
            String errorResultMsg = "error Msg Generic exception"
            Exception exception = new Exception(errorResultMsg)
            throw exception
        }

        boolean isReference() {
            return false
        }

        SearchResultEntry readEntry() {
            return searchResultEntry
        }
    }
}
