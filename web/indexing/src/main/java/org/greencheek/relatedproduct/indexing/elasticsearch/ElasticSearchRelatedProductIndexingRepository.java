package org.greencheek.relatedproduct.indexing.elasticsearch;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.greencheek.relatedproduct.domain.RelatedProduct;
import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 22:16
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchRelatedProductIndexingRepository implements RelatedProductStorageRepository {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchRelatedProductIndexingRepository.class);


    private final String indexType;
    private final String relatedWithAttributeName;

    private final String idAttributeName;
    private final String dateAttributeName;

    private final ElasticSearchClientFactory elasticSearchClientFactory;
    private final Client elasticClient;


    public ElasticSearchRelatedProductIndexingRepository(Configuration configuration,
                                                         ElasticSearchClientFactory factory) {

        this.indexType = configuration.getStorageContentTypeName();
        this.idAttributeName = configuration.getKeyForIndexRequestIdAttr();
        this.dateAttributeName = configuration.getKeyForIndexRequestDateAttr();
        this.relatedWithAttributeName = configuration.getKeyForIndexRequestRelatedWithAttr();
        this.elasticSearchClientFactory = factory;
        this.elasticClient = elasticSearchClientFactory.getClient();
    }

    @Override
    public void store(RelatedProductStorageLocationMapper indexLocationMapper, RelatedProduct... relatedProducts) {
        BulkRequestBuilder bulkRequest = elasticClient.prepareBulk();


        int requestAdded = 0;
        for(RelatedProduct product : relatedProducts) {
            requestAdded += addRelatedProduct(indexLocationMapper,bulkRequest,product);
        }

        if(requestAdded>0) {
            log.info("Sending Relating Product Index Requests to Elastic: {}",requestAdded);
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();

            if(bulkResponse.hasFailures()) {
                log.warn(bulkResponse.buildFailureMessage());
            }
        }
    }

    private int addRelatedProduct(RelatedProductStorageLocationMapper indexLocationMapper,
                                      BulkRequestBuilder bulkRequest,
                                      RelatedProduct product) {

        try {

            char[] id = product.getId();
            XContentBuilder builder = jsonBuilder().startObject()
                    .field(idAttributeName,id,0,id.length )
                    .field(dateAttributeName, product.getDate())
                    .array(relatedWithAttributeName, product.getRelatedProductPids());

            for(String[] properties : product.getAdditionalProperties()) {
                builder.field(properties[0],properties[1]);
            }

            builder.endObject();

            IndexRequestBuilder indexRequestBuilder = elasticClient.prepareIndex(indexLocationMapper.getLocationName(product), indexType);

            bulkRequest.add(indexRequestBuilder.setSource(builder));

            return 1;
        } catch(IOException e) {
            return 0;
        }

    }

    @Override
    @PreDestroy
    public void shutdown() {
        log.debug("Shutting down ElasticSearchRelatedProductIndexingRepository");
        try {
            elasticSearchClientFactory.shutdown();
        } catch(Exception e) {

        }
    }
}
