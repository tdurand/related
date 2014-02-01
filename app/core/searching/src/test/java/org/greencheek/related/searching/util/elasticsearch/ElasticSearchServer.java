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

package org.greencheek.related.searching.util.elasticsearch;

import com.github.tlrx.elasticsearch.test.EsSetup;
import com.github.tlrx.elasticsearch.test.provider.LocalClientProvider;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequestBuilder;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.io.FileSystemUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.github.tlrx.elasticsearch.test.EsSetup.deleteAll;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 15/12/2013
 * Time: 20:46
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchServer {

    private static final String TYPE = "related";
    private final int port;
    private final EsSetup esSetup;
    private final Client esClient;
    private final boolean transportClient;
    private final boolean setup;
    private final String tmpDir;


    public ElasticSearchServer(String clustername, boolean transportClient) {
        String tmpDirectory =  System.getProperty("java.io.tmpdir");
        String fileSep = System.getProperty("file.separator");

        if(!tmpDirectory.endsWith(fileSep)) tmpDirectory += fileSep;
        tmpDirectory += UUID.randomUUID().toString() + fileSep;

        tmpDir = tmpDirectory;
        this.transportClient = transportClient;
        EsSetup esSetup = null;
        Client theClient = null;
        boolean setupOk = true;


        int port = -1;

        if(transportClient) {
            try {
                port = findFreePort();
            } catch (Exception e) {
                setupOk = false;
                port = -1;
            } finally {
                this.port = port;
            }
        } else {
            this.port = -1;
        }

        try {
            ImmutableSettings.Builder b = ImmutableSettings.settingsBuilder()
                    .put("cluster.name", clustername)
                    .put("index.store.type", "memory")
                    .put("index.store.fs.memory.enabled", "true")
                    .put("gateway.type", "local")
                    .put("index.number_of_shards", "1")
                    .put("index.number_of_replicas", "0")
                    .put("cluster.routing.schedule", "50ms")
                    .put("node.data", true)
                    .put("node.client",false)
                    .put("node.master", true)
                    .put("discovery.zen.ping.multicast.enabled", "false")
                    .put("discovery.zen.ping.multicast.ping.enabled","false")
                    .put("discovery.zen.ping.unicast.enabled", "true")
                    .put("discovery.zen.ping.unicast.hosts", "127.0.0.1[12345-23456]")
                    .put("path.data", tmpDir+"data")
                    .put("path.work", tmpDir+"work")
                    .put("path.logs", tmpDir+"logs")
                    .put("index.number_of_shards", "1")
                    .put("index.number_of_replicas", "0")
                    .put("cluster.routing.schedule", "50ms")
                    .put("http.enabled", false);

            if(transportClient) {
                b.put("node.local", false)
                        .put("transport.tcp.port", port)
                        .put("network.host","127.0.0.1");

            } else {
                b.put("node.local", true);
            }

            esSetup = new CustomEsSetup(b.build());


            try {
                if(setupOk) {
                    esSetup.execute( deleteAll() );
                    theClient = esSetup.client();
                    setIndexTemplate(esSetup.client());
                    setupOk = true;
                }
            } catch (Exception e) {
                setupOk = false;
            }
        } finally {
            this.esSetup = esSetup;
            this.setup = setupOk;
            this.esClient = theClient;
        }

    }

    public void shutdown() {
        if(esSetup!=null) {
           try {
               esSetup.terminate();
           } catch(Exception e) {

           }
        }
        FileSystemUtils.deleteRecursively(new File(tmpDir), true);
    }

    /**
     *
     * @return -1 if no port
     */
    public int getPort() {
        return port;
    }

    public boolean isSetup() {
        return setup;
    }

    private class CustomEsSetup extends EsSetup {

        CustomEsSetup(Settings settings) {
            super(new CustomLocalClientProvider(settings));
        }
    }

    private class CustomLocalClientProvider extends LocalClientProvider {

        private final Settings settings;
        public CustomLocalClientProvider(Settings settings) {
            this.settings = settings;
        }
        @Override
        protected Settings buildNodeSettings() {
            return settings;
        }
    }

    public static int findFreePort()
            throws IOException {
        ServerSocket server =
                new ServerSocket(0);
        int port = server.getLocalPort();
        server.close();
        return port;
    }

    public boolean createIndex(String indexName) {
        try {
            CreateIndexResponse response = esClient.admin().indices().create(new CreateIndexRequest(indexName)).actionGet(10000, TimeUnit.MILLISECONDS);
            ClusterHealthRequestBuilder healthRequest = esClient.admin().cluster().prepareHealth();
            healthRequest.setIndices(indexName); // only request health of this index...
            healthRequest.setWaitForYellowStatus();
            ClusterHealthResponse healthResponse = healthRequest.execute().actionGet();
            System.out.println("status:" + healthResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            esClient.admin().indices().refresh(new RefreshRequest(indexName).force(true)).actionGet(10000, TimeUnit.MILLISECONDS);
            esClient.admin().indices().flush(new FlushRequest(indexName).force(true)).actionGet(10000, TimeUnit.MILLISECONDS);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean indexDocument(String indexName,String doc) {
        try {
            indexDocument(indexName, TYPE,doc);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean indexDocument(String indexName,String type, String doc) {
        try {
            IndexResponse res = esClient.index(new IndexRequest().index(indexName).type(type).source(doc)).actionGet(2000, TimeUnit.MILLISECONDS);
            esClient.admin().indices().refresh(new RefreshRequest(indexName).force(true)).actionGet(2000, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getIndexCount() {
        try {
            IndicesStatsResponse response = esClient.admin().indices().stats(new IndicesStatsRequest()).actionGet();
            return response.getIndices().size();
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getDocCount(String indexName) {
        try {
            IndicesStatsResponse response = esClient.admin().indices().stats(new IndicesStatsRequest()).actionGet();
            return (int)response.getIndex(indexName).getTotal().docs.getCount();
        } catch(Exception e ) {
            e.printStackTrace();
            return -1;
        }
    }

    private void setIndexTemplate(Client esClient) {

        esClient.admin().indices().putTemplate(new PutIndexTemplateRequest("relateditems").source("" +
                "{\n" +
                "    \"template\" : \"relateditems*\",\n" +
                "    \"settings\" : {\n" +
                "        \"number_of_shards\" : 1,\n" +
                "        \"number_of_replicas\" : 0,\n" +
                "        \"index.cache.field.type\" : \"soft\",\n" +
                "        \"index.refresh_interval\" : \"5s\",\n" +
                "        \"index.store.compress.stored\" : true,\n" +
                "        \"index.query.default_field\" : \"id\",\n" +
                "        \"index.routing.allocation.total_shards_per_node\" : 3\n" +
                "    },\n" +
                "    \"mappings\" : {\n" +
                "        \"related\" : {\n" +
                "           \"_all\" : {\"enabled\" : false},\n" +
                "           \"properties\" : {              \n" +
                "              \"id\": { \"type\": \"string\", \"index\": \"not_analyzed\" },\n" +
                "              \"related-with\": { \"type\": \"string\", \"index\": \"not_analyzed\" },              \n" +
                "              \"date\": { \"type\": \"date\", \"index\": \"not_analyzed\" },\n" +
                "              \"channel\" : {\"type\" : \"string\" , \"index\" : \"not_analyzed\" },\n" +
                "              \"site\" : {\"type\" : \"string\" , \"index\" : \"not_analyzed\" },\n" +
                "              \"title\" : {\"type\" : \"string\" , \"index\" : \"not_analyzed\" },\n" +
                "              \"type\" : {\"type\" : \"string\" , \"index\" : \"not_analyzed\" } \n" +
                "           }   \n" +
                "        }\n" +
                "   }\n" +
                "}")).actionGet();

    }

    public void setIndexTemplate(String indexName) {
        try {
            esClient.admin().indices().putTemplate(new PutIndexTemplateRequest(indexName).source("" +
                    "{\n" +
                    "    \"template\" : \""+indexName+"*\",\n" +
                    "    \"settings\" : {\n" +
                    "        \"number_of_shards\" : 1,\n" +
                    "        \"number_of_replicas\" : 0,\n" +
                    "        \"index.cache.field.type\" : \"soft\",\n" +
                    "        \"index.refresh_interval\" : \"5s\",\n" +
                    "        \"index.store.compress.stored\" : true,\n" +
                    "        \"index.query.default_field\" : \"id\",\n" +
                    "        \"index.routing.allocation.total_shards_per_node\" : 3\n" +
                    "    },\n" +
                    "    \"mappings\" : {\n" +
                    "        \"related\" : {\n" +
                    "           \"_all\" : {\"enabled\" : false},\n" +
                    "           \"properties\" : {              \n" +
                    "              \"id\": { \"type\": \"string\", \"index\": \"not_analyzed\"},\n" +
                    "              \"related-with\": { \"type\": \"string\", \"index\": \"not_analyzed\" },              \n" +
                    "              \"date\": { \"type\": \"date\", \"index\": \"not_analyzed\" },\n" +
                    "              \"channel\" : {\"type\" : \"string\" , \"index\" : \"not_analyzed\" },\n" +
                    "              \"site\" : {\"type\" : \"string\" , \"index\" : \"not_analyzed\" },\n" +
                    "              \"title\" : {\"type\" : \"string\" , \"index\" : \"not_analyzed\" },\n" +
                    "              \"type\" : {\"type\" : \"string\" , \"index\" : \"not_analyzed\" } \n" +
                    "           } \n" +
                    "        }\n" +
                    "   }\n" +
                    "}")).actionGet();
        } catch (Exception e) {
            e.printStackTrace();;
        }

    }

}
