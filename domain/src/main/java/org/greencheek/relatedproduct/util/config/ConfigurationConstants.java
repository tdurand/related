package org.greencheek.relatedproduct.util.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dominictootell on 19/01/2014.
 */
public class ConfigurationConstants {
    public static final String APPLICATION_CONTEXT_ATTRIBUTE_NAME = "ApplicationContext";

    public static final String PROPNAME_SAFE_TO_OUTPUT_REQUEST_DATA = "related-product.safe.to.output.index.request.data";
    public static final String PROPNAME_MAX_NO_OF_RELATED_PRODUCT_PROPERTES = "related-product.max.number.related.product.properties";
    public static final String PROPNAME_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST = "related-product.max.number.related.products.per.request";
    public static final String PROPNAME_RELATED_PRODUCT_ID_LENGTH = "related-product.related.product.id.length";
    public static final String PROPNAME_RELATED_PRODUCT_INVALID_ID_STRING = "related-product.related.product.invalid.id.string";
    public static final String PROPNAME_MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES = "related-product.max.related.product.post.data.size.in.bytes";
    public static final String PROPNAME_MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES = "related-product.min.related.product.post.data.size.in.bytes";
    public static final String PROPNAME_RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH = "related-product.additional.prop.key.length";
    public static final String PROPNAME_RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH = "related-product.additional.prop.value.length";
    public static final String PROPNAME_SIZE_OF_INCOMING_REQUEST_QUEUE = "related-product.indexing.size.of.incoming.request.queue";
    public static final String PROPNAME_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE  = "related-product.indexing.size.of.batch.indexing.request.queue";
    public static final String PROPNAME_BATCH_INDEX_SIZE = "related-product.indexing.batch.size";
    public static final String PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE = "related-product.searching.size.of.related.content.search.request.queue";
    public static final String PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE = "related-product.searching.size.of.related.content.search.request.handler.queue";
    public static final String PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE = "related-product.searching.size.of.related.content.search.request.and.response.queue";
    public static final String PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT = "related-product.searching.max.number.of.search.criteria.for.related.content";
    public static final String PROPNAME_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS = "related-product.searching.number.of.expected.like.for.like.requests";
    public static final String PROPNAME_KEY_FOR_FREQUENCY_RESULT_ID = "related-product.searching.key.for.frequency.result.id";
    public static final String PROPNAME_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE = "related-product.searching.key.for.frequency.result.occurrence";
    public static final String PROPNAME_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS = "related-product.searching.key.for.frequency.result.overall.no.of.related.products";
    public static final String PROPNAME_KEY_FOR_FREQUENCY_RESULTS ="related-product.searching.key.for.frequency.results";
    public static final String PROPNAME_REQUEST_PARAMETER_FOR_SIZE = "related-product.searching.request.parameter.for.size";
    public static final String PROPNAME_REQUEST_PARAMETER_FOR_ID =  "related-product.searching.request.parameter.for.id";
    public static final String PROPNAME_DEFAULT_NUMBER_OF_RESULTS= "related-product.searching.default.number.of.results";
    public static final String PROPNAME_SIZE_OF_RESPONSE_PROCESSING_QUEUE = "related-product.searching.size.of.response.processing.queue";
    public static final String PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS = "related-product.indexing.number.of.indexing.request.processors";
    public static final String PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS = "related-product.searching.number.of.searching.request.processors";
    public static final String PROPNAME_STORAGE_INDEX_NAME_PREFIX = "related-product.storage.index.name.prefix";
    public static final String PROPNAME_STORAGE_INDEX_NAME_ALIAS = "related-product.storage.index.name.alias";
    public static final String PROPNAME_STORAGE_CONTENT_TYPE_NAME = "related-product.storage.content.type.name";
    public static final String PROPNAME_STORAGE_CLUSTER_NAME = "related-product.storage.cluster.name";
    public static final String PROPNAME_STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME= "related-product.storage.frequently.related.products.facet.results.facet.name";
    public static final String PROPNAME_STORAGE_FACET_SEARCH_EXECUTION_HINT  = "related-product.storage.searching.facet.search.execution.hint";
    public static final String PROPNAME_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR =  "related-product.indexing.key.for.index.request.related.with.attr";
    public static final String PROPNAME_KEY_FOR_INDEX_REQUEST_DATE_ATTR = "related-product.indexing.key.for.index.request.date.attr";
    public static final String PROPNAME_KEY_FOR_INDEX_REQUEST_ID_ATTR = "related-product.indexing.key.for.index.request.id.attr";
    public static final String PROPNAME_KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR = "related-product.indexing.key.for.index.request.product.array.attr";
    public static final String PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME = "related-product.elastic.search.client.default.transport.settings.file.name";
    public static final String PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME =  "related-product.elastic.search.client.default.node.settings.file.name";
    public static final String PROPNAME_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME  = "related-product.elastic.search.client.override.settings.file.name";
    public static final String PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS = "related-product.searching.frequently.related.search.timeout.in.millis";
    public static final String PROPNAME_RELATED_PRODUCT_STORAGE_LOCATION_MAPPER =  "related-product.storage.location.mapper";
    public static final String PROPNAME_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE = "related-product.searching.timed.out.search.request.status.code";
    public static final String PROPNAME_FAILED_SEARCH_REQUEST_STATUS_CODE =  "related-product.searching.failed.search.request.status.code";
    public static final String PROPNAME_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE = "related-product.searching.not.found.search.request.status.code";
    public static final String PROPNAME_FOUND_SEARCH_REQUEST_STATUS_CODE = "related-product.searching.missing.search.results.handler.status.code";
    public static final String PROPNAME_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE = "related-product.missing.search.results.handler.status.code";
    public static final String PROPNAME_PROPERTY_ENCODING = "related-product.additional.prop.string.encoding";
    public static final String PROPNAME_WAIT_STRATEGY = "related-product.wait.strategy";
    public static final String PROPNAME_ES_CLIENT_TYPE  = "related-product.es.client.type";
    public static final String PROPNAME_INDEXNAME_DATE_CACHING_ENABLED = "related-product.indexing.indexname.date.caching.enabled";
    public static final String PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE = "related-product.indexing.number.of.indexname.to.cache";
    public static final String PROPNAME_REPLACE_OLD_INDEXED_CONTENT = "related-product.indexing.replace.old.indexed.content";
    public static final String PROPNAME_SEPARATE_INDEXING_THREAD = "related-product.use.separate.repository.storage.thread";
    public static final String PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS ="related-product.indexing.discard.storage.requests.with.too.many.relations";
    public static final String PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS = "related-product.elastic.search.transport.hosts";
    public static final String PROPNAME_DEFAULT_ELASTIC_SEARCH_PORT = "related-product.elastic.search.default.port";
    public static final String PROPNAME_USE_SHARED_SEARCH_REPOSITORY = "related-product.searching.use.shared.search.repository";
    public static final String PROPNAME_RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED = "related-product.searching.response.debug.output.enabled";

