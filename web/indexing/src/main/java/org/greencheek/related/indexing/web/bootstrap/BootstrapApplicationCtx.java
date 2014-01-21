package org.greencheek.related.indexing.web.bootstrap;

import org.greencheek.related.api.indexing.BasicRelatedItemIndexingMessageConverter;
import org.greencheek.related.api.indexing.RelatedItemIndexingMessageConverter;
import org.greencheek.related.api.indexing.RelatedItemIndexingMessageFactory;
import org.greencheek.related.api.indexing.RelatedItemReferenceMessageFactory;
import org.greencheek.related.indexing.IndexingRequestConverterFactory;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.RelatedItemStorageRepositoryFactory;
import org.greencheek.related.indexing.elasticsearch.ElasticSearchRelatedItemStorageRepositoryFactory;
import org.greencheek.related.indexing.jsonrequestprocessing.JsonSmartIndexingRequestConverterFactory;
import org.greencheek.related.indexing.locationmappers.DayBasedStorageLocationMapper;
import org.greencheek.related.indexing.locationmappers.HourBasedStorageLocationMapper;
import org.greencheek.related.indexing.locationmappers.MinuteBasedStorageLocationMapper;
import org.greencheek.related.indexing.requestprocessorfactory.DisruptorIndexRequestProcessorFactory;
import org.greencheek.related.indexing.requestprocessorfactory.IndexRequestProcessorFactory;
import org.greencheek.related.indexing.requestprocessors.RelatedItemIndexingMessageEventHandler;
import org.greencheek.related.indexing.requestprocessors.multi.disruptor.BatchingRelatedItemReferenceEventHanderFactory;
import org.greencheek.related.indexing.requestprocessors.multi.disruptor.RelatedItemReferenceEventHandlerFactory;
import org.greencheek.related.indexing.requestprocessors.multi.disruptor.RoundRobinRelatedItemIndexingMessageEventHandler;
import org.greencheek.related.indexing.requestprocessors.single.disruptor.SingleRelatedItemIndexingMessageEventHandler;
import org.greencheek.related.indexing.util.JodaISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.related.indexing.util.JodaUTCCurrentDateAndHourAndMinuteFormatter;
import org.greencheek.related.indexing.util.JodaUTCCurrentDateAndHourFormatter;
import org.greencheek.related.indexing.util.JodaUTCCurrentDateFormatter;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;


/**
 * Basic Bootstrap, that performs the wiring up of application.
 * It's the class that performs the dependency injection
 */
public class BootstrapApplicationCtx implements ApplicationCtx {

    private final Configuration applicationConfiguration;
    private volatile IndexRequestProcessorFactory indexingRequestProcessingFactory;

    public BootstrapApplicationCtx()
    {
        this.applicationConfiguration = createConfiguration();
    }

    public RelatedItemReferenceMessageFactory createRelatedItemReferenceFactory() {
        return new RelatedItemReferenceMessageFactory();
    }


    public RelatedItemReferenceEventHandlerFactory createRelatedItemReferenceFactory(Configuration applicationConfiguration,
                                                                                     RelatedItemStorageLocationMapper locationMapper,
                                                                                     RelatedItemStorageRepositoryFactory repoFactory) {

        return new BatchingRelatedItemReferenceEventHanderFactory(applicationConfiguration,repoFactory,locationMapper);
    }

    public RelatedItemStorageLocationMapper createIndexNameLocationMapper(Configuration applicationConfiguration) {

        RelatedItemStorageLocationMapper locationMapper;

        String storageLocationMapperType = applicationConfiguration.getStorageLocationMapper();
        if(storageLocationMapperType.equalsIgnoreCase("day")) {
            locationMapper = new DayBasedStorageLocationMapper(applicationConfiguration, new JodaUTCCurrentDateFormatter());
        } else if(storageLocationMapperType.equalsIgnoreCase("hour")) {
            locationMapper = new HourBasedStorageLocationMapper(applicationConfiguration, new JodaUTCCurrentDateAndHourFormatter());
        } else {
            locationMapper = new MinuteBasedStorageLocationMapper(applicationConfiguration, new JodaUTCCurrentDateAndHourAndMinuteFormatter());
        }

        return locationMapper;
    }

    public RelatedItemIndexingMessageConverter createIndexingMessageToRelatedItem(Configuration applicationConfiguration) {
        return new BasicRelatedItemIndexingMessageConverter(applicationConfiguration);
    }

    public RelatedItemIndexingMessageFactory createIndexingMessageFactory() {
        return new RelatedItemIndexingMessageFactory(applicationConfiguration);

    }

    public IndexingRequestConverterFactory createBytesToIndexingMessageConverterFactory() {
        return new JsonSmartIndexingRequestConverterFactory(new JodaISO8601UTCCurrentDateAndTimeFormatter());
    }

    public Configuration createConfiguration() {
         return new SystemPropertiesConfiguration();
    }

    /**
     * Returns the factory that is responsible for creating the backend storage repository objects, that
     * basically store the {@link org.greencheek.related.api.indexing.RelatedItemIndexingMessage}
     */
    public RelatedItemStorageRepositoryFactory getStorageRepositoryFactory(Configuration applicationConfiguration) {
        return new ElasticSearchRelatedItemStorageRepositoryFactory(applicationConfiguration);
    }

   /**
    * chooses between the backend processing that is done to turn the request data into a
    * {@link org.greencheek.related.api.indexing.RelatedItemIndexingMessage}
    * that is stored in the backend.
    *
    * The processing is in either one of two ways:
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
    * The choice between the two request processor is done based upon the value set for {@link org.greencheek.related.util.config.Configuration#getNumberOfIndexingRequestProcessors()}
    */
    @Override
    public synchronized IndexRequestProcessorFactory getIndexRequestProcessorFactory() {
        if(indexingRequestProcessingFactory==null) {
            RelatedItemStorageLocationMapper locationMapper = createIndexNameLocationMapper(applicationConfiguration);
            RelatedItemStorageRepositoryFactory repoFactory = getStorageRepositoryFactory(applicationConfiguration);
            RelatedItemReferenceEventHandlerFactory factory = createRelatedItemReferenceFactory(applicationConfiguration, locationMapper, repoFactory);
            RelatedItemIndexingMessageConverter indexingMessageToRelatedItemsConverter = createIndexingMessageToRelatedItem(applicationConfiguration);

            RelatedItemIndexingMessageEventHandler eventHandler = null;

            if(applicationConfiguration.getNumberOfIndexingRequestProcessors()>1) {
                eventHandler = new RoundRobinRelatedItemIndexingMessageEventHandler(applicationConfiguration,
                    indexingMessageToRelatedItemsConverter, createRelatedItemReferenceFactory(),factory);
            } else {
                eventHandler = new SingleRelatedItemIndexingMessageEventHandler(applicationConfiguration,
                    indexingMessageToRelatedItemsConverter,repoFactory.getRepository(applicationConfiguration),locationMapper);
            }

            this.indexingRequestProcessingFactory = new DisruptorIndexRequestProcessorFactory(createBytesToIndexingMessageConverterFactory(),
                    createIndexingMessageFactory(),eventHandler);
        }

        return indexingRequestProcessingFactory;
    }

    @Override
    public Configuration getConfiguration() {
        return applicationConfiguration;
    }
}
