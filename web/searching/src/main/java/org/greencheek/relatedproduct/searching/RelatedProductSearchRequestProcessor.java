package org.greencheek.relatedproduct.searching;

import javax.servlet.AsyncContext;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 18:36
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedProductSearchRequestProcessor {
    public void processRequest(Map<String,String> parameters, AsyncContext context);
    public void shutdown();
}
