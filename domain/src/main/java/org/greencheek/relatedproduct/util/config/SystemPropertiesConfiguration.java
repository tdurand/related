package org.greencheek.relatedproduct.util.config;

import org.greencheek.relatedproduct.api.searching.SearchResultsOutcome;
import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.greencheek.relatedproduct.util.config.ConfigurationConstants.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**

 * For Indexing:
 * -------------
 * The settings for a 2 cpu, 4 core per cpu, with a 12gb heap would be:
 * (JDK7) :
 * Standard JDK Opts:
 *
 * -Djava.awt.headless=true
 * -XX:+UseParNewGC
 * -XX:+UseConcMarkSweepGC
 * -XX:CMSInitiatingOccupancyFraction=90
 * -XX:+UseCMSInitiatingOccupancyOnly
 * -XX:MaxTenuringThreshold=15
 * -Djava.rmi.server.hostname=10.0.1.29
 * -Dcom.sun.management.jmxremote.port=3333
 * -Dcom.sun.management.jmxremote.ssl=false
 * -Dcom.sun.management.jmxremote.authenticate=false
 * -Xmx11776m -Xmn8700m -Xms11776m -Xss256k -XX:MaxPermSize=128m
 * -XX:+UnlockDiagnosticVMOptions -XX:ParGCCardsPerStrideChunk=4096 -XX:+AggressiveOpts
 * -XX:+UseCondCardMark
 *
 * Indexing opts:
 *
 * -Drelated-product.wait.strategy=busy
 * -Drelated-product.size.of.incoming.request.queue=65536
 * -Drelated-product.number.of.indexing.request.processors=8
 * -Drelated-product.index.batch.size=450
 * -Drelated-product.elastic.search.transport.hosts=10.0.1.19:9300
 * -Des.discovery.zen.ping.multicast.enabled=false
 * -Des.discovery.zen.ping.unicast.hosts=10.0.1.19
 * -Dnetwork.tcp.no_delay=false
 *
 * ==============================================================
 *
 * -Drelated-product.size.of.related.content.search.request.queue=32768
 * -Drelated-product.size.of.response.processing.queue=32768
 * -Drelated-product.size.of.related.content.search.request.and.response.queue=262144
 * -Drelated-product.size.of.related.content.search.request.handler.queue=32768
 * -Drelated-product.number.of.searching.request.processors=8
 *
 */
public class SystemPropertiesConfiguration implements Configuration {

    private final boolean SAFE_TO_OUTPUT_REQUEST_DATA ;
    private final int MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES ;
    private final int MAX_NUMBER_OF_RELATED_PRODUCTS_PER_PURCHASE ;
    private final int RELATED_PRODUCT_ID_LENGTH ;
    private final String RELATED_PRODUCT_INVALID_ID_STRING ;
    private final int MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES ;
    private final int MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES ;

    private final int RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH ;
    private final int RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH ;
    private final int SIZE_OF_INCOMING_REQUEST_QUEUE ;
    private final int SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE ;
    private final int BATCH_INDEX_SIZE ;

    private final int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE ;

    private final int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE ;
    private final int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE ;

    private final int MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT ;
    private final int NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS ;

    private final String KEY_FOR_FREQUENCY_RESULT_ID ;
    private final String KEY_FOR_FREQUENCY_RESULT_OCCURRENCE ;
    private final String KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS ;
    private final String KEY_FOR_FREQUENCY_RESULTS ;

    private final String REQUEST_PARAMETER_FOR_SIZE ;
    private final String REQUEST_PARAMETER_FOR_ID ;

    private final int DEFAULT_NUMBER_OF_RESULTS ;
    private final int SIZE_OF_RESPONSE_PROCESSING_QUEUE ;
    private final int NUMBER_OF_INDEXING_REQUEST_PROCESSORS ;
    private final int NUMBER_OF_SEARCHING_REQUEST_PROCESSORS ;

    private final String STORAGE_INDEX_NAME_PREFIX ;
    private final String STORAGE_INDEX_NAME_ALIAS ;
    private final String STORAGE_CONTENT_TYPE_NAME ;
    private final String STORAGE_CLUSTER_NAME ;
    private final String STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME ;
    private final String STORAGE_FACET_SEARCH_EXECUTION_HINT ;

    private final String KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR ;
    private final String KEY_FOR_INDEX_REQUEST_DATE_ATTR ;
    private final String KEY_FOR_INDEX_REQUEST_ID_ATTR ;
    private final String KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR ;

    private final String ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME ;
    private final String ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME ;
    private final String ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME ;

    private final long FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS ;

    // can be "day|hour|minute";
    private final String RELATED_PRODUCT_STORAGE_LOCATION_MAPPER ;

    private final int TIMED_OUT_SEARCH_REQUEST_STATUS_CODE ;
    private final int FAILED_SEARCH_REQUEST_STATUS_CODE ;
    private final int NO_FOUND_SEARCH_REQUEST_STATUS_CODE ;
    private final int FOUND_SEARCH_REQUEST_STATUS_CODE ;
    private final int MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE ;

    // string Encoding for the properties;
    private final String PROPERTY_ENCODING ;


    private final String WAIT_STRATEGY ;

    private final String ES_CLIENT_TYPE ;

    private final boolean INDEXNAME_DATE_CACHING_ENABLED ;

    private final int NUMBER_OF_INDEXNAMES_TO_CACHE ;

