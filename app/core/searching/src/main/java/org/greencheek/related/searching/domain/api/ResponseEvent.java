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

package org.greencheek.related.searching.domain.api;

import com.lmax.disruptor.EventFactory;
import org.greencheek.related.searching.requestprocessing.SearchResponseContextHolder;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 16/06/2013
 * Time: 07:11
 * To change this template use File | Settings | File Templates.
 */
public class ResponseEvent {
    private SearchResponseContextHolder[] contexts;
    private SearchResultsEvent results;


    public final static EventFactory<ResponseEvent> FACTORY = new EventFactory<ResponseEvent>()
    {
        @Override
        public ResponseEvent newInstance()
        {
            return new ResponseEvent();
        }
    };

    public SearchResponseContextHolder[] getContexts() {
        return contexts;
    }

    public void setContexts(SearchResponseContextHolder[] context) {
        this.contexts = context;
    }

    public SearchResultsEvent getResults() {
        return results;
    }

    public void setResults(SearchResultsEvent results) {
        this.results = results;
    }
}
