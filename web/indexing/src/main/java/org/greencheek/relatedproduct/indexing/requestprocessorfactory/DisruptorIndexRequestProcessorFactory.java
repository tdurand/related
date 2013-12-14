package org.greencheek.relatedproduct.indexing.requestprocessorfactory;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverterFactory;
import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.requestprocessors.DisruptorBasedRelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.requestprocessors.RelatedProductIndexingMessageEventHandler;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a IndexRequestProcessorFactory that use the disruptor to accept indexing requests and
 * process them.  The given event handler more than likely does one of the two modes of request processing:
 *
 *
 * <pre>
 * 1)  ---->  Request --->  Ring buffer (to IndexingMessage)  --->  Storage repository
 *
 * OR
 *
 * 2)  ---->  Request --->  Ring buffer (to IndexingMessage)  --->  Ring Buffer (Reference) ---> Storage Repo
 *                                                            --->  Ring Buffer (Reference) ---> Storage Repo
 *                                                            --->  Ring Buffer (Reference) ---> Storage Repo
 * </pre>
 *
 * The choice between the two request processors will more than likely be done based upon the value set for
 * {@link org.greencheek.relatedproduct.util.config.Configuration#getNumberOfIndexingRequestProcessors()}
 *
 *
 */
public class DisruptorIndexRequestProcessorFactory implements IndexRequestProcessorFactory {
    private static final Logger log = LoggerFactory.getLogger(DisruptorIndexRequestProcessorFactory.class);


    private final IndexingRequestConverterFactory requestBytesConverter;
    private final  EventFactory<RelatedProductIndexingMessage> indexingMessageFactory;

    private final RelatedProductIndexingMessageEventHandler indexingEventHandler;

    public DisruptorIndexRequestProcessorFactory(IndexingRequestConverterFactory requestBytesConverter,
                                                 EventFactory<RelatedProductIndexingMessage> indexingMessageFactory,
                                                 RelatedProductIndexingMessageEventHandler indexingEventHandler) {
        this.requestBytesConverter = requestBytesConverter;
        this.indexingMessageFactory = indexingMessageFactory;
        this.indexingEventHandler = indexingEventHandler;
    }

    @Override
    public RelatedProductIndexRequestProcessor createProcessor(Configuration configuration) {
        return new DisruptorBasedRelatedProductIndexRequestProcessor(configuration,
                    requestBytesConverter,indexingMessageFactory,indexingEventHandler);

    }

    @Override
    public void shutdown() {
        try {
            indexingEventHandler.shutdown();
        } catch(Exception e) {
            log.error("Exception shutting down round robin event handler: {}", e.getMessage());
        }
    }
}