    public static final boolean DEFAULT_SAFE_TO_OUTPUT_REQUEST_DATA = false;
    public static final int DEFAULT_MAX_NO_OF_RELATED_PRODUCT_PROPERTES = 10;
    public static final int DEFAULT_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST = 10;
    public static final int DEFAULT_RELATED_PRODUCT_ID_LENGTH = 36;
    public static final String DEFAULT_RELATED_PRODUCT_INVALID_ID_STRING = "INVALID_ID";
    public static final int DEFAULT_MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES = 10240;
    public static final int DEFAULT_MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES = 4096;
    public static final int DEFAULT_RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH = 30;
    public static final int DEFAULT_RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH = 30;
    public static final int DEFAULT_SIZE_OF_INCOMING_REQUEST_QUEUE = 2048;
    public static final int DEFAULT_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE = -1;
    public static final int DEFAULT_BATCH_INDEX_SIZE = 128;
    public static final int DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE = 2048;
    public static final int DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE = 2048;
    public static final int DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE = 2048;
    public static final int DEFAULT_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT = 10;
    public static final int DEFAULT_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS = 10;
    public static final String DEFAULT_KEY_FOR_FREQUENCY_RESULT_ID = "id";
    public static final String DEFAULT_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE = "frequency";
    public static final String DEFAULT_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS = "size";
    public static final String DEFAULT_KEY_FOR_FREQUENCY_RESULTS = "results";
    public static final String DEFAULT_REQUEST_PARAMETER_FOR_SIZE = "maxresults";
    public static final String DEFAULT_REQUEST_PARAMETER_FOR_ID = "id";
    public static final int DEFAULT_DEFAULT_NUMBER_OF_RESULTS = 4;
    public static final int DEFAULT_SIZE_OF_RESPONSE_PROCESSING_QUEUE = 2048;
    public static final int DEFAULT_NUMBER_OF_INDEXING_REQUEST_PROCESSORS = 2;
    public static final int DEFAULT_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS = 4;
    public static final String DEFAULT_STORAGE_INDEX_NAME_PREFIX = "relatedproducts";
    public static final String DEFAULT_STORAGE_INDEX_NAME_ALIAS = "";
    public static final String DEFAULT_STORAGE_CONTENT_TYPE_NAME = "relatedproduct";
    public static final String DEFAULT_STORAGE_CLUSTER_NAME = "relatedproducts";
    public static final String DEFAULT_STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME = "frequently-related-with";
    public static final String DEFAULT_STORAGE_FACET_SEARCH_EXECUTION_HINT = "";
    public static final String DEFAULT_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR ="related-with";
    public static final String DEFAULT_KEY_FOR_INDEX_REQUEST_DATE_ATTR = "date";
    public static final String DEFAULT_KEY_FOR_INDEX_REQUEST_ID_ATTR = "id";
    public static final String DEFAULT_KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR ="products";
    public static final String DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME = "default-transport-elasticsearch.yml";
    public static final String DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME = "default-node-elasticsearch.yml";
    public static final String DEFAULT_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME = "elasticsearch.yml";
    public static final long DEFAULT_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS = 5000;
    public static final String DEFAULT_RELATED_PRODUCT_STORAGE_LOCATION_MAPPER = "day";
    public static final int DEFAULT_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE = 504;
    public static final int DEFAULT_FAILED_SEARCH_REQUEST_STATUS_CODE = 502;
    public static final int DEFAULT_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE = 404;
    public static final int DEFAULT_FOUND_SEARCH_REQUEST_STATUS_CODE = 200;
    public static final int DEFAULT_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE = 500;
    public static final String DEFAULT_PROPERTY_ENCODING = "UTF-8";
    public static final String DEFAULT_WAIT_STRATEGY = "yield";
    public static final String DEFAULT_ES_CLIENT_TYPE = "transport";
    public static final boolean DEFAULT_INDEXNAME_DATE_CACHING_ENABLED = true;
    public static final int DEFAULT_NUMBER_OF_INDEXNAMES_TO_CACHE = 365;
    public static final boolean DEFAULT_REPLACE_OLD_INDEXED_CONTENT = false;
    public static final boolean DEFAULT_SEPARATE_INDEXING_THREAD = false;
    public static final boolean DEFAULT_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS = false;
    public static final String DEFAULT_ELASTIC_SEARCH_TRANSPORT_HOSTS = "127.0.0.1:9300";
    public static final int DEFAULT_DEFAULT_ELASTIC_SEARCH_PORT = 9300;
    public static final boolean DEFAULT_USE_SHARED_SEARCH_REPOSITORY = false;
    public static final boolean DEFAULT_RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED = false;

//    public static final String[] PROPERTY_NAMES = new String[]{PROPNAME_SAFE_TO_OUTPUT_REQUEST_DATA, PROPNAME_MAX_NO_OF_RELATED_PRODUCT_PROPERTES,
//            PROPNAME_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST, PROPNAME_RELATED_PRODUCT_ID_LENGTH, PROPNAME_RELATED_PRODUCT_INVALID_ID_STRING,
//            PROPNAME_MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES, PROPNAME_MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES,
//            PROPNAME_RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH, PROPNAME_RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH,
//            PROPNAME_SIZE_OF_INCOMING_REQUEST_QUEUE, PROPNAME_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE, PROPNAME_BATCH_INDEX_SIZE,
//            PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE, PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE,
//            PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE, PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT,
//            PROPNAME_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS, PROPNAME_KEY_FOR_FREQUENCY_RESULT_ID, PROPNAME_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE,
//            PROPNAME_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS, PROPNAME_KEY_FOR_FREQUENCY_RESULTS, PROPNAME_REQUEST_PARAMETER_FOR_SIZE,
//            PROPNAME_REQUEST_PARAMETER_FOR_ID, PROPNAME_DEFAULT_NUMBER_OF_RESULTS =, PROPNAME_SIZE_OF_RESPONSE_PROCESSING_QUEUE,
//            PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS, PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS, PROPNAME_STORAGE_INDEX_NAME_PREFIX,
//            PROPNAME_STORAGE_INDEX_NAME_ALIAS, PROPNAME_STORAGE_CONTENT_TYPE_NAME, PROPNAME_STORAGE_CLUSTER_NAME,
//            PROPNAME_STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME =, PROPNAME_STORAGE_FACET_SEARCH_EXECUTION_HINT,
//            PROPNAME_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR, PROPNAME_KEY_FOR_INDEX_REQUEST_DATE_ATTR, PROPNAME_KEY_FOR_INDEX_REQUEST_ID_ATTR,
//            PROPNAME_KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR, PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME,
//            PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME, PROPNAME_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME,
//            PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS, PROPNAME_RELATED_PRODUCT_STORAGE_LOCATION_MAPPER,
//            PROPNAME_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE, PROPNAME_FAILED_SEARCH_REQUEST_STATUS_CODE, PROPNAME_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE,
//            PROPNAME_FOUND_SEARCH_REQUEST_STATUS_CODE, PROPNAME_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE, PROPNAME_PROPERTY_ENCODING, PROPNAME_WAIT_STRATEGY,
//            PROPNAME_ES_CLIENT_TYPE, PROPNAME_INDEXNAME_DATE_CACHING_ENABLED, PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE, PROPNAME_REPLACE_OLD_INDEXED_CONTENT,
//            PROPNAME_SEPARATE_INDEXING_THREAD, PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS, PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS,
//            PROPNAME_DEFAULT_ELASTIC_SEARCH_PORT, PROPNAME_USE_SHARED_SEARCH_REPOSITORY, PROPNAME_RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED};

