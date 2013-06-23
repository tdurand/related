package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.searching.domain.api.SearchEvent;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessor;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.servlet.AsyncContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:14
 * To change this template use File | Settings | File Templates.
 */
public class DisruptorBasedRequestResponseProcessor implements RelatedProductSearchRequestResponseProcessor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRequestResponseProcessor.class);

    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final ExecutorService executorService = newSingleThreadExecutor();
    private final Disruptor<SearchEvent> disruptor;

    private final Configuration configuration;


    public DisruptorBasedRequestResponseProcessor(EventHandler<SearchEvent>  eventHandler,
                                                  Configuration configuration
    ) {
        this.configuration = configuration;
        disruptor = new Disruptor<SearchEvent>(
                SearchEvent.FACTORY,
                configuration.getSizeOfRelatedContentSearchRequestAndResponseQueue(), executorService,
                ProducerType.MULTI, new SleepingWaitStrategy());
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());

        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        disruptor.start();

    }

    @Override
    public void handleRequest(SearchRequestLookupKey requestKey, AsyncContext requestCtx) {
        disruptor.publishEvent(new SearchRequestTranslator(requestKey,requestCtx));
    }

    @Override
    public void handleResponse(SearchRequestLookupKey key, SearchResultsConverter result) {
        disruptor.publishEvent(new SearchResultsTranslator(key,result));
    }

    @PreDestroy
    public void shutdown() {
        if(shutdown.compareAndSet(false,true)) {
            try {
                log.info("Attempting to shut down executor thread pool in search request/response processor");
                executorService.shutdown();
            } catch (Exception e) {
                log.warn("Unable to shut down executor thread pool in search request/response processor",e);
            }

            log.info("Shutting down index request processor");
            try {
                log.info("Attempting to shut down disruptor in search request/response processor");
                disruptor.halt();
                disruptor.shutdown();
            } catch (Exception e) {
                log.warn("Unable to shut down disruptor in search request/response processor",e);
            }
        }

    }

}