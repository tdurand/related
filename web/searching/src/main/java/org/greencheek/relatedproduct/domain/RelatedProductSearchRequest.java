package org.greencheek.relatedproduct.domain;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperty;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.servlet.AsyncContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 19:53
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearchRequest {

    private RelatedProductSearchType requestType;
    private AsyncContext requestContext;
    private Map<String,String> requestProperties;

    private RelatedProductSearchRequest() {
    }

    public void setRequestProperties(Map<String,String> requestProperties) {
        this.requestProperties = requestProperties;
    }

    public Map<String, String> getRequestProperties() {
        return requestProperties;
    }

    public void setRequestContext(AsyncContext clientCtx) {
        this.requestContext = clientCtx;
    }

    public AsyncContext getRequestContext() {
        return requestContext;
    }

    public void setRequestType(RelatedProductSearchType type) {
        this.requestType = type;
    }

    public RelatedProductSearchType getRequestType() {
        return requestType;
    }

    public final static EventFactory<RelatedProductSearchRequest> FACTORY = new EventFactory<RelatedProductSearchRequest>()
    {
        @Override
        public RelatedProductSearchRequest newInstance()
        {
            return new RelatedProductSearchRequest();
        }
    };

}
