/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.indexing.elasticsearch;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.support.replication.ReplicationType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.elastic.http.*;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


public class ElasticSearchRelatedItemHttpIndexingRepository implements RelatedItemStorageRepository {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchRelatedItemHttpIndexingRepository.class);


    private final String indexType;
    private final String relatedWithAttributeName;
    private final boolean threadedIndexing;
    private final IndexRequest.OpType createOrIndex;

    private final String idAttributeName;
    private final String dateAttributeName;

    private final HttpElasticClient elasticClient;

    private final String indexingEndpoint;


    public ElasticSearchRelatedItemHttpIndexingRepository(Configuration configuration,
                                                          HttpElasticSearchClientFactory factory) {

        this.indexType = configuration.getStorageContentTypeName();
        this.idAttributeName = configuration.getKeyForIndexRequestIdAttr();
        this.dateAttributeName = configuration.getKeyForIndexRequestDateAttr();
        this.relatedWithAttributeName = configuration.getKeyForIndexRequestRelatedWithAttr();
        this.createOrIndex = configuration.getShouldReplaceOldContentIfExists() == true ? IndexRequest.OpType.INDEX : IndexRequest.OpType.CREATE;
        this.threadedIndexing = configuration.getShouldUseSeparateIndexStorageThread();
        this.elasticClient = factory.getClient();

        StringBuilder b = new StringBuilder(60);
        b.append("/_bulk?refresh=false&replication=async");
        indexingEndpoint = b.toString();
    }

    @Override
    public void store(RelatedItemStorageLocationMapper indexLocationMapper, List<RelatedItem> relatedItems) {
        StringBuilder jsonRequest = new StringBuilder(256*relatedItems.size());

        int requestAdded = 0;
        for(RelatedItem product : relatedItems) {
            requestAdded += addRelatedItem(indexLocationMapper, jsonRequest, product);
        }

        if(requestAdded>0) {
            log.info("Sending {} Relating Product Index Requests to Elastic:",requestAdded);
//            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
//
//            if(bulkResponse.hasFailures()) {
//                log.warn(bulkResponse.buildFailureMessage());
//            }
            String request = jsonRequest.toString();
            log.debug("Sending http request {}",request);

            HttpResult res = elasticClient.executeSearch(HttpMethod.POST,indexingEndpoint,request);
            if(res.getStatus()!= HttpSearchExecutionStatus.OK) {
                String responseString = res.getResult();
                if(responseString!=null) {
                    log.warn("Bulk indexing request failed: {} with response {}",request,responseString);
                } else {
                    log.warn("Bulk indexing request failed: {}",request);
                }
            }
        }
    }

    private int addRelatedItem(RelatedItemStorageLocationMapper indexLocationMapper,
                               StringBuilder bulkRequest,
                               RelatedItem product) {

        try {

            char[] id = product.getId();
            XContentBuilder builder = jsonBuilder().startObject()
                    .field(idAttributeName,id,0,id.length )
                    .field(dateAttributeName, product.getDate()).startArray(relatedWithAttributeName);

            for(char[] relatedIds : product.getRelatedItemPids()) {
                builder.value(new String(relatedIds, 0, relatedIds.length));
            }
            builder.endArray();

            RelatedItemAdditionalProperties properties = product.getAdditionalProperties();
            int maxNumberOfProperties = properties.getNumberOfProperties();
            for(int i=0;i<maxNumberOfProperties;i++) {
                char[] value = properties.getPropertyValueCharArray(i);
                builder.field(properties.getPropertyName(i), value, 0, value.length);
            }

            builder.endObject();

            if(createOrIndex== IndexRequest.OpType.INDEX) {
                bulkRequest.append("{\"index\":{");
            } else {
                bulkRequest.append("{\"create\":{");
            }
            bulkRequest.append("\"_index\":\"").append(indexLocationMapper.getLocationName(product));
            bulkRequest.append("\",\"_type\":\"").append(indexType);
            bulkRequest.append("\"}}\n");
            bulkRequest.append(builder.string()).append("\n");

            if(log.isDebugEnabled()) {
                log.debug("added indexing request to batch request: {}", builder.string());
            }

            return 1;
        } catch(IOException e) {
            return 0;
        }

    }

    @Override
    @PreDestroy
    public void shutdown() {
        log.debug("Shutting down ElasticSearchRelatedItemIndexingRepository");
        try {
            elasticClient.shutdown();
        } catch(Exception e) {

        }
    }
}
