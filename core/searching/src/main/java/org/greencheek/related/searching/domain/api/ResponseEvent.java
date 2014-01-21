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
