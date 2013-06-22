package org.greencheek.relatedproduct.searching.bootstrap;

import org.greencheek.relatedproduct.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.relatedproduct.searching.*;
import org.greencheek.relatedproduct.searching.disruptor.requestprocessing.DisruptorBasedSearchRequestProcessor;
import org.greencheek.relatedproduct.searching.disruptor.requestprocessing.RelatedContentSearchRequestProcessorHandlerFactory;
import org.greencheek.relatedproduct.searching.disruptor.requestprocessing.RoundRobinRelatedContentSearchRequestProcessorHandlerFactory;
import org.greencheek.relatedproduct.searching.disruptor.requestresponse.DisruptorBasedRequestResponseProcessor;
import org.greencheek.relatedproduct.searching.disruptor.requestresponse.DisruptorBasedSearchEventHandler;
import org.greencheek.relatedproduct.searching.disruptor.requestresponse.SearchEventHandler;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.DisruptorBasedResponseEventHandler;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.DisruptorBasedResponseProcessor;
import org.greencheek.relatedproduct.searching.disruptor.searchexecution.DisruptorBasedRelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.disruptor.searchexecution.RelatedProductSearchEventHandler;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchFrequentlyRelatedProductSearchProcessor;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchRelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.MapBasedSearchRequestParameterValidatorLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.MultiMapAsyncContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchRequestParameterValidatorLocator;
import org.greencheek.relatedproduct.searching.responseprocessing.HttpBasedRelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 10/06/2013
 * Time: 21:44
 * To change this template use File | Settings | File Templates.
 */
public class BootstrapApplicationContext implements ApplicationCtx {

    private final Configuration config;
    private final SearchRequestParameterValidatorLocator validatorLocator;
    private final RelatedContentSearchRequestProcessorHandlerFactory searchRequestProcessorHandlerFactory;

    public BootstrapApplicationContext() {
        this.config = new SystemPropertiesConfiguration();
        this.validatorLocator = new MapBasedSearchRequestParameterValidatorLookup(config);
        this.searchRequestProcessorHandlerFactory = new RoundRobinRelatedContentSearchRequestProcessorHandlerFactory();

    }

    public void shutdown() {

    }

    @Override
    public RelatedContentSearchRequestProcessorHandlerFactory getSearchRequestProcessingHandlerFactory() {
        return searchRequestProcessorHandlerFactory;
    }

    @Override
    public RelatedProductSearchRequestProcessor getRequestProcessor() {
       return new DisruptorBasedSearchRequestProcessor(getSearchRequestProcessingHandlerFactory().createHandler(config,this),
                                                       config,getSearchRequestParameterValidator());
    }

    @Override
    public Configuration getConfiguration() {
        return config;
    }

    @Override
    public SearchRequestParameterValidatorLocator getSearchRequestParameterValidator() {
        return this.validatorLocator;
    }




    @Override
    public AsyncContextLookup createAsyncContextLookup() {
        return new MultiMapAsyncContextLookup(config);
    }

    @Override
    public RelatedProductSearchResultsResponseProcessor createProcessorForSendingSearchResultsSendToClient() {
        return new DisruptorBasedResponseProcessor(new DisruptorBasedResponseEventHandler(config,new HttpBasedRelatedProductSearchResultsResponseProcessor()),config);

    }


    @Override
    public RelatedProductSearchRequestResponseProcessor createSearchRequestAndResponseGateway(AsyncContextLookup asyncContextStorage,
                                                                                              RelatedProductSearchResultsResponseProcessor responseProcessor) {

        SearchEventHandler searchEventHandler = new DisruptorBasedSearchEventHandler(config,asyncContextStorage,responseProcessor);
        return new DisruptorBasedRequestResponseProcessor(searchEventHandler,config);
    }

    @Override
    public RelatedProductSearchExecutor createSearchExecutor(RelatedProductSearchRequestResponseProcessor requestAndResponseGateway) {
        return new DisruptorBasedRelatedProductSearchExecutor(config,new RelatedProductSearchEventHandler(config,createSearchRepository(),requestAndResponseGateway));

    }

    @Override
    public RelatedProductSearchRepository createSearchRepository() {
        return new ElasticSearchRelatedProductSearchRepository(config,new NodeBasedElasticSearchClientFactory(config),new ElasticSearchFrequentlyRelatedProductSearchProcessor(config));
    }






}