    private final boolean REPLACE_OLD_INDEXED_CONTENT ;

    private final boolean SEPARATE_INDEXING_THREAD  ;

    private final boolean DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS ;

    private final String ELASTIC_SEARCH_TRANSPORT_HOSTS ;

    private final int DEFAULT_ELASTIC_SEARCH_PORT ;

    private final boolean USE_SHARED_SEARCH_REPOSITORY ;

    private final boolean RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED ;

    private final int[] searchRequestResponseCodes = new int[SearchResultsOutcome.values().length];
    private final WaitStrategyFactory waitStrategyFactory;
    private final ElasticeSearchClientType esClientType;
    private static final Logger log = LoggerFactory.getLogger(SystemPropertiesConfiguration.class);


    public SystemPropertiesConfiguration() {
        this(getMergedSystemPropertiesAndDefaults());
    }

    protected SystemPropertiesConfiguration(Map<String,Object> properties) {

        SAFE_TO_OUTPUT_REQUEST_DATA = getBoolean(properties,PROPNAME_SAFE_TO_OUTPUT_REQUEST_DATA,DEFAULT_SAFE_TO_OUTPUT_REQUEST_DATA);
        MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES = getInt(properties,PROPNAME_MAX_NO_OF_RELATED_PRODUCT_PROPERTES,DEFAULT_MAX_NO_OF_RELATED_PRODUCT_PROPERTES);
        MAX_NUMBER_OF_RELATED_PRODUCTS_PER_PURCHASE = getInt(properties,PROPNAME_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST,DEFAULT_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST);
        RELATED_PRODUCT_ID_LENGTH = getInt(properties,PROPNAME_RELATED_PRODUCT_ID_LENGTH,DEFAULT_RELATED_PRODUCT_ID_LENGTH);
        RELATED_PRODUCT_INVALID_ID_STRING = getString(properties,PROPNAME_RELATED_PRODUCT_INVALID_ID_STRING,DEFAULT_RELATED_PRODUCT_INVALID_ID_STRING);
        MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES = getInt(properties,PROPNAME_MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES,DEFAULT_MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES);
        MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES = getInt(properties,PROPNAME_MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES,DEFAULT_MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES);
        RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH = getInt(properties,PROPNAME_RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH,DEFAULT_RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH);
        RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH = getInt(properties,PROPNAME_RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH,DEFAULT_RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH);
        SIZE_OF_INCOMING_REQUEST_QUEUE = getInt(properties,PROPNAME_SIZE_OF_INCOMING_REQUEST_QUEUE,DEFAULT_SIZE_OF_INCOMING_REQUEST_QUEUE);
        SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE = getInt(properties,PROPNAME_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE,DEFAULT_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE);
        BATCH_INDEX_SIZE = getInt(properties,PROPNAME_BATCH_INDEX_SIZE,DEFAULT_BATCH_INDEX_SIZE);
        SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE = Util.ceilingNextPowerOfTwo(getInt(properties,PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE,DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE));
        SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE = Util.ceilingNextPowerOfTwo(getInt(properties,PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE,DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE));
        SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE = Util.ceilingNextPowerOfTwo(getInt(properties,PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE,DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE));
        MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT =  getInt(properties,PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT,DEFAULT_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT);
        NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS = getInt(properties,PROPNAME_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS,DEFAULT_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS);
        KEY_FOR_FREQUENCY_RESULT_ID = getString(properties,PROPNAME_KEY_FOR_FREQUENCY_RESULT_ID,DEFAULT_KEY_FOR_FREQUENCY_RESULT_ID);
        KEY_FOR_FREQUENCY_RESULT_OCCURRENCE = getString(properties,PROPNAME_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE,DEFAULT_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE);
        KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS = getString(properties,PROPNAME_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS,DEFAULT_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS);
        KEY_FOR_FREQUENCY_RESULTS = getString(properties,PROPNAME_KEY_FOR_FREQUENCY_RESULTS,DEFAULT_KEY_FOR_FREQUENCY_RESULTS);
        REQUEST_PARAMETER_FOR_SIZE = getString(properties,PROPNAME_REQUEST_PARAMETER_FOR_SIZE,DEFAULT_REQUEST_PARAMETER_FOR_SIZE);
        REQUEST_PARAMETER_FOR_ID = getString(properties,PROPNAME_REQUEST_PARAMETER_FOR_ID,DEFAULT_REQUEST_PARAMETER_FOR_ID);
        DEFAULT_NUMBER_OF_RESULTS = getInt(properties,PROPNAME_DEFAULT_NUMBER_OF_RESULTS,DEFAULT_DEFAULT_NUMBER_OF_RESULTS);
        SIZE_OF_RESPONSE_PROCESSING_QUEUE = Util.ceilingNextPowerOfTwo(getInt(properties,PROPNAME_SIZE_OF_RESPONSE_PROCESSING_QUEUE,DEFAULT_SIZE_OF_RESPONSE_PROCESSING_QUEUE));
        NUMBER_OF_INDEXING_REQUEST_PROCESSORS = getInt(properties,PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS,DEFAULT_NUMBER_OF_INDEXING_REQUEST_PROCESSORS);
        NUMBER_OF_SEARCHING_REQUEST_PROCESSORS = Util.ceilingNextPowerOfTwo(getInt(properties,PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS,DEFAULT_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS));
        STORAGE_INDEX_NAME_PREFIX = getString(properties,PROPNAME_STORAGE_INDEX_NAME_PREFIX,DEFAULT_STORAGE_INDEX_NAME_PREFIX);
        STORAGE_INDEX_NAME_ALIAS = getString(properties,PROPNAME_STORAGE_INDEX_NAME_ALIAS,DEFAULT_STORAGE_INDEX_NAME_ALIAS);
        STORAGE_CONTENT_TYPE_NAME = getString(properties,PROPNAME_STORAGE_CONTENT_TYPE_NAME,DEFAULT_STORAGE_CONTENT_TYPE_NAME);
        STORAGE_CLUSTER_NAME = getString(properties,PROPNAME_STORAGE_CLUSTER_NAME,DEFAULT_STORAGE_CLUSTER_NAME);
        STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME =  getString(properties,PROPNAME_STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME,DEFAULT_STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME);
        STORAGE_FACET_SEARCH_EXECUTION_HINT = getString(properties,PROPNAME_STORAGE_FACET_SEARCH_EXECUTION_HINT,DEFAULT_STORAGE_FACET_SEARCH_EXECUTION_HINT);
        KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR = getString(properties,PROPNAME_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR,DEFAULT_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR);
        KEY_FOR_INDEX_REQUEST_DATE_ATTR = getString(properties,PROPNAME_KEY_FOR_INDEX_REQUEST_DATE_ATTR,DEFAULT_KEY_FOR_INDEX_REQUEST_DATE_ATTR);
        KEY_FOR_INDEX_REQUEST_ID_ATTR = getString(properties,PROPNAME_KEY_FOR_INDEX_REQUEST_ID_ATTR,DEFAULT_KEY_FOR_INDEX_REQUEST_ID_ATTR);
        KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR = getString(properties,PROPNAME_KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR,DEFAULT_KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR);
        ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME = getString(properties,PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME,DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME);
        ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME = getString(properties,PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME,DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME);
        ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME = getString(properties,PROPNAME_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME,DEFAULT_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME);
        FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS = getLong(properties,PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS,DEFAULT_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS);
        RELATED_PRODUCT_STORAGE_LOCATION_MAPPER = getString(properties,PROPNAME_RELATED_PRODUCT_STORAGE_LOCATION_MAPPER,DEFAULT_RELATED_PRODUCT_STORAGE_LOCATION_MAPPER);
        TIMED_OUT_SEARCH_REQUEST_STATUS_CODE = getInt(properties,PROPNAME_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE,DEFAULT_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE);
        FAILED_SEARCH_REQUEST_STATUS_CODE = getInt(properties,PROPNAME_FAILED_SEARCH_REQUEST_STATUS_CODE,DEFAULT_FAILED_SEARCH_REQUEST_STATUS_CODE);
        NO_FOUND_SEARCH_REQUEST_STATUS_CODE = getInt(properties,PROPNAME_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE,DEFAULT_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE);
        FOUND_SEARCH_REQUEST_STATUS_CODE = getInt(properties,PROPNAME_FOUND_SEARCH_REQUEST_STATUS_CODE,DEFAULT_FOUND_SEARCH_REQUEST_STATUS_CODE);
        MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE = getInt(properties,PROPNAME_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE,DEFAULT_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE);

        PROPERTY_ENCODING = getString(properties,PROPNAME_PROPERTY_ENCODING,DEFAULT_PROPERTY_ENCODING);
        WAIT_STRATEGY = getString(properties,PROPNAME_WAIT_STRATEGY,DEFAULT_WAIT_STRATEGY).toLowerCase();
        ES_CLIENT_TYPE = getString(properties,PROPNAME_ES_CLIENT_TYPE,DEFAULT_ES_CLIENT_TYPE).toLowerCase();
        INDEXNAME_DATE_CACHING_ENABLED = getBoolean(properties,PROPNAME_INDEXNAME_DATE_CACHING_ENABLED,DEFAULT_INDEXNAME_DATE_CACHING_ENABLED);
        NUMBER_OF_INDEXNAMES_TO_CACHE = getInt(properties,PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE,DEFAULT_NUMBER_OF_INDEXNAMES_TO_CACHE);
        REPLACE_OLD_INDEXED_CONTENT = getBoolean(properties,PROPNAME_REPLACE_OLD_INDEXED_CONTENT,DEFAULT_REPLACE_OLD_INDEXED_CONTENT);
        SEPARATE_INDEXING_THREAD  = getBoolean(properties,PROPNAME_SEPARATE_INDEXING_THREAD,DEFAULT_SEPARATE_INDEXING_THREAD);
        DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS =  getBoolean(properties,PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS,DEFAULT_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS);
        ELASTIC_SEARCH_TRANSPORT_HOSTS = getString(properties,PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS,DEFAULT_ELASTIC_SEARCH_TRANSPORT_HOSTS);
        DEFAULT_ELASTIC_SEARCH_PORT = getInt(properties,PROPNAME_DEFAULT_ELASTIC_SEARCH_PORT,DEFAULT_DEFAULT_ELASTIC_SEARCH_PORT);
        USE_SHARED_SEARCH_REPOSITORY = getBoolean(properties,PROPNAME_USE_SHARED_SEARCH_REPOSITORY,DEFAULT_USE_SHARED_SEARCH_REPOSITORY);
        RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED = getBoolean(properties,PROPNAME_RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED,DEFAULT_RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED);

        setResponseCodes(NO_FOUND_SEARCH_REQUEST_STATUS_CODE,FAILED_SEARCH_REQUEST_STATUS_CODE,
                TIMED_OUT_SEARCH_REQUEST_STATUS_CODE,MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE,
                FOUND_SEARCH_REQUEST_STATUS_CODE);

        waitStrategyFactory = parseWaitStrategy(WAIT_STRATEGY);
        esClientType = parseEsClientType(ES_CLIENT_TYPE);
    }

