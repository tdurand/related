package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.searching.domain.api.ResponseEvent;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Provides an asychronous way that the response can be generated and sent to the waiting users
 * without tying up the request thread.
 *
 */
public class DisruptorBasedResponseProcessor implements RelatedProductSearchResultsResponseProcessor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedResponseProcessor.class);

    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final ExecutorService executorService = newSingleThreadExecutor();
    private final Disruptor<ResponseEvent> disruptor;
    private final RingBuffer<ResponseEvent> ringBuffer;



    public DisruptorBasedResponseProcessor(ResponseEventHandler eventHandler,
                                           Configuration configuration
    ) {
        disruptor = new Disruptor<ResponseEvent>(
                ResponseEvent.FACTORY,
                configuration.getSizeOfResponseProcessingQueue(), executorService,
                ProducerType.SINGLE, new SleepingWaitStrategy());
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());

        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        ringBuffer = disruptor.start();


    }


    @Override
    public void processSearchResults(SearchResponseContextHolder[] contexts, SearchResultsEvent results) {
        ringBuffer.publishEvent(SearchResultsToResponseEventTranslator.INSTANCE,contexts,results);
    }




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
