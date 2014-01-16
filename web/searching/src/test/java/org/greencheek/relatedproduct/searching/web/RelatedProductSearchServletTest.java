package org.greencheek.relatedproduct.searching.web;

import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ListenableFuture;
import org.greencheek.relatedproduct.api.indexing.RelatedProduct;
import org.greencheek.relatedproduct.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.elastic.TransportBasedElasticSearchClientFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.requestprocessing.MultiMapSearchResponseContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.greencheek.relatedproduct.searching.web.bootstrap.SearchBootstrapApplicationCtx;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchFrequentlyRelatedProductSearchProcessor;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchRelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.util.elasticsearch.ElasticSearchServer;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.naming.resources.VirtualDirContext;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Integration test for testing that search requests can be executed
 */
public class RelatedProductSearchServletTest {

    private final String mWorkingDir = System.getProperty("java.io.tmpdir");

    private Tomcat tomcat;
    private AsyncHttpClient asyncHttpClient;
    private String searchurl;
    private Configuration configuration;

    private ElasticSearchServer server;
    private ElasticSearchClientFactory factory;
    RelatedProductSearchRepository<FrequentlyRelatedSearchResult[]> repository;



    @Before
    public void setUp() {
        String indexName = "relatedprog";
        System.setProperty("related-product.storage.index.name.prefix",indexName);
        // Set the clustername
        System.setProperty("related-product.storage.cluster.name", "relatedprogrammes");
        configuration = new SystemPropertiesConfiguration();

        // Start the Elastic Search Server
        server = new ElasticSearchServer(configuration.getStorageClusterName(),true);

        if(!server.isSetup()) throw new RuntimeException("ElasticSearch Not set");

        server.setIndexTemplate(indexName);

        // Create the client pointing to the above server
        System.setProperty("related-product.elastic.search.transport.hosts","localhost:" + server.getPort());
        configuration = new SystemPropertiesConfiguration();
        factory = new TransportBasedElasticSearchClientFactory(configuration);

        // Create the repo
        repository = new ElasticSearchRelatedProductSearchRepository(factory,new ElasticSearchFrequentlyRelatedProductSearchProcessor(configuration));

        try {
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-14",RELATED_CONTENT_BLADES1_PURCHASEa);
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-14",RELATED_CONTENT_BLADES1_PURCHASEb);
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-15",RELATED_CONTENT_BLADES2_PURCHASEa);
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-15",RELATED_CONTENT_BLADES2_PURCHASEb);


            assertEquals(2,server.getIndexCount());
            assertEquals(2,server.getDocCount(configuration.getStorageIndexNamePrefix()+"-2013-12-14"));
            assertEquals(2,server.getDocCount(configuration.getStorageIndexNamePrefix()+"-2013-12-15"));
        } catch(Exception e)  {
            fail("Cannot create test date for search test");
        }

        System.out.println("===========");
        System.out.println("Setup");
        System.out.println("===========");
    }

    @After
    public final void teardown() throws Throwable {
        System.clearProperty("related-product.size.of.related.content.search.request.queue");
        System.clearProperty("related-product.number.of.searching.request.processors");
        System.clearProperty("related-product.size.of.related.content.search.request.and.response.queue");
        System.clearProperty("related-product.size.of.response.processing.queue");

        factory.shutdown();


        if(server!=null) {
            server.shutdown();
        }


        try {
            shutdownTomcat();
        } catch (Exception e) {

        }

        if(asyncHttpClient!=null) {
            try {
                asyncHttpClient.close();
            } catch(Exception e) {

            }
        }


        System.clearProperty("related-product.storage.index.name.prefix");
        System.clearProperty("related-product.storage.cluster.name");
        System.clearProperty("related-product.elastic.search.transport.hosts");
        System.clearProperty("related-product.frequently.related.search.timeout.in.millis");



    }

    public final void shutdownTomcat() throws Exception  {
        if (tomcat.getServer() != null
                && tomcat.getServer().getState() != LifecycleState.DESTROYED) {
            if (tomcat.getServer().getState() != LifecycleState.STOPPED) {
                tomcat.stop();
            }
            tomcat.destroy();
        }
        tomcat.getServer().await();
    }



    protected int getTomcatPort() {
        return tomcat.getConnector().getLocalPort();
    }



