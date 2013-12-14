package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.EventTranslatorOneArg;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.domain.api.SearchEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchEventType;
import org.greencheek.relatedproduct.searching.domain.api.SearchRequestEvent;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;

import javax.servlet.AsyncContext;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class SearchRequestTranslator implements EventTranslatorOneArg<SearchEvent,RelatedProductSearchRequest> {

    public static final SearchRequestTranslator INSTANCE = new SearchRequestTranslator();

    public SearchRequestTranslator() {

    }

    @Override
    public void translateTo(SearchEvent event, long sequence, RelatedProductSearchRequest searchRequest) {
        event.setSearchRequestEvent(new SearchRequestEvent(searchRequest.getRequestContext(),searchRequest.getSearchRequest(),searchRequest.getSearchExecutor()));
        event.setEventType(SearchEventType.SEARCH_REQUEST);
        event.setRequestKey(searchRequest.getSearchRequest().getLookupKey());
    }


}