    private static Map<String,Object> getMergedSystemPropertiesAndDefaults() {
        Map<String,Object> defaultProperties = new HashMap(ConfigurationConstants.DEFAULT_SETTINGS);
        Map<String,Object> systemProperties = parseSystemProperties();

        return replaceProperties(defaultProperties, systemProperties);
    }

    private static boolean getBoolean(Map<String,Object> properties,String prop, boolean defaultValue) {
        Object value = properties.get(prop);
        if(value==null) return defaultValue;
        else {
            try {
                return ((Boolean)value).booleanValue();
            } catch(ClassCastException e) {
                log.warn("Failed to cast value for property {} to a boolean",prop);
                return defaultValue;
            }
        }
    }

    private static int getInt(Map<String,Object> properties,String prop, int defaultValue) {
        Object value = properties.get(prop);
        if(value==null) return defaultValue;
        else {
            try {
                return ((Integer)value).intValue();
            } catch(ClassCastException e) {
                log.warn("Failed to cast value for property {} to a int",prop);
                return defaultValue;
            }
        }
    }

    private static long getLong(Map<String,Object> properties,String prop, long defaultValue) {
        Object value = properties.get(prop);
        if(value==null) return defaultValue;
        else {
            try {
                return ((Long)value).longValue();
            } catch(ClassCastException e) {
                log.warn("Failed to cast value for property {} to a int",prop);
                return defaultValue;
            }
        }
    }