    public void startTomcat(SearchBootstrapApplicationCtx bootstrapApplicationCtx) {
        String webappDirLocation = "src/main/webapp/";
        tomcat = new Tomcat();
        tomcat.setPort(0);
        tomcat.setBaseDir(mWorkingDir);
        tomcat.getHost().setAppBase(mWorkingDir);
        tomcat.getHost().setAutoDeploy(true);
        tomcat.getHost().setDeployOnStartup(true);

        Context ctx = tomcat.addContext(tomcat.getHost(),"/search","/");

        ((StandardContext)ctx).setProcessTlds(false);  // disable tld processing.. we don't use any
        ctx.addParameter("com.sun.faces.forceLoadConfiguration","false");

        Wrapper wrapper = tomcat.addServlet("/search","Searching","org.greencheek.relatedproduct.searching.web.RelatedProductSearchServlet");
        wrapper.setAsyncSupported(true);
        wrapper.addMapping("/frequentlyrelatedto/*");

        ctx.getServletContext().setAttribute(Configuration.APPLICATION_CONTEXT_ATTRIBUTE_NAME,bootstrapApplicationCtx);

        //declare an alternate location for your "WEB-INF/classes" dir:
        File additionWebInfClasses = new File("target/classes");
        VirtualDirContext resources = new VirtualDirContext();
        resources.setExtraResourcePaths("/WEB-INF/classes=" + additionWebInfClasses);
        ctx.setResources(resources);

        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        AsyncHttpClientConfig.Builder b = new AsyncHttpClientConfig.Builder();
        b.setRequestTimeoutInMs(10000);
        b.setConnectionTimeoutInMs(5000);
        b.setMaxRequestRetry(0);
        asyncHttpClient = new AsyncHttpClient(b.build());
        searchurl = "http://localhost:" + getTomcatPort() +"/search/frequentlyrelatedto";

    }


    /**
     * Test that with an empty id, a 400 is thrown by the servlet
     */
    @Test
    public void test400ReturnedForNoId() {
        System.setProperty("related-product.size.of.related.content.search.request.queue","1");
        System.setProperty("related-product.number.of.searching.request.processors", "1");

        TestBootstrapApplicationCtx bootstrap = getTestBootStrap();
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        sendGet(400, "");
    }

    /**
     * Test that indexing requests are sent to a single storage repository
     * i.e. traverses the ring buffer to the storage repo
     */
    @Test
    public void testSearchingSingleItemWithSingleRequestProcessorReturns200() {
        System.setProperty("related-product.size.of.related.content.search.request.queue","1");
        System.setProperty("related-product.number.of.searching.request.processors", "1");
//
//        final CountDownLatch latch = new CountDownLatch(3);
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap();
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        Response response1 = sendGet(200, "anchorman"); 
    }

    @Test
    public void testBufferSizeIsAdjustedToAPowerOf2() {
        System.setProperty("related-product.size.of.related.content.search.request.queue","10");
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap();
        assertEquals(16, bootstrap.getConfiguration().getSizeOfRelatedContentSearchRequestQueue());
    }

    @Test
    public void testBufferSizeIsKeptToSetPowerOf2() {
        System.setProperty("related-product.size.of.related.content.search.request.queue","16");
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap();
        assertEquals(16, bootstrap.getConfiguration().getSizeOfRelatedContentSearchRequestQueue());
    }

    /**
     * Test that indexing requests are sent to a single storage repository
     * i.e. traverses the ring buffer to the storage repo
     */
    @Test
    public void testSearchingMultipleRequestsReturns200WhenQueueNot() {
        System.setProperty("related-product.size.of.related.content.search.request.queue","10");
        System.setProperty("related-product.number.of.searching.request.processors", "2");
//
//        final CountDownLatch latch = new CountDownLatch(3);
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap();
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        List<ListenableFuture<Response>> resps = sendGet("anchorman",10);
        int oks = 0;
        int gatewayBusy = 0;
        int unknown = 0;
        for(ListenableFuture<Response> r : resps) {
            Response res = null;
            try {
                res = r.get();
                switch(res.getStatusCode()) {
                    case 503:
                        gatewayBusy++;
                        break;
                    case 200:
                        oks++;
                        break;
                    default:
                        unknown++;
                        break;
                }
            } catch(Exception e) {
                unknown++;
            }

        }

        assertEquals("1 Request should have been ok",10,oks);
        assertEquals("2 Requests should have been rejected",0,gatewayBusy);
        assertEquals("No requests should have failed with unexpected statuscode",0,unknown);
    }

