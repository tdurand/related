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

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;
import org.greencheek.related.util.arrayindexing.Util;
import org.greencheek.related.util.concurrency.DefaultNameableThreadFactory;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newSingleThreadExecutor;


/**
 * Uses a ringbuffer as means of batching, and asychronously sending results to awaiting
 * clients.
 */
public class DisruptorBasedResponseContextTypeBasedResponseEventHandler implements ResponseEventHandler {


    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedResponseContextTypeBasedResponseEventHandler.class);
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final ExecutorService executorService;
    private final Disruptor<SearchResultsToDistributeToResponseContexts> disruptor;
    private final RingBuffer<SearchResultsToDistributeToResponseContexts> ringBuffer;

    private final ResponseEventHandler delegateHandler;

    private final EventHandler<SearchResultsToDistributeToResponseContexts> EVENT_HANDLER = new EventHandler<SearchResultsToDistributeToResponseContexts>() {
        @Override
        public void onEvent(SearchResultsToDistributeToResponseContexts event, long sequence, boolean endOfBatch) throws Exception {
            delegateHandler.handleResponseEvents(event.getSearchResultsEvents(),event.getResponseContexts());
            event.setResponseContexts(null);
            event.setSearchResultsEvents(null);
        }
    };

    EventTranslatorTwoArg<SearchResultsToDistributeToResponseContexts,SearchResultEventWithSearchRequestKey[],List<List<SearchResponseContext>>> translator = new EventTranslatorTwoArg<SearchResultsToDistributeToResponseContexts,SearchResultEventWithSearchRequestKey[],List<List<SearchResponseContext>>>() {
        @Override
        public void translateTo(SearchResultsToDistributeToResponseContexts event, long sequence, SearchResultEventWithSearchRequestKey[] arg0, List<List<SearchResponseContext>> arg1) {
            event.setSearchResultsEvents(arg0);
            event.setResponseContexts(arg1);
        }
    };

    private final static EventFactory<SearchResultsToDistributeToResponseContexts> FACTORY = new EventFactory<SearchResultsToDistributeToResponseContexts>()
    {
        @Override
        public SearchResultsToDistributeToResponseContexts newInstance()
        {
            return new SearchResultsToDistributeToResponseContexts();
        }
    };

    public DisruptorBasedResponseContextTypeBasedResponseEventHandler(Configuration configuration,
                                                                      ResponseEventHandler delegate)
    {
        this.delegateHandler = delegate;
        this.executorService = getExecutorService();
        int bufferSize = configuration.getSizeOfResponseProcessingQueue();
        if(bufferSize==-1) {
            bufferSize = configuration.getSizeOfRelatedItemSearchRequestQueue();
        } else {
            bufferSize = Util.ceilingNextPowerOfTwo(bufferSize);
        }
        disruptor = new Disruptor<SearchResultsToDistributeToResponseContexts>(
                FACTORY,
                bufferSize, executorService,
                ProducerType.MULTI, configuration.getWaitStrategyFactory().createWaitStrategy());
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());

        disruptor.handleEventsWith(new EventHandler[] {EVENT_HANDLER});

        ringBuffer = disruptor.start();
    }

    private ExecutorService getExecutorService() {
        return newSingleThreadExecutor(new DefaultNameableThreadFactory("SearchResponseProcessingQueue"));
    }


    @Override
    public void handleResponseEvents(SearchResultEventWithSearchRequestKey[] searchResults,List<List<SearchResponseContext>> responseContexts) {
        ringBuffer.publishEvent(translator,searchResults,responseContexts);
    }

    @Override
    public void shutdown() {
       if(shutdown.compareAndSet(false,true)) {
           try {
               log.debug("Shutting down SearchResponseProcessingQueue ring buffer");
               disruptor.shutdown();
           } catch(Exception e) {

           }

           try {
               log.debug("Shutting down SearchResponseProcessingQueue thread pool");
               executorService.shutdownNow();
           } catch (Exception e) {

           }
       }
    }



    private static class SearchResultsToDistributeToResponseContexts {
        private SearchResultEventWithSearchRequestKey[] searchResultsEvents;
        private List<List<SearchResponseContext>> responseContexts;


        public SearchResultEventWithSearchRequestKey[] getSearchResultsEvents() {
            return searchResultsEvents;
        }

        public void setSearchResultsEvents(SearchResultEventWithSearchRequestKey[] searchResultsEvents) {
            this.searchResultsEvents = searchResultsEvents;
        }

        public List<List<SearchResponseContext>> getResponseContexts() {
            return responseContexts;
        }

        public void setResponseContexts(List<List<SearchResponseContext>> responseContexts) {
            this.responseContexts = responseContexts;
        }
    }
}
