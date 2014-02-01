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

package org.greencheek.related.searching.responseprocessing.resultsconverter;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 18:54
 * To change this template use File | Settings | File Templates.
 */
public class JsonFrequentlyRelatedSearchResultsConverter implements SearchResultsConverter<FrequentlyRelatedSearchResult[]> {

    private static final Logger log = LoggerFactory.getLogger(JsonFrequentlyRelatedSearchResultsConverter.class);


    private static final String JSON_CONTENT_TYPE = "application/json";
    private final Configuration configuration;

    public JsonFrequentlyRelatedSearchResultsConverter(Configuration configuration) {
        this.configuration = configuration;
    }

    private Map<String,Object> createJson(SearchResultEventWithSearchRequestKey<FrequentlyRelatedSearchResult[]> searchResultsEvent) {
        SearchResultsEvent<FrequentlyRelatedSearchResult[]> event = searchResultsEvent.getResponse();
        if(event==null) return createEmptyJson();
        FrequentlyRelatedSearchResult[] results = event.getSearchResults();
        int resultsSize = results.length;
        if(resultsSize==0) return createEmptyJson();

        Map<String,Object> resultsMap = new HashMap<String,Object>((int)Math.ceil((2 + (resultsSize*2))/0.75));
        resultsMap.put(configuration.getKeyForFrequencyResultOverallResultsSize(),Integer.toString(resultsSize));

        Map<String, String>[] relatedItems = new HashMap[resultsSize];
        int i = 0;
        for (FrequentlyRelatedSearchResult res : results) {
            Map<String, String> product = new HashMap<String, String>(3);

            product.put(configuration.getKeyForFrequencyResultOccurrence(), Long.toString(res.getFrequency()));
            product.put(configuration.getKeyForFrequencyResultId(), res.getRelatedItemId());
            relatedItems[i++] = product;

        }
        resultsMap.put(configuration.getKeyForFrequencyResults(), relatedItems);
        resultsMap.put(configuration.getKeyForStorageResponseTime(),Long.toString(searchResultsEvent.getSearchExecutionTime()));
        resultsMap.put(configuration.getKeyForSearchProcessingResponseTime(),Long.toString((System.nanoTime() - searchResultsEvent.getStartOfSearchRequestProcessing())/1000000));

        return resultsMap;

    }

    private Map<String,Object> createEmptyJson() {
        Map<String,Object> resultsMap = new HashMap<String,Object>(4);
        resultsMap.put(configuration.getKeyForFrequencyResultOverallResultsSize(),"0");
        resultsMap.put(configuration.getKeyForFrequencyResults(),new String[0]);
        return resultsMap;
    }

    @Override
    public String contentType() {
        return JSON_CONTENT_TYPE;
    }

    @Override
    public String convertToString(SearchResultEventWithSearchRequestKey<FrequentlyRelatedSearchResult[]> results) {
        Map<String,Object> jsonResults = null;

        if(results==null) {
            jsonResults = createEmptyJson();
        }
        else {
            jsonResults = createJson(results);
        }
        return JSONObject.toJSONString(jsonResults,JSONStyle.LT_COMPRESS);
    }
}