    @Test
    public void testSearchingMultipleRequestsReturns503WhenQueueFull() {
        System.setProperty("related-product.size.of.related.content.search.request.queue","1");
        System.setProperty("related-product.number.of.searching.request.processors", "1");
//
//        final CountDownLatch latch = new CountDownLatch(3);
        TestBootstrapApplicationCtx bootstrap = getTestBootStrapSlowGet();
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        List<ListenableFuture<Response>> resps = sendGet("anchorman",3);
        int oks = 0;
        int gatewayBusy = 0;
        int unknown = 0;
        for(ListenableFuture<Response> r : resps) {
            Response res = null;
            try {
                res = r.get();
                switch(res.getStatusCode()) {
                    case 503:
                        gatewayBusy++;
                        break;
                    case 200:
                        oks++;
                        break;
                    default:
                        unknown++;
                        break;
                }
            } catch(Exception e) {
                unknown++;
            }

        }

        assertEquals("1 Request should have been ok",1,oks);
        assertEquals("2 Requests should have been rejected",2,gatewayBusy);
        assertEquals("No requests should have failed with unexpected statuscode",0,unknown);
    }
//
//        try {
//            boolean countedDown = latch.await(5000, TimeUnit.MILLISECONDS);
//            assertTrue("Storage Repository not called in required time",countedDown);
//
//        } catch (Exception e) {
//            fail("Storage Repository not called in required time");
//        }
//
//
//
//        int i = 0;
//        int reposCalled = 0;
//        for(TestRelatedProductStorageRepository repo : bootstrap.getRepository().getRepos()) {
//            i+= repo.getProductsRequestedToBeStored();
//            if(repo.getProductsRequestedToBeStored()>1) reposCalled++;
//        }
//
//        assertEquals(3,i);
//        assertEquals(1,reposCalled);


    /**
     * sends the indexing request and asserts that a http 202 was received.
     * @return
     */
    private Response sendGet(int statusCodeExpected,String id) {
        Response response=null;
        try {
            response = asyncHttpClient.prepareGet(searchurl+"/"+id).execute().get();
            assertEquals(statusCodeExpected, response.getStatusCode());
            return response;
        } catch (IOException e ) {
            fail(e.getMessage());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (ExecutionException e) {
            fail(e.getMessage());
        }
        return null;
    }

    private List<ListenableFuture<Response>> sendGet(String id, int numberOfRequests) {

        List<ListenableFuture<Response>> responses = new ArrayList<ListenableFuture<Response>>(numberOfRequests);
        try {
            for(int i =0;i<numberOfRequests;i++) responses.add(asyncHttpClient.prepareGet(searchurl+"/"+id).execute());

            return responses;
        } catch (IOException e ) {
            fail(e.getMessage());
        }
        return null;
    }


    private final static String RELATED_CONTENT_BLADES1_PURCHASEa = "{\n"+
            "\"id\": \"anchorman\",\n"+
            "\"date\": \"2013-12-14T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"blades of glory\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";

    private final static String RELATED_CONTENT_BLADES1_PURCHASEb = "{\n"+
            "\"id\": \"blades of glory\",\n"+
            "\"date\": \"2013-12-14T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"anchor man\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";

    private final static String RELATED_CONTENT_BLADES2_PURCHASEa = "{\n"+
            "\"id\": \"blades of glory\",\n"+
            "\"date\": \"2013-12-15T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"anchorman\",\"dodgeball\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";

    private final static String RELATED_CONTENT_BLADES2_PURCHASEb = "{\n"+
            "\"id\": \"anchorman\",\n"+
            "\"date\": \"2013-12-15T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"blades of glory\",\"dodgeball\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";



    public TestBootstrapApplicationCtx getTestBootStrap() {
        return new TestBootstrapApplicationCtx(false,false);
    }

    public TestBootstrapApplicationCtx getTestBootStrapSlowGet() {
        return new TestBootstrapApplicationCtx(true,false);
    }

    public TestBootstrapApplicationCtx getTestBootStrapSlowPut() {
        return new TestBootstrapApplicationCtx(false,true);
    }

    public class TestBootstrapApplicationCtx extends SearchBootstrapApplicationCtx {

        private final boolean slowGet;
        private final boolean slowPut;

        public TestBootstrapApplicationCtx(boolean slowGet,boolean slowPut) {
            super();
            this.slowGet = slowGet;
            this.slowPut = slowPut;
        }

        @Override
        public SearchResponseContextLookup getResponseContextLookup() {
            return new SlowMultiMapSearchResponseContextLookup(slowGet,slowPut,getConfiguration());
        }

    }

    public class SlowMultiMapSearchResponseContextLookup extends MultiMapSearchResponseContextLookup {

        private final long sleepTime;
        private final boolean slowGet;
        private final boolean slowPut;

        public SlowMultiMapSearchResponseContextLookup(boolean slowGet, boolean slowPut, Configuration config) {
            super(config);
            this.slowGet = slowGet;
            this.slowPut = slowPut;
            long sleepTime;
            try {
                sleepTime = Long.parseLong(System.getProperty("test.slow.repo.sleepTime","3000"));
            } catch(NumberFormatException e) {
                sleepTime = 2000;
            }
            this.sleepTime = sleepTime;

        }

        public SearchResponseContext[] removeContexts(SearchRequestLookupKey key) {
            try {
                if(slowPut) Thread.sleep(sleepTime);
            } catch (InterruptedException e) {

            }
            return super.removeContexts(key);
        }

        public boolean addContext(SearchRequestLookupKey key, SearchResponseContext[] context) {
            try {
                if(slowGet) Thread.sleep(sleepTime);
            } catch (InterruptedException e) {

            }
            return super.addContext(key,context);
        }
    }


}
