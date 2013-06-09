package org.greencheek.relatedproduct.util.config;

import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 */
@Named
public class SystemPropertiesConfiguration implements Configuration {
    private final static short MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES = Short.valueOf(System.getProperty("related-product.max.number.related.product.properties", "10"));


    private final static short MAX_NUMBER_OF_RELATED_PRODUCTS_PER_PURCHASE = Short.valueOf(System.getProperty("related-product.max.number.related.products.per.product", "10"));
    private final static short RELATED_PRODUCT_ID_LENGTH = Short.valueOf(System.getProperty("related-product.related.product.id.length", "36"));
    private final static String RELATED_PRODUCT_INVALID_ID_STRING = System.getProperty("related-product.related.product.invalid.id.string", "INVALID_ID");
    private final static int MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES = Integer.valueOf(System.getProperty("related-product.max.related.product.post.data.size.in.bytes","10240"));

    private final static short RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH = Short.valueOf(System.getProperty("related-product.related.product.additional.key.length", "30"));
    private final static short RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH = Short.valueOf(System.getProperty("related-product.related.product.additional.value.length", "30"));
    private final static int SIZE_OF_INDEX_REQUEST_QUEUE = Integer.valueOf(System.getProperty("related-product.size.of.index.request.queue", "2048"));

    private final static int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE = Integer.valueOf(System.getProperty("related-product.size.of.related.content.search.request.queue", "2048"));
    private final static int SIZE_OF_RELATED_CONTENT_SEARCH_RESULTS_QUEUE = Integer.valueOf(System.getProperty("related-product.size.of.related.content.search.results.queue", "2048"));
    private final static int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE = Integer.valueOf(System.getProperty("related-product.size.of.related.content.search.request.handler.queue", "2048"));
    private final static int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE = Integer.valueOf(System.getProperty("related-product.size.of.related.content.search.request.and.response.queue", "2048"));

    private final static short MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT =  Short.valueOf(System.getProperty("related-product.max.number.of.search.criteria.for.related.content", "10"));
    private final static int NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS = Integer.valueOf(System.getProperty("related-product.number.of.expected.like.for.like.requests", "10"));

    private final static String RELATED_WITH_FACET_NAME = System.getProperty("related-product.related.with.facet.name","related-with");
    private final static String KEY_FOR_FREQUENCY_RESULT_NAME = System.getProperty("related-product.key.for.frequency.result.name","id");
    private final static String KEY_FOR_FREQUENCY_RESULT_SIZE = System.getProperty("related-product.key.for.frequency.result.name","frequency");
    private final static String KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_REALTED_PRODUCTS = System.getProperty("related-product.key.for.frequency.result.name","size");
    private final static String KEY_FOR_FREQUENCY_RESULTS = System.getProperty("related-product.key.for.frequency.results","results");

    public short getMaxNumberOfSearchCriteriaForRelatedContent() {
        return MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT;
    }

    public int getSizeOfRelatedContentSearchRequestHandlerQueue() {
        return SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE;
    }

    public int getSizeOfRelatedContentSearchResultsQueue() {
        return SIZE_OF_RELATED_CONTENT_SEARCH_RESULTS_QUEUE;
    }

    public int getSizeOfRelatedContentSearchRequestQueue() {
        return SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE;
    }

    @Override
    public int getSizeOfRelatedContentSearchRequestAndResponseQueue() {
        return SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE;
    }

    @Override
    public int getNumberOfExpectedLikeForLikeRequests() {
        return NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS;
    }

    @Override
    public String getRelatedWithFacetName() {
        return RELATED_WITH_FACET_NAME;
    }

    @Override
    public String getKeyForFrequencyResultSize() {
        return KEY_FOR_FREQUENCY_RESULT_SIZE;
    }

    @Override
    public String getKeyForFrequencyResultName() {
        return KEY_FOR_FREQUENCY_RESULT_NAME;
    }

    @Override
    public String getKeyForFrequencyResults() {
        return KEY_FOR_FREQUENCY_RESULTS;
    }


    @Override
    public String getKeyForFrequencyResultOverallResultsSize() {
        return KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_REALTED_PRODUCTS;
    }

    @Override
    public short getMaxNumberOfRelatedProductProperties() {
        return MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES;
    }

    @Override
    public short getMaxNumberOfRelatedProductsPerPurchase() {
        return MAX_NUMBER_OF_RELATED_PRODUCTS_PER_PURCHASE;
    }

    @Override
    public short getRelatedProductIdLength() {
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
    public short getRelatedProductAdditionalPropertyKeyLength() {
        return RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH;
    }

    @Override
    public short getRelatedProductAdditionalPropertyValueLength() {
        return RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH;
    }

    @Override
    public int getSizeOfIndexRequestQueue() {
        return SIZE_OF_INDEX_REQUEST_QUEUE;
    }
}
