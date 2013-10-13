package org.greencheek.relatedproduct.domain.searching;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/10/2013
 * Time: 11:57
 *
 * Allow for future different implmentations of the SearchRequestLookupKey
 * by creating a Factory that creates the direct implementations.  So we can
 * swap in and out the implementations rather than having a direct dependency on
 * a particular implementation
 */
public interface SearchRequestLookupKeyFactory {

    /**
     * Give a normal string object, converts to a SearchRequestLookupKey
     *
     * @param key The string to wrap.
     * @return the SearchRequestLookupKey
     */
    SearchRequestLookupKey createSearchRequestLookupKey(String key);


}