    private static String getString(Map<String,Object> properties,String prop, String defaultValue) {
        Object value = properties.get(prop);
        if(value==null) return defaultValue;
        else {
            try {
                return (String)value;
            } catch(ClassCastException e) {
                log.warn("Failed to cast value for property {} to a int",prop);
                return defaultValue;
            }
        }
    }


    /**
     * returns a new map that is a combination of the values from the first parameter {@see defaultProperties}
     * with the values override by the one that are specified in {@see overrides}
     *
     * @param defaultProperties The map will contain all these keys.
     * @param overrides the properties that are to replace those in the defaults.  This may be a sub set of the
     *                  properties in the defaultProperties, or new values.
     * @return map with replaced properties
     */
    public static Map<String,Object> mergeProperties(Map<String,Object> defaultProperties,
                                              Map<String,Object> overrides) {
        Map<String,Object> props = new HashMap<String,Object>(defaultProperties);
        if(overrides==null || overrides.size()==0) return props;
        for(String prop : overrides.keySet()) {
            props.put(prop,overrides.get(prop));
        }
        return props;
    }

    /**
     * returns a new map that is a combination of the values from the first parameter {@see defaultProperties}
     * with the values override by the one that are specified in {@see overrides}.  Only if the property exists in the
     * defaultProperties will if be replaced.  In other words. any new property in the overrides that is not in
     * defaultProperties will not be present in the returned array
     *
     * @param defaultProperties The map will contain all these keys.
     * @param overrides the properties that are to replace those in the defaults.  This may be a sub set of the
     *                  properties in the defaultProperties, or new values.
     * @return map with replaced properties
     */
    public static Map<String,Object> replaceProperties(Map<String,Object> defaultProperties,
                                              Map<String,Object> overrides) {
        Map<String,Object> props = new HashMap<String,Object>(defaultProperties);
        if(overrides==null || overrides.size()==0) return props;
        for(String prop : overrides.keySet()) {
            if(props.containsKey(prop)) props.put(prop,overrides.get(prop));
        }
        return props;
    }


