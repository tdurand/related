package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.api.searching.lookup.SipHashSearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsToResponseGateway;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.MultiMapSearchResponseContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

/**
 * Created by dominictootell on 12/01/2014.
 */
public class DisruptorRelatedProductSearchResultsToResponseGatewayTest {

    RelatedProductSearchResultsToResponseGateway gateway;

    @After
    public void tearDown() {
        if(gateway!=null) {
            gateway.shutdown();
        }
    }

    private static class RequestCountingContextLookup implements SearchResponseContextLookup {

        SearchResponseContextLookup lookup;
        CountDownLatch removeContextExpected;
        CountDownLatch addContextExpected;
        AtomicInteger removeContextCount = new AtomicInteger(0);
        AtomicInteger addContextCount  = new AtomicInteger(0);

        int removeExpected;
        int addExpected;

        public RequestCountingContextLookup(SearchResponseContextLookup origLookup,
                                            int expectedRemoves,
                                            int expectedAdds) {
            this.lookup = origLookup;
            this.addContextExpected = new CountDownLatch(expectedAdds);
            this.removeContextExpected = new CountDownLatch(expectedRemoves);
            removeExpected = expectedRemoves;
            addExpected = expectedAdds;

        }

        public boolean waitOnRemoveContexts(long millis) {
            try {
                return removeContextExpected.await(millis, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                return false;
            }
        }

        public boolean waitOnAddContexts(long millis) {
            try {
                return addContextExpected.await(millis,TimeUnit.MILLISECONDS);
            } catch(InterruptedException e) {
                return false;
            }
        }

        public int getAddCount() {
            return addContextCount.get();
        }

        public int getRemoveCount() {
            return removeContextCount.get();
        }

        @Override
        public SearchResponseContextHolder[] removeContexts(SearchRequestLookupKey key) {
            SearchResponseContextHolder[] holders = lookup.removeContexts(key);
            removeContextCount.incrementAndGet();
            removeContextExpected.countDown();
            return holders;
        }

        @Override
        public boolean addContext(SearchRequestLookupKey key, SearchResponseContextHolder context) {
            boolean addedNew = lookup.addContext(key, context);
            addContextCount.incrementAndGet();
            addContextExpected.countDown();
            return addedNew;
        }

        public void reset() {
            reset(removeExpected,addExpected);
        }

        public void reset(int removeExpected,int addExpected) {
            addContextCount.set(0);
            removeContextCount.set(0);
            addContextExpected = new CountDownLatch(addExpected);
            removeContextExpected = new CountDownLatch(removeExpected);
        }
    }



    @Test
    public void testStoreResponseContextForSearchRequest() throws Exception {
        Configuration config = new SystemPropertiesConfiguration();
        RequestCountingContextLookup contextLookup = new RequestCountingContextLookup(new MultiMapSearchResponseContextLookup(config),1,1);
        ResponseEventHandler responseHandler = mock(ResponseEventHandler.class);

        gateway = new DisruptorRelatedProductSearchResultsToResponseGateway(new SystemPropertiesConfiguration(),
                new RequestSearchEventProcessor(contextLookup),
                new ResponseSearchEventProcessor(contextLookup,responseHandler));

        SearchResponseContextHolder holder = new SearchResponseContextHolder();

        gateway.storeResponseContextForSearchRequest(new SipHashSearchRequestLookupKey("1"),holder);

        boolean added = contextLookup.waitOnAddContexts(1000);

        assertTrue(added);

        SearchResponseContextHolder[] holders = contextLookup.removeContexts(new SipHashSearchRequestLookupKey("1"));

        assertEquals(1, holders.length);

        assertSame(holders[0],holder);

        contextLookup.reset(1,3);


        gateway.storeResponseContextForSearchRequest(new SipHashSearchRequestLookupKey("1"),new SearchResponseContextHolder());
        gateway.storeResponseContextForSearchRequest(new SipHashSearchRequestLookupKey("1"),new SearchResponseContextHolder());
        gateway.storeResponseContextForSearchRequest(new SipHashSearchRequestLookupKey("1"),new SearchResponseContextHolder());


        added = contextLookup.waitOnAddContexts(1000);

        assertTrue(added);

        holders = contextLookup.removeContexts(new SipHashSearchRequestLookupKey("1"));

        assertEquals(3, holders.length);

    }

    @Test
    public void testSendSearchResultsToResponseContexts() throws Exception {
        Configuration config = new SystemPropertiesConfiguration();
        RequestCountingContextLookup contextLookup = new RequestCountingContextLookup(new MultiMapSearchResponseContextLookup(config),1,1);
        ResponseEventHandler responseHandler = mock(ResponseEventHandler.class);

        gateway = new DisruptorRelatedProductSearchResultsToResponseGateway(new SystemPropertiesConfiguration(),
                new RequestSearchEventProcessor(contextLookup),
                new ResponseSearchEventProcessor(contextLookup,responseHandler));

        SearchResponseContextHolder holder = new SearchResponseContextHolder();


        gateway.storeResponseContextForSearchRequest(new SipHashSearchRequestLookupKey("1"),holder);

        boolean added = contextLookup.waitOnAddContexts(1000);

        assertTrue(added);

        SearchResultEventWithSearchRequestKey results = new SearchResultEventWithSearchRequestKey(mock(SearchResultsEvent.class),new SipHashSearchRequestLookupKey("1"));

        gateway.sendSearchResultsToResponseContexts(new SearchResultEventWithSearchRequestKey[]{results});

        added = contextLookup.waitOnRemoveContexts(1000);

        assertTrue(added);

        assertEquals(0,contextLookup.removeContexts(new SipHashSearchRequestLookupKey("1")).length);


    }
}
