package org.greencheek.relatedproduct.searching.repository;

import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.domain.searching.FrequentlyRelatedSearchResult;
import org.greencheek.relatedproduct.domain.searching.FrequentlyRelatedSearchResults;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.api.searching.SearchResultsOutcomeType;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 19:47
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchFrequentlyRelatedProductSearchProcessor {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchFrequentlyRelatedProductSearchProcessor.class);

    private final Configuration configuration;
    private final String indexName;
    private final String facetResultName;
    private final long searchTimeout;

    public ElasticSearchFrequentlyRelatedProductSearchProcessor(Configuration configuration) {
        this.configuration = configuration;
        String indexNameAlias = configuration.getStorageIndexNameAlias();
        if(indexNameAlias==null || indexNameAlias.trim().length()==0) {
            indexName = configuration.getStorageIndexNamePrefix()+"*";
        } else {
            indexName = indexNameAlias;
        }
        this.facetResultName = configuration.getStorageFrequentlyRelatedProductsFacetResultsFacetName();
        this.searchTimeout = configuration.getFrequentlyRelatedProductsSearchTimeoutInMillis();

    }

    public MultiSearchResponse executeSearch(Client elasticClient,RelatedProductSearch[] searches) {
        MultiSearchRequestBuilder multiSearch = elasticClient.prepareMultiSearch();
        for(RelatedProductSearch search : searches) {

            if(search.getRelatedProductSearchType() == RelatedProductSearchType.FREQUENTLY_RELATED_WITH) {
                multiSearch.add(createFrequentlyRelatedContentSearch(search,elasticClient));
            }
        }
        log.debug("executing searches");
        return multiSearch.execute().actionGet(searchTimeout, TimeUnit.MILLISECONDS);
    }

    public SearchResultEventWithSearchRequestKey[] processMultiSearchResponse(RelatedProductSearch[] searches,MultiSearchResponse searchResponse) {
        int i = 0;
        SearchResultEventWithSearchRequestKey[] results = new SearchResultEventWithSearchRequestKey[searches.length];
        for(MultiSearchResponse.Item item : searchResponse.getResponses()) {
            SearchRequestLookupKey key = searches[i].getLookupKey();
            results[i++] = frequentlyRelatedWithResultsConverter(key,item.getResponse(),item.getFailureMessage(),item.isFailure());
        }

        return results;
    }

    private SearchResultEventWithSearchRequestKey frequentlyRelatedWithResultsConverter(SearchRequestLookupKey key,
                                                                     SearchResponse searchResponse,
                                                                     String failureMessage,
                                                                     boolean isFailure) {
        final FrequentlyRelatedSearchResults results;
        SearchResultsOutcomeType outcome;
        if(isFailure) {
            log.error("Search response failure for search request key : {}",key,failureMessage);
            return new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS,key);
        }
        else {
            TermsFacet f = (TermsFacet) searchResponse.getFacets().facetsAsMap().get(facetResultName);
            List<TermsFacet.Entry> facets = (List<TermsFacet.Entry>) f.getEntries();
            int noOfFacets = facets==null ? 0 : facets.size();
            if(noOfFacets!=0) {
                FrequentlyRelatedSearchResult[] mostFrequentItems = new FrequentlyRelatedSearchResult[noOfFacets];

                while(noOfFacets--!=0) {
                    TermsFacet.Entry entry = facets.get(noOfFacets);
                    mostFrequentItems[noOfFacets] = new FrequentlyRelatedSearchResult(entry.getTerm().string(), entry.getCount());
                }
                results = new FrequentlyRelatedSearchResults(mostFrequentItems);
                outcome = SearchResultsOutcomeType.HAS_RESULTS;
            } else {
                log.debug("no related content found for search key {}",key);
                return new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS,key);
            }

        }

        return new SearchResultEventWithSearchRequestKey(new SearchResultsEvent(RelatedProductSearchType.FREQUENTLY_RELATED_WITH,
                                      outcome,results),key);

    }



    /**
     * Search like :
     *
     * "query" : {
            "bool" : {
                "must" : [
                    {"field" : {"id" : "338906"}},
                    {"field" : {"channel" : "uk"}},
                    {"field" : {"site" : "amazon"}}
                ]
            }
        },
        "facets" : {
            "frequently-related-with" : {
                "terms" : {"field" : "related-with", "size" : 5 }
            }
        },
        "size":0
     }'
     * @param search
     * @return
     */
    private SearchRequestBuilder createFrequentlyRelatedContentSearch(RelatedProductSearch search, Client searchClient) {
        SearchRequestBuilder sr = searchClient.prepareSearch();
        String id = search.getRelatedContentId();
        StringBuilder b = new StringBuilder(id.length()+2).append('"').append(id).append('"');
        BoolQueryBuilder bool = QueryBuilders.boolQuery().must(QueryBuilders.fieldQuery(configuration.getKeyForIndexRequestIdAttr(),b.toString()));

        RelatedProductAdditionalProperties searchProps = search.getAdditionalSearchCriteria();
        int numberOfProps = searchProps.getNumberOfProperties();

        for(int i = 0;i<numberOfProps;i++) {
            bool.must(QueryBuilders.fieldQuery(searchProps.getPropertyName(i), searchProps.getPropertyValue(i)));
        }

        sr.setIndices(indexName);
        sr.setSize(0);
        sr.setQuery(bool);
        sr.setTimeout(TimeValue.timeValueMillis(searchTimeout));
        sr.addFacet(FacetBuilders.termsFacet(facetResultName).field(configuration.getKeyForIndexRequestRelatedWithAttr()).size(search.getMaxResults()));
        log.debug("Executing Query {}",sr);
        return sr;

    }
}
