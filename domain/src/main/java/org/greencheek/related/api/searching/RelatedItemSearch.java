package org.greencheek.related.api.searching;

import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.RelatedItemInfoIdentifier;
import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.util.config.Configuration;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 */
public class RelatedItemSearch {

    public static final String RESULTS_SET_SIZE_KEY = "resultssetsize";
    public static final String ID_KEY = "id";
    public static final int RESULTS_SET_SIZE_KEY_LENGTH = RESULTS_SET_SIZE_KEY.length();
    public static final int ID_KEY_LENGTH = ID_KEY.length();

    private int maxResults;
    private RelatedItemSearchType searchType;
    private SearchRequestLookupKey lookupKey;

    private final RelatedItemInfoIdentifier relatedItemId;
    private final RelatedItemAdditionalProperties additionalSearchCriteria;


    public RelatedItemSearch(Configuration config) {
        relatedItemId = new RelatedItemInfoIdentifier(config);
        additionalSearchCriteria = new RelatedItemAdditionalProperties(config,config.getMaxNumberOfSearchCriteriaForRelatedContent());
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public RelatedItemAdditionalProperties getAdditionalSearchCriteria() {
        return this.additionalSearchCriteria;
    }

    public void setRelatedItemId(String id) {
        this.relatedItemId.setId(id);
    }

    public String getRelatedItemId() {
        return this.relatedItemId.toString();
    }

    public RelatedItemInfoIdentifier getRelatedContentIdentifier() {
        return this.relatedItemId;
    }

    private void setRelatedItemId(RelatedItemInfoIdentifier id) {
        id.copyTo(this.relatedItemId);
    }

    public RelatedItemSearchType getRelatedItemSearchType() {
        return this.searchType;
    }

    public void setRelatedItemSearchType(RelatedItemSearchType searchType) {
        this.searchType = searchType;
    }

    public RelatedItemSearch copy(Configuration config) {
        RelatedItemSearch newCopy = new RelatedItemSearch(config);
        newCopy.setRelatedItemId(this.relatedItemId);
        newCopy.setMaxResults(this.maxResults);
        newCopy.setRelatedItemSearchType(this.searchType);
        newCopy.setLookupKey(this.getLookupKey());
        this.additionalSearchCriteria.copyTo(newCopy.additionalSearchCriteria);
        return newCopy;
    }

    public void setLookupKey(SearchRequestLookupKey key) {
        this.lookupKey = key;
    }

    public SearchRequestLookupKey getLookupKey() {
        return lookupKey;
    }

}
