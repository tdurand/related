package org.greencheek.relatedproduct.domain.searching;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/10/2013
 * Time: 13:14
 * To change this template use File | Settings | File Templates.
 */
public class SipHashSearchRequestLookupKeyTest extends SearchRequestLookupKeyTest
{
    private static SearchRequestLookupKeyFactory factory = new SipHashSearchRequestLookupKeyFactory();

    @Override
    public SearchRequestLookupKeyFactory getFactory() {
        return factory;
    }
}
