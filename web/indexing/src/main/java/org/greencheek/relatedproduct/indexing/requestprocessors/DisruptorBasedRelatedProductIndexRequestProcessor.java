package org.greencheek.relatedproduct.indexing.requestprocessors;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageFactory;
import org.greencheek.relatedproduct.indexing.*;
import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;


public class DisruptorBasedRelatedProductIndexRequestProcessor implements RelatedProductIndexRequestProcessor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRelatedProductIndexRequestProcessor.class);

    private final ExecutorService executorService = newSingleThreadExecutor();
    private final Disruptor<RelatedProductIndexingMessage> disruptor;

    private final IndexingRequestConverterFactory requestConverter;
    private final RelatedProductIndexingMessageEventHandler eventHandler;

    public DisruptorBasedRelatedProductIndexRequestProcessor(Configuration configuration,
                                                             IndexingRequestConverterFactory requestConverter,
                                                             EventFactory<RelatedProductIndexingMessage> indexingMessageFactory,
                                                             RelatedProductIndexingMessageEventHandler eventHandler
    ) {


        this.requestConverter = requestConverter;
        disruptor = new Disruptor<RelatedProductIndexingMessage>(
                indexingMessageFactory,
                Util.ceilingNextPowerOfTwo(configuration.getSizeOfIncomingMessageQueue()), executorService,
                ProducerType.MULTI, configuration.getWaitStrategyFactory().createWaitStrategy());

        this.eventHandler = eventHandler;
        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        disruptor.start();

    }

    @Override
    public void processRequest(Configuration config, ByteBuffer data) {
        try {
            disruptor.publishEvent(requestConverter.createConverter(config,data));
        } catch(InvalidIndexingRequestException e) {
            log.warn("Invalid json content, unable to process request.  Length of data:{}", data.remaining());

            if(log.isDebugEnabled()) {
                if(data.hasArray())
                    log.debug("Invalid content as byte array: {}", Arrays.toString(data.array()));
            }
        }
    }


    @PreDestroy
    public void shutdown() {

        try {
            log.info("Attempting to shut down executor thread pool in index request processor");
            executorService.shutdown();
        } catch (Exception e) {
            log.warn("Unable to shut down executor thread pool in index request processor",e);
        }

        log.info("Shutting down index request processor");
        try {
            log.info("Attempting to shut down disruptor in index request processor");
            disruptor.halt();
            disruptor.shutdown();
        } catch (Exception e) {
            log.warn("Unable to shut down disruptor in index request processor",e);
        }

        log.info("Shutting down round robin index request event handler");
        eventHandler.shutdown();


    }


}
