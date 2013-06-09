package org.greencheek.relatedproduct.domain.api;

import org.greencheek.relatedproduct.resultsconverter.SearchResultsConverter;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:38
 * To change this template use File | Settings | File Templates.
 */
public class SearchResultsEvent {
    private final SearchResultsConverter results;

    public SearchResultsEvent(SearchResultsConverter results) {
        this.results = results;
    }
    public SearchResultsConverter getResults() {
        return results;
    }

}