    /**
     * Looks for System properties with the names defined in {@link org.greencheek.relatedproduct.util.config.ConfigurationConstants}
     * Parsing the resulting values in to appropriate types.  If the system property is not defined an
     * entry in the return map (with the same name as the constant in ConfigurationContants), is not created.
     * If a system property is defined an entry in the map will exist.
     *
     * @return
     */
    public static Map<String,Object> parseSystemProperties() {
        Map<String,Object> systemProperties = new HashMap<String,Object>(100);

        parseBoolean(systemProperties,PROPNAME_SAFE_TO_OUTPUT_REQUEST_DATA);
        parseInt(systemProperties,PROPNAME_MAX_NO_OF_RELATED_PRODUCT_PROPERTES);
        parseInt(systemProperties,PROPNAME_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST);
        parseInt(systemProperties,PROPNAME_RELATED_PRODUCT_ID_LENGTH);
        parseString(systemProperties,PROPNAME_RELATED_PRODUCT_INVALID_ID_STRING);
        parseString(systemProperties,PROPNAME_RELATED_PRODUCT_INVALID_ID_STRING);
        parseInt(systemProperties,PROPNAME_MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES);
        parseInt(systemProperties, PROPNAME_MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES);
        parseInt(systemProperties,PROPNAME_RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH);
        parseInt(systemProperties,PROPNAME_RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH);
        parseInt(systemProperties,PROPNAME_SIZE_OF_INCOMING_REQUEST_QUEUE);
        parseInt(systemProperties,PROPNAME_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE);
        parseInt(systemProperties,PROPNAME_BATCH_INDEX_SIZE);
        parseInt(systemProperties,PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE);
        parseInt(systemProperties,PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE);
        parseInt(systemProperties,PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE);
        parseInt(systemProperties,PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT);
        parseInt(systemProperties,PROPNAME_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS);
        parseString(systemProperties,PROPNAME_KEY_FOR_FREQUENCY_RESULT_ID);
        parseString(systemProperties,PROPNAME_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE);
        parseString(systemProperties,PROPNAME_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS);
        parseString(systemProperties,PROPNAME_KEY_FOR_FREQUENCY_RESULTS);
        parseString(systemProperties,PROPNAME_REQUEST_PARAMETER_FOR_SIZE);
        parseString(systemProperties,PROPNAME_REQUEST_PARAMETER_FOR_ID);
        parseInt(systemProperties,PROPNAME_DEFAULT_NUMBER_OF_RESULTS);
        parseInt(systemProperties,PROPNAME_SIZE_OF_RESPONSE_PROCESSING_QUEUE);
        parseInt(systemProperties,PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS);
        parseInt(systemProperties,PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS);
        parseString(systemProperties,PROPNAME_STORAGE_INDEX_NAME_PREFIX);
        parseString(systemProperties,PROPNAME_STORAGE_INDEX_NAME_ALIAS);
        parseString(systemProperties,PROPNAME_STORAGE_CONTENT_TYPE_NAME);
        parseString(systemProperties,PROPNAME_STORAGE_CLUSTER_NAME);
        parseString(systemProperties,PROPNAME_STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME);
        parseString(systemProperties,PROPNAME_STORAGE_FACET_SEARCH_EXECUTION_HINT);
        parseString(systemProperties,PROPNAME_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR);
        parseString(systemProperties,PROPNAME_KEY_FOR_INDEX_REQUEST_DATE_ATTR);
        parseString(systemProperties,PROPNAME_KEY_FOR_INDEX_REQUEST_ID_ATTR);
        parseString(systemProperties,PROPNAME_KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR);
        parseString(systemProperties,PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME);
        parseString(systemProperties,PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME);
        parseString(systemProperties,PROPNAME_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME);
        parseLong(systemProperties,PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS);
        parseString(systemProperties,PROPNAME_RELATED_PRODUCT_STORAGE_LOCATION_MAPPER);
        parseInt(systemProperties,PROPNAME_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE);
        parseInt(systemProperties,PROPNAME_FAILED_SEARCH_REQUEST_STATUS_CODE);
        parseInt(systemProperties,PROPNAME_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE);
        parseInt(systemProperties,PROPNAME_FOUND_SEARCH_REQUEST_STATUS_CODE);
        parseInt(systemProperties,PROPNAME_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE);
        parseString(systemProperties,PROPNAME_PROPERTY_ENCODING);
        parseString(systemProperties,PROPNAME_WAIT_STRATEGY);
        parseString(systemProperties,PROPNAME_ES_CLIENT_TYPE);
        parseBoolean(systemProperties,PROPNAME_INDEXNAME_DATE_CACHING_ENABLED);
        parseInt(systemProperties,PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE);
        parseBoolean(systemProperties,PROPNAME_REPLACE_OLD_INDEXED_CONTENT);
        parseBoolean(systemProperties,PROPNAME_SEPARATE_INDEXING_THREAD);
        parseBoolean(systemProperties,PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS);
        parseString(systemProperties,PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS);
        parseInt(systemProperties,PROPNAME_DEFAULT_ELASTIC_SEARCH_PORT);
        parseBoolean(systemProperties,PROPNAME_USE_SHARED_SEARCH_REPOSITORY);
        parseBoolean(systemProperties,PROPNAME_RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED); 
        return systemProperties;
    }

    protected void setResponseCodes(int notfound,int failedSearch, int timedOut,int missingHandler, int found) {
        searchRequestResponseCodes[SearchResultsOutcome.EMPTY_RESULTS.getIndex()] = notfound;
        searchRequestResponseCodes[SearchResultsOutcome.FAILED_REQUEST.getIndex()] = failedSearch;
        searchRequestResponseCodes[SearchResultsOutcome.REQUEST_TIMEOUT.getIndex()] = timedOut;
        searchRequestResponseCodes[SearchResultsOutcome.HAS_RESULTS.getIndex()] = found;
        searchRequestResponseCodes[SearchResultsOutcome.MISSING_SEARCH_RESULTS_HANDLER.getIndex()] = missingHandler;

    }


    private static void parseBoolean(Map<String,Object> values, String property) {
        Boolean b = parseBoolean(property);
        if(b!=null) values.put(property,b);
    }

    private static Boolean parseBoolean(String property) {
        String value = System.getProperty(property);
        if(value==null || value.trim().length()==0) {
            return null;
        } else {
            return Boolean.parseBoolean(value);
        }
    }

    private static void parseString(Map<String,Object> values, String property) {
        String s = parseString(property);
        if(s!=null) values.put(property,s);
    }

    private static String parseString(String property) {
        String value = System.getProperty(property);
        if(value==null || value.trim().length()==0) {
            return null;
        } else {
            return value;
        }
    }

    private static void parseInt(Map<String,Object> values, String property) {
        Integer i = parseInt(property);
        if(i!=null) values.put(property,i);
    }