    public static final Map<String,Object> DEFAULT_SETTINGS;
    static {
        Map<String,Object> configuration = new HashMap<String,Object>(100);
        configuration.put(PROPNAME_WAIT_STRATEGY,DEFAULT_WAIT_STRATEGY);
        configuration.put(PROPNAME_SAFE_TO_OUTPUT_REQUEST_DATA,DEFAULT_SAFE_TO_OUTPUT_REQUEST_DATA);
        configuration.put(PROPNAME_MAX_NO_OF_RELATED_PRODUCT_PROPERTES,DEFAULT_MAX_NO_OF_RELATED_PRODUCT_PROPERTES);
        configuration.put(PROPNAME_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST,DEFAULT_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST);
        configuration.put(PROPNAME_RELATED_PRODUCT_ID_LENGTH,DEFAULT_RELATED_PRODUCT_ID_LENGTH);
        configuration.put(PROPNAME_RELATED_PRODUCT_INVALID_ID_STRING,DEFAULT_RELATED_PRODUCT_INVALID_ID_STRING);
        configuration.put(PROPNAME_MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES,DEFAULT_MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES);
        configuration.put(PROPNAME_MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES,DEFAULT_MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES);
        configuration.put(PROPNAME_RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH,DEFAULT_RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH);
        configuration.put(PROPNAME_RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH,DEFAULT_RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH);
        configuration.put(PROPNAME_SIZE_OF_INCOMING_REQUEST_QUEUE,DEFAULT_SIZE_OF_INCOMING_REQUEST_QUEUE);
        configuration.put(PROPNAME_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE,DEFAULT_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE);
        configuration.put(PROPNAME_BATCH_INDEX_SIZE,DEFAULT_BATCH_INDEX_SIZE);
        configuration.put(PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE,DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE);
        configuration.put(PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE,DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE);
        configuration.put(PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE,DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE);
        configuration.put(PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT,DEFAULT_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT);
        configuration.put(PROPNAME_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS,DEFAULT_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS);
        configuration.put(PROPNAME_KEY_FOR_FREQUENCY_RESULT_ID,DEFAULT_KEY_FOR_FREQUENCY_RESULT_ID);
        configuration.put(PROPNAME_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE,DEFAULT_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE);
        configuration.put(PROPNAME_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS,DEFAULT_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS);
        configuration.put(PROPNAME_KEY_FOR_FREQUENCY_RESULTS,DEFAULT_KEY_FOR_FREQUENCY_RESULTS);
        configuration.put(PROPNAME_REQUEST_PARAMETER_FOR_SIZE,DEFAULT_REQUEST_PARAMETER_FOR_SIZE);
        configuration.put(PROPNAME_REQUEST_PARAMETER_FOR_ID,DEFAULT_REQUEST_PARAMETER_FOR_ID);
        configuration.put(PROPNAME_DEFAULT_NUMBER_OF_RESULTS,DEFAULT_DEFAULT_NUMBER_OF_RESULTS);
        configuration.put(PROPNAME_SIZE_OF_RESPONSE_PROCESSING_QUEUE,DEFAULT_SIZE_OF_RESPONSE_PROCESSING_QUEUE);
        configuration.put(PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS,DEFAULT_NUMBER_OF_INDEXING_REQUEST_PROCESSORS);
        configuration.put(PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS,DEFAULT_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS);
        configuration.put(PROPNAME_STORAGE_INDEX_NAME_PREFIX,DEFAULT_STORAGE_INDEX_NAME_PREFIX);
        configuration.put(PROPNAME_STORAGE_INDEX_NAME_ALIAS,DEFAULT_STORAGE_INDEX_NAME_ALIAS);
        configuration.put(PROPNAME_STORAGE_CONTENT_TYPE_NAME,DEFAULT_STORAGE_CONTENT_TYPE_NAME);
        configuration.put(PROPNAME_STORAGE_CLUSTER_NAME,DEFAULT_STORAGE_CLUSTER_NAME);
        configuration.put(PROPNAME_STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME,DEFAULT_STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME);
        configuration.put(PROPNAME_STORAGE_FACET_SEARCH_EXECUTION_HINT,DEFAULT_STORAGE_FACET_SEARCH_EXECUTION_HINT);
        configuration.put(PROPNAME_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR,DEFAULT_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR);
        configuration.put(PROPNAME_KEY_FOR_INDEX_REQUEST_DATE_ATTR,DEFAULT_KEY_FOR_INDEX_REQUEST_DATE_ATTR);
        configuration.put(PROPNAME_KEY_FOR_INDEX_REQUEST_ID_ATTR,DEFAULT_KEY_FOR_INDEX_REQUEST_ID_ATTR);
        configuration.put(PROPNAME_KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR,DEFAULT_KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR);
        configuration.put(PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME,DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME);
        configuration.put(PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME,DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME);
        configuration.put(PROPNAME_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME,DEFAULT_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME);
        configuration.put(PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS,DEFAULT_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS);
        configuration.put(PROPNAME_RELATED_PRODUCT_STORAGE_LOCATION_MAPPER,DEFAULT_RELATED_PRODUCT_STORAGE_LOCATION_MAPPER);
        configuration.put(PROPNAME_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE,DEFAULT_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE);
        configuration.put(PROPNAME_FAILED_SEARCH_REQUEST_STATUS_CODE,DEFAULT_FAILED_SEARCH_REQUEST_STATUS_CODE);
        configuration.put(PROPNAME_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE,DEFAULT_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE);
        configuration.put(PROPNAME_FOUND_SEARCH_REQUEST_STATUS_CODE,DEFAULT_FOUND_SEARCH_REQUEST_STATUS_CODE);
        configuration.put(PROPNAME_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE,DEFAULT_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE);
        configuration.put(PROPNAME_PROPERTY_ENCODING,DEFAULT_PROPERTY_ENCODING);
        configuration.put(PROPNAME_WAIT_STRATEGY,DEFAULT_WAIT_STRATEGY);
        configuration.put(PROPNAME_ES_CLIENT_TYPE,DEFAULT_ES_CLIENT_TYPE);
        configuration.put(PROPNAME_INDEXNAME_DATE_CACHING_ENABLED,DEFAULT_INDEXNAME_DATE_CACHING_ENABLED);
        configuration.put(PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE,DEFAULT_NUMBER_OF_INDEXNAMES_TO_CACHE);
        configuration.put(PROPNAME_REPLACE_OLD_INDEXED_CONTENT,DEFAULT_REPLACE_OLD_INDEXED_CONTENT);
        configuration.put(PROPNAME_SEPARATE_INDEXING_THREAD,DEFAULT_SEPARATE_INDEXING_THREAD);
        configuration.put(PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS,DEFAULT_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS);
        configuration.put(PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS,DEFAULT_ELASTIC_SEARCH_TRANSPORT_HOSTS);
        configuration.put(PROPNAME_DEFAULT_ELASTIC_SEARCH_PORT,DEFAULT_DEFAULT_ELASTIC_SEARCH_PORT);
        configuration.put(PROPNAME_USE_SHARED_SEARCH_REPOSITORY,DEFAULT_USE_SHARED_SEARCH_REPOSITORY);
        configuration.put(PROPNAME_RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED,DEFAULT_RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED);


        DEFAULT_SETTINGS = configuration;
    }


}
