package org.greencheek.relatedproduct.domain.searching;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/10/2013
 * Time: 13:14
 * To change this template use File | Settings | File Templates.
 */
public class DJBX33AWithTCRSearchRequestLookupKeyTest extends SearchRequestLookupKeyTest
{
    private static SearchRequestLookupKeyFactory factory = new DJBX33WithTCRSearchRequestLookupKeyFactory();

    @Override
    public SearchRequestLookupKeyFactory getFactory() {
        return factory;
    }
}
