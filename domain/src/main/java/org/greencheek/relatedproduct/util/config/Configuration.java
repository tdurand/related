package org.greencheek.relatedproduct.util.config;

import org.greencheek.relatedproduct.api.searching.SearchResultsOutcome;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 09:59
 * To change this template use File | Settings | File Templates.
 */
public interface Configuration {
    public static final String APPLICATION_CONTEXT_ATTRIBUTE_NAME = "ApplicationContext";

    public WaitStrategyFactory getWaitStrategyFactory();
    public int getNumberOfIndexingRequestProcessors();

    public int getMaxNumberOfRelatedProductProperties();
    public int getMaxNumberOfRelatedProductsPerPurchase();
    public int getRelatedProductIdLength();
    public String getRelatedProductInvalidIdString();
    public int getMinRelatedProductPostDataSizeInBytes();
    public int getMaxRelatedProductPostDataSizeInBytes();
    public int getRelatedProductAdditionalPropertyKeyLength();
    public int getRelatedProductAdditionalPropertyValueLength();

    /**
     * Can be used as a mechanism of whether or not a
     * @return
     */
    public boolean isSearchResponseDebugOutputEnabled();

    public int getIndexBatchSize();

    /**
     * The size of the ring buffer that batches the storage of index request to the
     * backend storage respository.
     *
     * client.
     * @return
     */
    public int getSizeOfBatchIndexingRequestQueue();

    /**
     * The size of the ring buffer that accepts incoming messages from the
     * client.
     * @return
     */
    public int getSizeOfIncomingMessageQueue();

    public int getMaxNumberOfSearchCriteriaForRelatedContent();
    public int getSizeOfRelatedContentSearchRequestHandlerQueue();
    public int getSizeOfRelatedContentSearchRequestQueue();
    public int getSizeOfRelatedContentSearchRequestAndResponseQueue();
    public int getSizeOfResponseProcessingQueue();
    public int getNumberOfSearchingRequestProcessors();

    public int getNumberOfExpectedLikeForLikeRequests();

    public String getKeyForIndexRequestRelatedWithAttr();

    public String getKeyForFrequencyResults();
    public String getKeyForFrequencyResultOccurrence();
    public String getKeyForFrequencyResultId();
    public String getKeyForFrequencyResultOverallResultsSize();

    public String getKeyForIndexRequestDateAttr();
    public String getKeyForIndexRequestIdAttr();
    public String getKeyForIndexRequestProductArrayAttr();


    public String getRequestParameterForSize();
    public String getRequestParameterForId();
    public int getDefaultNumberOfResults();

    public String getStorageIndexNamePrefix();
    public String getStorageIndexNameAlias();
    public String getStorageContentTypeName();
    public String getStorageClusterName();
    public String getStorageFrequentlyRelatedProductsFacetResultsFacetName();

    public String getElasticSearchClientDefaultNodeSettingFileName();
    public String getElasticSearchClientDefaultTransportSettingFileName();
    public String getElasticSearchClientOverrideSettingFileName();


    public String getStorageLocationMapper();

    public long getFrequentlyRelatedProductsSearchTimeoutInMillis();


    public int getResponseCode(SearchResultsOutcome type);

    public int getTimedOutSearchRequestStatusCode();
    public int getFailedSearchRequestStatusCode();
    public int getNoFoundSearchResultsStatusCode();
    public int getFoundSearchResultsStatusCode();


    public String getPropertyEncoding();

    boolean isIndexNameDateCachingEnabled();
    public int getNumberOfIndexNamesToCache();

    public boolean getShouldReplaceOldContentIfExists();

    public boolean getShouldUseSeparateIndexStorageThread();

    boolean shouldDiscardIndexRequestWithTooManyRelations();

    ElasticeSearchClientType getElasticSearchClientType();

    public String getElasticSearchTransportHosts();

    int getDefaultElasticSearchPort();

    public enum ElasticeSearchClientType {
        NODE,
        TRANSPORT;
    }

    public boolean useSharedSearchRepository();

    /**
     * Is it safe to output the indexing request data sent by the client
     * @return
     */
    public boolean isSafeToOutputRequestData();

    /**
     * provides a repository specific execution hint for
     * when faceting is performed.
     */
    public String getStorageFacetExecutionHint();
}
