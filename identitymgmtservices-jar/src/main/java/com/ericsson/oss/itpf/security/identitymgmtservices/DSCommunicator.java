/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.itpf.security.identitymgmtservices;

import com.ericsson.oss.itpf.security.identitymgmtservices.datastore.DataStoreManager;
import org.forgerock.opendj.ldap.*;
import org.forgerock.opendj.ldap.requests.ModifyRequest;
import org.forgerock.opendj.ldap.requests.Requests;
import org.forgerock.opendj.ldap.responses.Result;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.forgerock.opendj.ldif.ConnectionEntryReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.SearchControls;
import java.util.*;

public class DSCommunicator {
    private final Logger logger = LoggerFactory.getLogger(DSCommunicator.class);

    private Connection connection;

    private static final String ADD = "ADD";
    private static final String REPLACE = "REPLACE";
    private static final String DELETE = "DELETE";
    private static final String UNKNOWN = "UNKNOWN";

    /**
     * Resolve Connection to LDAP
     *
     * @return
     * @throws DSCommunicatorException
     */
    private Connection getConnection() throws DSCommunicatorException {
        if ((connection == null) || connection.isClosed() || !connection.isValid()) {
            connection = DataStoreManager.getAdminInstance();
        }
        return connection;
    }

    /**
     * It looks for an entry in LDAP using the given dn
     *
     * @param dn String value of full DN
     * @return true if the entry is found, false if not
     * @throws DSCommunicatorException any error condition except no entry found
     */
    public boolean searchEntry(final String dn) throws DSCommunicatorException {
        try {
            final Connection dss = getConnection();
            if (dss != null) {
                final SearchResultEntry retEntry = dss.readEntry(dn);
                if (retEntry != null) {
                    logger.debug("Search is done and the name of retEntry.getName() is {}", retEntry.getName());
                    if (retEntry.getName().compareTo(DN.valueOf(dn)) == 0) {
                        logger.debug("searchEntry(), search dn:{} completed.", dn);
                        return true;
                    }
                }
            } else {
                logger.error("Failed to get connection to datastore for searchEntry operation.");
                throw new DSCommunicatorException("Failed to get datastore for searchEntry operation.", IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
            }
        } catch (final EntryNotFoundException e) {
            logger.warn("Entry {} does not exist, encountered exception: {}", dn, e.getMessage());
        } catch (final ConnectionException e) {
            logger.error("Failed to search entry: {} encountered ConnectionException: {} {}", dn, e.getMessage(),
                    IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
        } catch (final DSCommunicatorException e) {
            throw e;
        } catch (final Exception e) {
            logger.error("Failed to search entry: {} encountered ConnectionException: {} {}", dn, e.getMessage(),
                    IdmConstants.IDMS_UNEXPECTED_ERROR);
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
        }
        return false;
    }

    /**
     * The method is to add an entry and its attribute-value pairs to LDAP
     *
     * @param dn      String value of full DN
     * @param avPairs Map that contains attribute as a key and its values for the dn
     * @throws DSCommunicatorException any error condition including entry already exists
     */
    public void addEntry(final String dn, final Map<String, ArrayList<String>> avPairs) throws DSCommunicatorException {
        final String errMsg = "Add entry " + dn + " operation failed with error code ";
        try {
            final Connection dss = getConnection();
            if (dss == null) {
                logger.error("Failed to get connection to datastore for addEntry operation.");
                throw new DSCommunicatorException("Failed to get datastore for addEntry operation.", IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
            }
            final Entry entry = new LinkedHashMapEntry(dn);
            for (final Map.Entry<String, ArrayList<String>> e : avPairs.entrySet()) {
                entry.addAttribute(e.getKey(), e.getValue().toArray());
            }
            final Result rs = dss.add(entry);
            if (rs != null && rs.isSuccess()) {
                logger.debug("addEntry(), add dn: {} completed", dn);
            }
        } catch (final ConstraintViolationException e) {
            final ResultCode rc = e.getResult().getResultCode();
            if (rc == ResultCode.ATTRIBUTE_OR_VALUE_EXISTS) {
                logger.error("{} {} One or more attribute(s) already exist {}", errMsg, rc, e.getMessage());
                throw new DSCommunicatorException(e.getMessage(), IdmConstants.LDAP_ERROR_ATTRIBUTE_OR_VALUE_EXIST);
            }
            if (rc == ResultCode.ENTRY_ALREADY_EXISTS) {
                logger.error("{} {} Entry already exists {}", errMsg, rc, e.getMessage());
                throw new DSCommunicatorException(e.getMessage(), IdmConstants.LDAP_ERROR_ATTRIBUTE_OR_VALUE_EXIST);
            }
            logger.error("{} {} Unexpected error {}", errMsg, rc, e.getMessage());
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
        } catch (final ConnectionException e) {
            final ResultCode rc = e.getResult().getResultCode();
            logger.error("{} {} encountered ConnectionException: {}", errMsg, rc, e.getMessage());
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
        } catch (final ErrorResultException e) {
            final ResultCode rc = e.getResult().getResultCode();
            logger.error("{} {} encountered unexpected exception: {}", errMsg, rc, e.getMessage());
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
        } catch (final DSCommunicatorException e) {
            throw e;
        } catch (final Exception e) {
            logger.error("Failed to add entry: {} encountered unexpected error: {} {}", dn, e.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
        }
    }

    private void handleErrorResultException(final ErrorResultException exc, final String errMsg, final int retCode) throws DSCommunicatorException {
        final ResultCode rc = exc.getResult().getResultCode();
        logger.error("{} {} entry does not exist {}", errMsg, rc, exc.getMessage());
        throw new DSCommunicatorException(exc.getMessage(), retCode);
    }

    /**
     * The method is to delete the entry given by the dn from LDAP
     *
     * @param dn String value of full DN
     * @throws DSCommunicatorException any error condition including entry does not found
     */
    public void deleteEntry(final String dn) throws DSCommunicatorException {
        final String errMsg = "Delete entry " + dn + ": operation failed with error code ";
        try {
            final Connection dss = getConnection();
            if (dss != null) {
                final Result rs = dss.delete(dn);
                if (rs != null && rs.isSuccess()) {
                    logger.debug("deleteEntry(), delete dn: {} completed.", dn);
                }
            } else {
                logger.error("Failed to get connection to datastore for deleteEntry operation");
                throw new DSCommunicatorException("Failed to get datastore for deleteEntry operation", IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
            }
        } catch (final DSCommunicatorException e) {
            throw e;
        } catch (final EntryNotFoundException e) {
            handleErrorResultException(e, errMsg, IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY);
        } catch (final ConnectionException e) {
            handleErrorResultException(e, errMsg, IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
        } catch (final Exception e) {
            logger.error("Failed to delete entry: {} , encountered unexpected exception: {}", dn, e.getMessage());
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
        }
    }

    private HashMap<String, ArrayList<String>> getMultipleAttributesForConnection(final ConnectionEntryReader ceReader,
                                                                                  final List<String> attributesToReturn) throws ErrorResultIOException, SearchResultReferenceIOException {
        final HashMap<String, ArrayList<String>> retMap = new HashMap<>();
        if (ceReader != null) {
            while (ceReader.hasNext()) {
                if (!ceReader.isReference()) {
                    final SearchResultEntry rEntry = ceReader.readEntry();
                    for (final String temp : attributesToReturn) {
                        retMap.put(temp, getNotEmptyElementsAsListFromSet(rEntry.parseAttribute(temp).asSetOfString("")));
                    }
                }
            }
        }
        return retMap;
    }

    /**
     * This method is to query multiple attributes that are associated to dn. an attribute may multivalued attribute, so it expects a Map that
     * contains attribute as a key and its values as a list of values
     *
     * @param dn                 String value of full DN
     * @param scope              int search scope
     * @param attributesToReturn a List of attributes to be returned
     * @return a Map that contains attribute as a key and its values
     * @throws DSCommunicatorException any error condition including the entry does not exist
     */
    public Map<String, ArrayList<String>> queryMultipleAttributes(final String dn, final int scope, final List<String> attributesToReturn)
            throws DSCommunicatorException {
        try {
            SearchScope djscope;
            switch (scope) {
                case SearchControls.OBJECT_SCOPE:
                    djscope = SearchScope.BASE_OBJECT;
                    break;
                case SearchControls.ONELEVEL_SCOPE:
                    djscope = SearchScope.SINGLE_LEVEL;
                    break;
                case SearchControls.SUBTREE_SCOPE:
                    djscope = SearchScope.WHOLE_SUBTREE;
                    break;
                default:
                    djscope = SearchScope.WHOLE_SUBTREE;
                    break;
            }

            final String filter = "(objectclass=*)";
            final Connection dss = getConnection();
            if (dss == null) {
                logger.error("Failed to get connection to datastore for queryMultipleAttributes.");
                throw new DSCommunicatorException("Failed to get datastore for queryMultipleAttributes", IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
            }
            final ConnectionEntryReader reader = dss.search(dn, djscope, filter, attributesToReturn.toArray(new String[attributesToReturn.size()]));
            return getMultipleAttributesForConnection(reader, attributesToReturn);
        } catch (final DSCommunicatorException e) {
            throw e;
        } catch (final ErrorResultIOException e) {
            final String errMsg = "queryMultipleAttributes operation failed for entry: " + dn + ", " + e.getMessage();
            logger.error(errMsg, e);
            if (e.getCause() instanceof EntryNotFoundException) {
                throw new DSCommunicatorException(e.getMessage(), IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY);
            }
            if (e.getCause() instanceof ConnectionException) {
                throw new DSCommunicatorException(e.getMessage(), IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
            }
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
        } catch (final Exception e) {
            logger.error("Failed to perform queryMultipleAttributes for entry: {} , encountered exception: {}", dn, e.getMessage());
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
        }
    }

    /**
     * @param retSet
     * @return
     */
    private ArrayList<String> getNotEmptyElementsAsListFromSet(final Set<String> retSet) {
        final ArrayList<String> elementList = new ArrayList<>();
        for (final String s : retSet) {
            if (!s.isEmpty()) {
                elementList.add(s);
            }
        }
        return elementList;
    }

    private SearchScope convertScopeToSearchScope(final int scope) {
        final SearchScope searchScope;
        switch (scope) {
            case SearchControls.OBJECT_SCOPE:
                searchScope = SearchScope.BASE_OBJECT;
                break;
            case SearchControls.ONELEVEL_SCOPE:
                searchScope = SearchScope.SINGLE_LEVEL;
                break;
            default:
                searchScope = SearchScope.WHOLE_SUBTREE;
                break;
        }
        return searchScope;
    }

    /**
     * The method is to query an attribute to get its value(s) that is associated to dnPostfix using the filter string
     *
     * @param dnPostfix         String value of full DN to use as a base DN
     * @param scope             int value of search scope from the base DN
     * @param attributeToReturn String value of attribute name to look for
     * @param filterString      String value that can be used as (a) filter, e.g., gidNumber=5000
     * @return An array list that contains all the values, null if no value found for the attribute
     * @throws DSCommunicatorException any error condition including when the given dnPostfix does not exist
     */
    public List<String> querySingleAttribute(final String dnPostfix,
                                             final int scope,
                                             final String attributeToReturn,
                                             final String filterString) throws DSCommunicatorException {
        try {
            logger.debug("querySingleAttribute() : dnPostfix=[{}] , scope=[{}]", dnPostfix, scope);
            SearchScope searchScope = convertScopeToSearchScope(scope);
            final String filter = (filterString == null) ? "(objectclass=*)" : filterString;

            final Connection connectionLcl = getConnection();
            if (connectionLcl == null) {
                logger.error("Failed to get connection to datastore for querySingleAttribute operation.");
                throw new DSCommunicatorException("Failed to get datastore for querySingleAttribute operation",
                        IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
            }

            List<String> resultList = new ArrayList<>();
            final ConnectionEntryReader reader = connectionLcl.search(dnPostfix, searchScope, filter, attributeToReturn);
            if (reader != null) {
                while (reader.hasNext()) {
                    if (!reader.isReference()) {
                        final SearchResultEntry resultEntry = reader.readEntry();
                        final Set<String> retSet = resultEntry.parseAttribute(attributeToReturn).asSetOfString("");
                        resultList.addAll(getNotEmptyElementsAsListFromSet(retSet));
                    }
                }
            }
            logger.debug("ConnectionEntryReader search result: [{}]", resultList);
            return resultList;
        } catch (final DSCommunicatorException e) {
            throw e;
        } catch (final ErrorResultIOException e) {
            final String errMsg = "querySingleAttribute operation failed for entry: " + dnPostfix + ", " + e.getMessage();
            logger.error(errMsg, e);
            if (e.getCause() instanceof EntryNotFoundException) {
                throw new DSCommunicatorException(e.getMessage(), IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY);
            }
            if (e.getCause() instanceof ConnectionException) {
                throw new DSCommunicatorException(e.getMessage(), IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
            }
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
        } catch (final Exception e) {
            logger.error("Failed to querySingleAttribute operation for entry: {} , encountered exception: {}", dnPostfix, e.getMessage());
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
        }
    }

    /**
     * The method is to modify an existing entry by replacing the value in given attribute
     *
     * @param dn       String value of full DN that will be modified
     * @param attrName String value of attribute name that belongs to the entry
     * @param value    String value of the attribute
     * @throws DSCommunicatorException when dn does not exist, when attribute does not belong to the dn, and any other condition
     */
    public void modifyEntryReplace(final String dn, final String attrName, final String value) throws DSCommunicatorException {
        modifyEntry(dn, attrName, value, ModificationType.REPLACE);
    }

    /**
     * The method is to modify an existing entry by adding attribute with the given value
     *
     * @param dn       String value of full DN that will be modified
     * @param attrName String value of attribute name that belongs to the entry
     * @param value    String value of the attribute
     * @throws DSCommunicatorException when dn does not exist, when attribute does not belong to the dn, when the value already exists, and any other condition
     */
    public void modifyEntryAdd(final String dn, final String attrName, final String value) throws DSCommunicatorException {
        modifyEntry(dn, attrName, value, ModificationType.ADD);
    }

    /**
     * The method is to modify an existing entry by deleting the given attribute and the given value
     *
     * @param dn       String value of full DN that will be modified
     * @param attrName String value of attribute name that belongs to the entry
     * @param value    String value of the attribute
     * @throws DSCommunicatorException when dn does not exist, when attribute does not belong to the dn, when the value already exists, and any other condition
     */
    public void modifyEntryDelete(final String dn, final String attrName, final String value) throws DSCommunicatorException {
        modifyEntry(dn, attrName, value, ModificationType.DELETE);
    }

    private void modifyEntry(final String dn, final String attrName, final String value, final ModificationType modType)
            throws DSCommunicatorException {
        String modTypeString = "";
        if (modType == ModificationType.REPLACE) {
            modTypeString = REPLACE;
        } else if (modType == ModificationType.ADD) {
            modTypeString = ADD;
        } else if (modType == ModificationType.DELETE) {
            modTypeString = DELETE;
        } else {
            final String errStr = "Unspported operation " + modTypeString + ", failed to modifyEntry " + dn;
            logger.error(errStr);
            throw new DSCommunicatorException(errStr, IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
        }

        final String errMsg = "modifyEntry " + modTypeString + " operation for entry: " + dn + " failed with error code ";
        try {

            final Connection dss = getConnection();
            final ModifyRequest request = Requests.newModifyRequest(dn).addModification(modType, attrName, value);
            if (dss == null) {
                logger.error("Failed to get connection to datastore for modifyEntry {} operation.", modTypeString);
                throw new DSCommunicatorException("Failed to get datastore for modifyEntry operation", IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
            }
            final Result rs = dss.modify(request);
            if ((rs != null) && rs.isSuccess()) {
                logger.debug("modifyEntry(), modify dn: {} completed.", dn);
            }
        } catch (final DSCommunicatorException e) {
            throw e;
        } catch (final EntryNotFoundException e) {
            handleErrorResultException(e, errMsg, IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY);
        } catch (final ConnectionException e) {
            handleErrorResultException(e, errMsg, IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
        } catch (final ErrorResultException e) {
            handleErrorResultException(e, errMsg, IdmConstants.IDMS_UNEXPECTED_ERROR);
        } catch (final Exception e) {
            logger.error("Failed to modify entry {}, attribute {} ,encountered exception: {}", dn, attrName, e.getMessage());
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
        }
    }

    public String getPasswordFormat() {
        try {
            final List<String> passwordFormatStrings = querySingleAttribute(IdmConstants.LDAP_CONFIG_CN, SearchControls.SUBTREE_SCOPE,
                    IdmConstants.LDAP_CFG_CRED_FORMAT, IdmConstants.LDAP_CRED_GENERATOR_FILTER);

            return passwordFormatStrings.get(0);
        } catch (final Exception e) {
            logger.error("Failed to retrive ds-cfg-password-format, exception: {}", e.getMessage());
        }
        return null;
    }

    public Map<String, NamedCharacterSet> getPasswordCharacterSet() {
        Map<String, NamedCharacterSet> charsets = null;

        try {
            final List<String> passwordCharacterSetStrings = querySingleAttribute(IdmConstants.LDAP_CONFIG_CN, SearchControls.SUBTREE_SCOPE,
                    IdmConstants.LDAP_CFG_CRED_CHARACTER_SET, IdmConstants.LDAP_CRED_GENERATOR_FILTER);

            final SortedSet<String> ss = new TreeSet<>();
            for (final String s : passwordCharacterSetStrings) {
                if (s != null && !s.isEmpty()) {
                    ss.add(s);
                }
            }

            charsets = new HashMap<>();
            for (final NamedCharacterSet s : NamedCharacterSet.decodeCharacterSets(ss)) {
                if (!charsets.containsKey(s.getName())) {
                    charsets.put(s.getName(), s);
                }
            }
        } catch (final Exception e) {
            logger.error("Failed to retrive ds-cfg-password-character-set, exception: {}", e.getMessage());
        }
        return charsets;
    }

    public String getRandomPassword() {
        String randomPassword = null;

        try {
            final Map<String, NamedCharacterSet> charsets = getPasswordCharacterSet();
            final String formatString = getPasswordFormat();

            if (formatString != null && charsets != null) {
                // The numbers of characters of each type that should be used to generate the passwords.
                int[] characterCounts = null;
                // The character sets that should be used to generate the passwords.
                NamedCharacterSet[] characterSets = null;
                int totalLength = 0;

                final StringTokenizer tokenizer = new StringTokenizer(formatString, ", ");
                final ArrayList<NamedCharacterSet> setList = new ArrayList<>();
                final ArrayList<Integer> countList = new ArrayList<>();
                while (tokenizer.hasMoreTokens()) {
                    final String token = tokenizer.nextToken();
                    final int colonPos = token.indexOf(':');
                    final String name = token.substring(0, colonPos);
                    final int count = Integer.parseInt(token.substring(colonPos + 1));
                    final NamedCharacterSet charset = charsets.get(name);
                    if (charset != null) {
                        setList.add(charset);
                        countList.add(count);
                    }
                }
                characterSets = new NamedCharacterSet[setList.size()];
                characterCounts = new int[characterSets.length];
                for (int i = 0; i < characterSets.length; i++) {
                    characterSets[i] = setList.get(i);
                    characterCounts[i] = countList.get(i);
                    totalLength += characterCounts[i];
                }

                final StringBuilder buffer = new StringBuilder(totalLength);
                for (int i = 0; i < characterSets.length; i++) {
                    characterSets[i].getRandomCharacters(buffer, characterCounts[i]);
                }
                randomPassword = buffer.toString();
            }
        } catch (final Exception e) {
            logger.error("Failed to generate password, exception: {}", e.getMessage());
        }

        return randomPassword;
    }

    /**
     * The method is to modify an existing entry by adding multiple attributes with the given values
     *
     * @param dn                 String value of full DN that will be modified
     * @param attributesToModify Map of attribute and value pair that belongs to the entry
     * @throws DSCommunicatorException when dn does not exist, when attribute does not belong to the dn, when the value already exists, and any other condition
     */
    public void modifyEntryAddMultipleAttributes(final String dn, final Map<String, ArrayList<String>> attributesToModify)
            throws DSCommunicatorException {
        modifyEntryMultipleAttributes(dn, attributesToModify, ModificationType.ADD);
    }

    /**
     * The method is to modify an existing entry by removing multiple attributes with the given values
     *
     * @param dn                 String value of full DN that will be modified
     * @param attributesToModify Map of attribute and value pair that belongs to the entry
     * @throws DSCommunicatorException when dn does not exist, when attribute does not belong to the dn, when the value already removed, and any other condition
     */
    public void modifyEntryRemoveMultipleAttributes(final String dn, final Map<String, ArrayList<String>> attributesToModify)
            throws DSCommunicatorException {
        modifyEntryMultipleAttributes(dn, attributesToModify, ModificationType.DELETE);
    }


    /**
     * The method is to modify an existing entry by replacing multuple attributes with the given values
     *
     * @param dn                 String value of full DN that will be modified
     * @param attributesToModify Map of attribute and value pair that belongs to the entry
     * @throws DSCommunicatorException when dn does not exist, when attribute does not belong to the dn, when the value already removed, and any other condition
     */
    public void modifyEntryReplaceMultipleAttributes(final String dn, final Map<String, ArrayList<String>> attributesToModify)
            throws DSCommunicatorException {
        modifyEntryMultipleAttributes(dn, attributesToModify, ModificationType.REPLACE);

    }

    private void modifyEntryMultipleAttributes(final String dn, final Map<String, ArrayList<String>> attributesToModify,
                                               final ModificationType modType) throws DSCommunicatorException {
        final String modTypeString = getModTypeString(modType);
        if (modTypeString.equals(UNKNOWN)) {
            final String errStr = "Unspported operation, failed to modifyEntryMultipleAttributes " + dn;
            logger.error(errStr);
            throw new DSCommunicatorException(errStr, IdmConstants.IDMS_UNEXPECTED_ERROR);
        }

        final Connection dss = getConnection();
        final ModifyRequest request = Requests.newModifyRequest(dn);
        for (Map.Entry<String, ArrayList<String>> entry : attributesToModify.entrySet()) {
            for (final String value : entry.getValue()) {
                request.addModification(modType, entry.getKey(), value);
            }
        }
        if (dss == null) {
            logger.error("Failed to get connection to datastore for modifyEntryMultipleAttributes {}  operation.", modTypeString);
            throw new DSCommunicatorException("Failed to get datastore for modifyEntryMultipleAttributes operation",
                    IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
        }
        Result rs = null;
        final String errMsg = "modifyEntryMultipleAttributes " + modTypeString + " operation for entry: " + dn + " failed with error code ";
        try {
            rs = dss.modify(request);
        } catch (final ConstraintViolationException e) {
            final ResultCode rc = e.getResult().getResultCode();
            if (rc == ResultCode.ATTRIBUTE_OR_VALUE_EXISTS) {
                logger.error("{} {} One or more attribute(s) already exist", errMsg, rc);
                throw new DSCommunicatorException(e.getMessage(), IdmConstants.LDAP_ERROR_ATTRIBUTE_OR_VALUE_EXIST);
            }
            if (rc == ResultCode.ENTRY_ALREADY_EXISTS) {
                logger.error("{} {} Entry already exists", errMsg, rc);
                throw new DSCommunicatorException(e.getMessage(), IdmConstants.LDAP_ERROR_ATTRIBUTE_OR_VALUE_EXIST);
            }
            logger.error("{} {} encountered unexpected exception: {}", errMsg, rc, e.getMessage());
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
        } catch (final EntryNotFoundException e) {
            final ResultCode rc = e.getResult().getResultCode();
            logger.error("{} {} entry does not exist {}", errMsg, rc, e.getMessage());
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY);
        } catch (final ConnectionException e) {
            final ResultCode rc = e.getResult().getResultCode();
            logger.error("{} {} encountered ConnectionException: {} {}", errMsg, rc,
                    e.getMessage(), IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
        } catch (final ErrorResultException e) {
            final ResultCode rc = e.getResult().getResultCode();
            logger.error("{} {} encountered unexpected exception: {}", errMsg, rc, e.getMessage());
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
        }
        if (rs != null && rs.isSuccess()) {
            logger.debug("modifyEntryMultipleAttributes(), modify dn: {} completed.", dn);
        }
    }

    public boolean enmOnCloud() {
        logger.info("check enm on cloud property");
        return DataStoreManager.enmOnCloud();
    }

    private String getModTypeString(final ModificationType modType) {
        if (modType == ModificationType.REPLACE) {
            return REPLACE;
        } else if (modType == ModificationType.ADD) {
            return ADD;
        } else if (modType == ModificationType.DELETE) {
            return DELETE;
        } else {
            return UNKNOWN;
        }
    }

    /**
     * The method is to query a list of attributes to get its value(s) that is associated to dnPostfix using the filter string
     *
     * @param dnPostfix          String value of full DN to use as a base DN
     * @param scope              int value of search scope from the base DN
     * @param attributesToReturn String value of attribute name to look for
     * @param filterString       String value that can be used as (a) filter, e.g., gidNumber=5000
     * @return A list of SearchResultEntry object to be parsed to get values
     * @throws DSCommunicatorException any error condition including when the given dnPostfix does not exist
     */
    public List<SearchResultEntry> queryGenericMultipleAttributes(final String dnPostfix, final int scope,
                                                                  final List<String> attributesToReturn,
                                                                  final String filterString) throws DSCommunicatorException {
        try {
            SearchScope searchScope = convertScopeToSearchScope(scope);
            final String filter = (filterString == null) ? "(objectclass=*)" : filterString;

            final Connection connectionLcl = getConnection();
            if (connectionLcl == null) {
                logger.error("Failed to get connection to datastore for queryGenericMultipleAttributes operation.");
                throw new DSCommunicatorException("Failed to get datastore for queryGenericMultipleAttributes operation",
                        IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
            }

            List<SearchResultEntry> resultListWithSearchEntries = new ArrayList<>();
            final ConnectionEntryReader reader = connectionLcl.search(dnPostfix, searchScope, filter,
                    attributesToReturn.toArray(new String[attributesToReturn.size()]));
            if (reader != null) {
                while (reader.hasNext()) {
                    if (!reader.isReference()) {
                        final SearchResultEntry resultEntry = reader.readEntry();
                        resultListWithSearchEntries.add(resultEntry);
                    }
                }
            }
            return resultListWithSearchEntries;
        } catch (final DSCommunicatorException excp) {
            throw excp;
        } catch (final ErrorResultIOException excp) {
            final String errMsgMultipleSearch = "queryGenericMultipleAttributes operation failed for entry: " + dnPostfix + ", " + excp.getMessage();
            logger.error(errMsgMultipleSearch, excp);
            if (excp.getCause() instanceof EntryNotFoundException) {
                throw new DSCommunicatorException(excp.getMessage(), IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY);
            }
            if (excp.getCause() instanceof ConnectionException) {
                throw new DSCommunicatorException(excp.getMessage(), IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
            }
            throw new DSCommunicatorException(excp.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
        } catch (final Exception excp) {
            logger.error("Failed to queryGenericMultipleAttributes operation for entry: {} , encountered exception: {}", dnPostfix, excp.getMessage());
            throw new DSCommunicatorException(excp.getMessage(), IdmConstants.IDMS_UNEXPECTED_ERROR);
        }
    }
}
