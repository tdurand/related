package org.greencheek.relatedproduct.searching.bootstrap;


import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchFactory;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.searching.*;
import org.greencheek.relatedproduct.searching.disruptor.requestprocessing.RelatedContentSearchRequestProcessorHandlerFactory;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequestFactory;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchRequestParameterValidatorLocator;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverterFactory;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
public interface ApplicationCtx
{
    public RelatedProductSearchRequestProcessor getRequestProcessor();
    public Configuration getConfiguration();
    public RelatedContentSearchRequestProcessorHandlerFactory getSearchRequestProcessingHandlerFactory();
    public SearchRequestParameterValidatorLocator getSearchRequestParameterValidator();


    public SearchResponseContextLookup createAsyncContextLookup();
    public RelatedProductSearchResultsResponseProcessor createProcessorForSendingSearchResultsSendToClient(SearchResponseContextLookup asyncContextStorage);

    public RelatedProductSearchResponseProcessor createSearchRequestAndResponseGateway(SearchResponseContextLookup asyncContextStorage,
                                                                                              RelatedProductSearchResultsResponseProcessor responseProcessor);

    /**
     * Creates the executor that is responsible for taking search requests, executing them,
     * and sending the results onwards for processing.
     */
    public RelatedProductSearchExecutor createSearchExecutor(RelatedProductSearchResultsResponseProcessor requestAndResponseGateway);

    /**
     * Class that physically performs the search requests, and marshalls the incoming results
     * back into the domain
     * @return
     */
    public RelatedProductSearchRepository createSearchRepository();

    public SearchResultsConverterFactory createSearchResultsConverterFactory();

    /**
     * Creates the RelatedProductSearchRequest factory
     */
    public RelatedProductSearchRequestFactory createRelatedSearchRequestFactory();


    /**
     * Creates the search request key factory.  This factory is responsible for  creating
     * search request lookup keys, that are based on user requests.
     */
    public SearchRequestLookupKeyFactory createSearchRequestLookupKeyFactory();


    /**
     *
     * @return
     */
    public EventFactory<RelatedProductSearch> createRelatedProductSearchEventFactory();


    /**
     * Returns the factory that is response for creating and populating RelatedProductSearch
     * objects.
     */
    public RelatedProductSearchFactory createRelatedProductSearchFactory();

    public void shutdown();

}
