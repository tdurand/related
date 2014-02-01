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

package org.greencheek.related.searching.disruptor.responseprocessing;

import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.api.searching.lookup.SipHashSearchRequestLookupKey;
import org.greencheek.related.searching.RelatedItemSearchResultsToResponseGateway;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.searching.requestprocessing.*;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by dominictootell on 12/01/2014.
 */
public class DisruptorRelatedItemSearchResultsToResponseGatewayTest {

    RelatedItemSearchResultsToResponseGateway gateway;

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
        public List<SearchResponseContext> removeContexts(SearchRequestLookupKey key) {
            List<SearchResponseContext> holders = lookup.removeContexts(key);
            removeContextCount.incrementAndGet();
            removeContextExpected.countDown();
            return holders;
        }

        @Override
        public boolean addContext(SearchRequestLookupKey key, SearchResponseContext[] context) {
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

        gateway = new DisruptorRelatedItemSearchResultsToResponseGateway(new SystemPropertiesConfiguration(),
                new RequestSearchEventProcessor(contextLookup),
                new ResponseSearchEventProcessor(contextLookup,responseHandler));

        SearchResponseContext[] holder = new SearchResponseContext[] {
            LogDebuggingSearchResponseContext.INSTANCE};

        gateway.storeResponseContextForSearchRequest(new SipHashSearchRequestLookupKey("1"),holder);

        boolean added = contextLookup.waitOnAddContexts(1000);

        assertTrue(added);

        List<SearchResponseContext> holders = contextLookup.removeContexts(new SipHashSearchRequestLookupKey("1"));

        assertEquals(1, holders.size());

        assertSame(holders.get(0),holder[0]);

        contextLookup.reset(1,3);


        gateway.storeResponseContextForSearchRequest(new SipHashSearchRequestLookupKey("1"),holder);
        gateway.storeResponseContextForSearchRequest(new SipHashSearchRequestLookupKey("1"),holder);
        gateway.storeResponseContextForSearchRequest(new SipHashSearchRequestLookupKey("1"),holder);


        added = contextLookup.waitOnAddContexts(1000);

        assertTrue(added);

        holders = contextLookup.removeContexts(new SipHashSearchRequestLookupKey("1"));

        assertEquals(3, holders.size());

    }

    @Test
    public void testSendSearchResultsToResponseContexts() throws Exception {
        Configuration config = new SystemPropertiesConfiguration();
        RequestCountingContextLookup contextLookup = new RequestCountingContextLookup(new MultiMapSearchResponseContextLookup(config),1,1);
        ResponseEventHandler responseHandler = mock(ResponseEventHandler.class);

        gateway = new DisruptorRelatedItemSearchResultsToResponseGateway(new SystemPropertiesConfiguration(),
                new RequestSearchEventProcessor(contextLookup),
                new ResponseSearchEventProcessor(contextLookup,responseHandler));

        SearchResponseContextHolder holder = new SearchResponseContextHolder();


        gateway.storeResponseContextForSearchRequest(new SipHashSearchRequestLookupKey("1"),null);

        boolean added = contextLookup.waitOnAddContexts(1000);

        assertTrue(added);

        SearchResultEventWithSearchRequestKey results = new SearchResultEventWithSearchRequestKey(mock(SearchResultsEvent.class),new SipHashSearchRequestLookupKey("1"),0,0);

        gateway.sendSearchResultsToResponseContexts(new SearchResultEventWithSearchRequestKey[]{results});

        added = contextLookup.waitOnRemoveContexts(1000);

        assertTrue(added);

        assertEquals(0,contextLookup.removeContexts(new SipHashSearchRequestLookupKey("1")).size());


    }
}
