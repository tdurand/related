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

import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.related.elastic.TransportBasedElasticSearchClientFactory;
import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.greencheek.related.indexing.RelatedItemStorageRepositoryFactory;
import org.greencheek.related.util.config.Configuration;


/**
 * Creates a new elastic search storage repository (client connection to an elastic search cluster) when
 * {@link #getRepository(org.greencheek.related.util.config.Configuration)} is called.
 */
public class ElasticSearchRelatedItemStorageRepositoryFactory implements RelatedItemStorageRepositoryFactory {

    private final Configuration configuration;

    public ElasticSearchRelatedItemStorageRepositoryFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public RelatedItemStorageRepository getRepository(Configuration configuration) {
        ElasticSearchClientFactory factory;
        switch(configuration.getElasticSearchClientType()) {
            case NODE:
                factory = new NodeBasedElasticSearchClientFactory(configuration);
                break;
            case TRANSPORT:
                factory = new TransportBasedElasticSearchClientFactory(configuration);
                break;
            default:
                factory = new TransportBasedElasticSearchClientFactory(configuration);
                break;
        }

        return new ElasticSearchRelatedItemIndexingRepository(configuration,factory);
    }
}