    private static Integer parseInt(String property) {
        String value = System.getProperty(property);
        if(value==null || value.trim().length()==0) {
            return null;
        } else {
            try {
                return Integer.valueOf(value);
            } catch(NumberFormatException e) {
                return null;
            }
        }
    }

    private static void parseLong(Map<String,Object> values, String property) {
        Long i = parseLong(property);
        if(i!=null) values.put(property,i);
    }

    private static Long parseLong(String property) {
        String value = System.getProperty(property);
        if(value==null || value.trim().length()==0) {
            return null;
        } else {
            try {
                return Long.valueOf(value);
            } catch(NumberFormatException e) {
                return null;
            }
        }
    }

    protected DefaultWaitStrategyFactory parseWaitStrategy(String type) {
        if(type.contains("yield")) {
            return new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.YIELDING);
        }
        else if(type.contains("block")) {
            return new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.BLOCKING);
        }
        else if(type.contains("sleep")) {
            return new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.SLEEPING);
        }
        else if (type.contains("busy")) {
            return new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.BUSY);
        }
        else {
            return new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.YIELDING);
        }
    }

    protected ElasticeSearchClientType parseEsClientType(String type) {
        if(type.equals("transport")) {
            return ElasticeSearchClientType.TRANSPORT;
        } else if(type.equals("node")) {
            return ElasticeSearchClientType.NODE;
        } else {
            return ElasticeSearchClientType.TRANSPORT;
        }
    }



    public WaitStrategyFactory getWaitStrategyFactory() {
        return waitStrategyFactory;
    }

    public int getMaxNumberOfSearchCriteriaForRelatedContent() {
        return MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT;
    }

    public int getSizeOfRelatedContentSearchRequestHandlerQueue() {
        return SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE;
    }

    public int getSizeOfRelatedContentSearchRequestQueue() {
        return SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE;
    }

    @Override
    public int getSizeOfRelatedContentSearchRequestAndResponseQueue() {
        return SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE;
    }

    @Override
    public int getSizeOfResponseProcessingQueue() {
        return SIZE_OF_RESPONSE_PROCESSING_QUEUE;
    }

    @Override
    public int getNumberOfSearchingRequestProcessors() {
        return NUMBER_OF_SEARCHING_REQUEST_PROCESSORS;
    }

    @Override
    public int getNumberOfExpectedLikeForLikeRequests() {
        return NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS;
    }


    @Override
    public String getKeyForFrequencyResultOccurrence() {
        return KEY_FOR_FREQUENCY_RESULT_OCCURRENCE;
    }

    @Override
    public String getKeyForFrequencyResultId() {
        return KEY_FOR_FREQUENCY_RESULT_ID;
    }

    @Override
    public String getKeyForFrequencyResults() {
        return KEY_FOR_FREQUENCY_RESULTS;
    }


    @Override
    public String getKeyForFrequencyResultOverallResultsSize() {
        return KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS;
    }

    @Override
    public String getKeyForIndexRequestRelatedWithAttr() {
        return KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR;
    }


    @Override
    public String getKeyForIndexRequestDateAttr() {
        return KEY_FOR_INDEX_REQUEST_DATE_ATTR;
    }

    @Override
    public String getKeyForIndexRequestIdAttr() {
        return KEY_FOR_INDEX_REQUEST_ID_ATTR;
    }

    @Override
    public String getKeyForIndexRequestProductArrayAttr() {
        return KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR;
    }

    @Override
    public String getRequestParameterForSize() {
        return REQUEST_PARAMETER_FOR_SIZE;
    }

    @Override
    public String getRequestParameterForId() {
        return REQUEST_PARAMETER_FOR_ID;
    }

    @Override
    public int getDefaultNumberOfResults() {
        return DEFAULT_NUMBER_OF_RESULTS;
    }

    @Override
    public String getStorageIndexNamePrefix() {
        return STORAGE_INDEX_NAME_PREFIX;
    }

    @Override
    public String getStorageIndexNameAlias() {
        return STORAGE_INDEX_NAME_ALIAS;
    }

    @Override
    public String getStorageContentTypeName() {
        return STORAGE_CONTENT_TYPE_NAME;
    }

    @Override
    public String getStorageClusterName() {
        return STORAGE_CLUSTER_NAME;
    }

    @Override
    public String getStorageFrequentlyRelatedProductsFacetResultsFacetName() {
        return STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME;
    }

    @Override
    public String getElasticSearchClientDefaultNodeSettingFileName() {
        return ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME;
    }

    @Override
    public String getElasticSearchClientDefaultTransportSettingFileName() {
        return ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME;
    }

    @Override
    public String getElasticSearchClientOverrideSettingFileName() {
        return ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME;
    }

    @Override
    public String getStorageLocationMapper() {
        return RELATED_PRODUCT_STORAGE_LOCATION_MAPPER;
    }

    @Override
    public long getFrequentlyRelatedProductsSearchTimeoutInMillis() {
        return FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS;
    }

    @Override
    public int getResponseCode(SearchResultsOutcome type) {
        return  searchRequestResponseCodes[type.getIndex()];
    }

    @Override
    public int getTimedOutSearchRequestStatusCode() {
        return TIMED_OUT_SEARCH_REQUEST_STATUS_CODE;
    }

    @Override
    public int getFailedSearchRequestStatusCode() {
        return FAILED_SEARCH_REQUEST_STATUS_CODE;
    }

    @Override
    public int getNoFoundSearchResultsStatusCode() {
        return NO_FOUND_SEARCH_REQUEST_STATUS_CODE;
    }

    @Override
    public int getFoundSearchResultsStatusCode() {
        return FOUND_SEARCH_REQUEST_STATUS_CODE;
    }

    @Override
    public int getMissingSearchResultsHandlerStatusCode() {
        return MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE;
    }

    @Override
    public int getNumberOfIndexingRequestProcessors() {
        return NUMBER_OF_INDEXING_REQUEST_PROCESSORS;
    }

    @Override
    public int getMaxNumberOfRelatedProductProperties() {
        return MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES;
    }

    @Override
    public int getMaxNumberOfRelatedProductsPerPurchase() {
        return MAX_NUMBER_OF_RELATED_PRODUCTS_PER_PURCHASE;
    }

    @Override
    public int getRelatedProductIdLength() {
        return RELATED_PRODUCT_ID_LENGTH;
    }

    @Override
    public String getRelatedProductInvalidIdString() {
        return RELATED_PRODUCT_INVALID_ID_STRING;
    }

    @Override
    public int getMaxRelatedProductPostDataSizeInBytes() {
        return MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES;
    }

    @Override
    public int getMinRelatedProductPostDataSizeInBytes() {
        return MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES;
    }

    @Override
    public int getRelatedProductAdditionalPropertyKeyLength() {
        return RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH;
    }

    @Override
    public int getRelatedProductAdditionalPropertyValueLength() {
        return RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH;
    }

    @Override
    public boolean isSearchResponseDebugOutputEnabled() {
        return RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED;
    }

    @Override
    public int getIndexBatchSize() {
        return BATCH_INDEX_SIZE;
    }

    @Override
    public int getSizeOfBatchIndexingRequestQueue() {
        return SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE;
    }

    @Override
    public int getSizeOfIncomingMessageQueue() {
        return SIZE_OF_INCOMING_REQUEST_QUEUE;
    }

    @Override
    public String getPropertyEncoding() {
        return PROPERTY_ENCODING;
    }

    @Override
    public boolean isIndexNameDateCachingEnabled() {
        return INDEXNAME_DATE_CACHING_ENABLED;
    }

    @Override
    public int getNumberOfIndexNamesToCache() {
        return NUMBER_OF_INDEXNAMES_TO_CACHE;
    }

    @Override
    public boolean getShouldReplaceOldContentIfExists() {
        return REPLACE_OLD_INDEXED_CONTENT;
    }

    @Override
    public boolean getShouldUseSeparateIndexStorageThread() {
        return SEPARATE_INDEXING_THREAD;
    }

    @Override
    public boolean shouldDiscardIndexRequestWithTooManyRelations() {
        return DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS;
    }

    @Override
    public ElasticeSearchClientType getElasticSearchClientType() {
        return esClientType;
    }

    @Override
    public String getElasticSearchTransportHosts() {
        return ELASTIC_SEARCH_TRANSPORT_HOSTS;
    }

    @Override
    public int getDefaultElasticSearchPort() {
        return DEFAULT_ELASTIC_SEARCH_PORT;
    }

    @Override
    public boolean useSharedSearchRepository() {
        return USE_SHARED_SEARCH_REPOSITORY;
    }

    @Override
    public boolean isSafeToOutputRequestData() {
        return SAFE_TO_OUTPUT_REQUEST_DATA;
    }

    @Override
    public String getStorageFacetExecutionHint() {
        return STORAGE_FACET_SEARCH_EXECUTION_HINT;
    }


    public Map<String,Object> toMap() {
        Map<String,Object> configuration = new HashMap<String,Object>();
        configuration.put(PROPNAME_WAIT_STRATEGY,getWaitStrategyFactory());
        configuration.put(PROPNAME_SAFE_TO_OUTPUT_REQUEST_DATA,isSafeToOutputRequestData());
        configuration.put(PROPNAME_MAX_NO_OF_RELATED_PRODUCT_PROPERTES, getMaxNumberOfRelatedProductProperties());
        configuration.put(PROPNAME_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST,getMaxNumberOfRelatedProductsPerPurchase());
        configuration.put(PROPNAME_RELATED_PRODUCT_ID_LENGTH,getRelatedProductIdLength());
        configuration.put(PROPNAME_RELATED_PRODUCT_INVALID_ID_STRING,getRelatedProductInvalidIdString());
        configuration.put(PROPNAME_MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES,getMaxRelatedProductPostDataSizeInBytes());
        configuration.put(PROPNAME_MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES,getMinRelatedProductPostDataSizeInBytes());
        configuration.put(PROPNAME_RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH,getRelatedProductAdditionalPropertyKeyLength());
        configuration.put(PROPNAME_RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH,getRelatedProductAdditionalPropertyValueLength());
        configuration.put(PROPNAME_SIZE_OF_INCOMING_REQUEST_QUEUE,getSizeOfIncomingMessageQueue());
        configuration.put(PROPNAME_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE,getSizeOfBatchIndexingRequestQueue());
        configuration.put(PROPNAME_BATCH_INDEX_SIZE,getIndexBatchSize());
        configuration.put(PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE,getSizeOfRelatedContentSearchRequestQueue());
        configuration.put(PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE,getSizeOfRelatedContentSearchRequestHandlerQueue());
        configuration.put(PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE,getSizeOfRelatedContentSearchRequestAndResponseQueue());
        configuration.put(PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT,getMaxNumberOfSearchCriteriaForRelatedContent());
        configuration.put(PROPNAME_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS,getNumberOfExpectedLikeForLikeRequests());
        configuration.put(PROPNAME_KEY_FOR_FREQUENCY_RESULT_ID,getKeyForFrequencyResultId());
        configuration.put(PROPNAME_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE,getKeyForFrequencyResultOccurrence());
        configuration.put(PROPNAME_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS,getKeyForFrequencyResultOverallResultsSize());
        configuration.put(PROPNAME_KEY_FOR_FREQUENCY_RESULTS,getKeyForFrequencyResults());
        configuration.put(PROPNAME_REQUEST_PARAMETER_FOR_SIZE,getRequestParameterForSize());
        configuration.put(PROPNAME_REQUEST_PARAMETER_FOR_ID,getRequestParameterForId());
        configuration.put(PROPNAME_DEFAULT_NUMBER_OF_RESULTS,getDefaultNumberOfResults());
        configuration.put(PROPNAME_SIZE_OF_RESPONSE_PROCESSING_QUEUE,getSizeOfIncomingMessageQueue());
        configuration.put(PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS,getNumberOfIndexingRequestProcessors());
        configuration.put(PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS,getNumberOfSearchingRequestProcessors());
        configuration.put(PROPNAME_STORAGE_INDEX_NAME_PREFIX,getStorageIndexNamePrefix());
        configuration.put(PROPNAME_STORAGE_INDEX_NAME_ALIAS,getStorageIndexNameAlias());
        configuration.put(PROPNAME_STORAGE_CONTENT_TYPE_NAME,getStorageContentTypeName());
        configuration.put(PROPNAME_STORAGE_CLUSTER_NAME,getStorageClusterName());
        configuration.put(PROPNAME_STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME,getStorageFrequentlyRelatedProductsFacetResultsFacetName());
        configuration.put(PROPNAME_STORAGE_FACET_SEARCH_EXECUTION_HINT,getStorageFacetExecutionHint());
        configuration.put(PROPNAME_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR,getKeyForIndexRequestRelatedWithAttr());
        configuration.put(PROPNAME_KEY_FOR_INDEX_REQUEST_DATE_ATTR,getKeyForIndexRequestDateAttr());
        configuration.put(PROPNAME_KEY_FOR_INDEX_REQUEST_ID_ATTR,getKeyForIndexRequestIdAttr());
        configuration.put(PROPNAME_KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR,getKeyForIndexRequestProductArrayAttr());
        configuration.put(PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME,getElasticSearchClientDefaultTransportSettingFileName());
        configuration.put(PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME,getElasticSearchClientDefaultNodeSettingFileName());
        configuration.put(PROPNAME_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME,getElasticSearchClientOverrideSettingFileName());
        configuration.put(PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS,getFrequentlyRelatedProductsSearchTimeoutInMillis());
        configuration.put(PROPNAME_RELATED_PRODUCT_STORAGE_LOCATION_MAPPER,getStorageLocationMapper());
        configuration.put(PROPNAME_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE,getTimedOutSearchRequestStatusCode());
        configuration.put(PROPNAME_FAILED_SEARCH_REQUEST_STATUS_CODE,getFailedSearchRequestStatusCode());
        configuration.put(PROPNAME_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE,getNoFoundSearchResultsStatusCode());
        configuration.put(PROPNAME_FOUND_SEARCH_REQUEST_STATUS_CODE,getFoundSearchResultsStatusCode());
        configuration.put(PROPNAME_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE,getMissingSearchResultsHandlerStatusCode());
        configuration.put(PROPNAME_PROPERTY_ENCODING,getPropertyEncoding());
        configuration.put(PROPNAME_WAIT_STRATEGY,getWaitStrategyFactory());
        configuration.put(PROPNAME_ES_CLIENT_TYPE,getElasticSearchClientType());
        configuration.put(PROPNAME_INDEXNAME_DATE_CACHING_ENABLED,isIndexNameDateCachingEnabled());
        configuration.put(PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE,getNumberOfIndexNamesToCache());
        configuration.put(PROPNAME_REPLACE_OLD_INDEXED_CONTENT,getShouldReplaceOldContentIfExists());
        configuration.put(PROPNAME_SEPARATE_INDEXING_THREAD,getShouldUseSeparateIndexStorageThread());
        configuration.put(PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS,shouldDiscardIndexRequestWithTooManyRelations());
        configuration.put(PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS,getElasticSearchTransportHosts());
        configuration.put(PROPNAME_DEFAULT_ELASTIC_SEARCH_PORT,getDefaultElasticSearchPort());
        configuration.put(PROPNAME_USE_SHARED_SEARCH_REPOSITORY,useSharedSearchRepository());
        configuration.put(PROPNAME_RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED,isSearchResponseDebugOutputEnabled());
        return configuration;
    }

    Map<String,Object> getDiffs(Map<String,Object> defaults, Map<String,Object> overrides) {
        Map<String,Object> clonedOverrides = new HashMap(overrides);
        for(String key : defaults.keySet()) {
            Object value = overrides.get(key);
            Object defaultValue = defaults.get(key);
            if(value==null && defaultValue==null) {
                clonedOverrides.remove(key);
                continue;
            }

            if(defaultValue!=null) {
                if(defaultValue.equals(value)) clonedOverrides.remove(key);
            }
        }
        return clonedOverrides;
    }
}
