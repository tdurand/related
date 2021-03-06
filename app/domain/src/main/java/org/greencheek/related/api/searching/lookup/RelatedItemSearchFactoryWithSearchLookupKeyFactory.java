/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.api.searching.lookup;

import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.api.searching.RelatedItemSearchFactory;
import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Converts from a map of properties and a search type, to a RelatedItemSearch object.
 * The RelatedItemSearch object passed to the {@link #populateSearchObject(org.greencheek.related.api.searching.RelatedItemSearch, org.greencheek.related.api.searching.RelatedItemSearchType, java.util.Map)}
 * method for population.
 *
 *
 */
public class RelatedItemSearchFactoryWithSearchLookupKeyFactory implements RelatedItemSearchFactory {

    private static final Logger log = LoggerFactory.getLogger(RelatedItemSearchFactoryWithSearchLookupKeyFactory.class);
    private final Configuration configuration;
    private final RelatedItemSearchLookupKeyGenerator lookupKeyGenerator;

    public RelatedItemSearchFactoryWithSearchLookupKeyFactory(Configuration config, RelatedItemSearchLookupKeyGenerator lookupKeyGenerator) {
        this.configuration = config;
        this.lookupKeyGenerator = lookupKeyGenerator;

    }

    @Override
    public RelatedItemSearch createSearchObject() {
        return new RelatedItemSearch(configuration);
    }

    @Override
    public void populateSearchObject(RelatedItemSearch objectToPopulate,
                                     RelatedItemSearchType type,
                                     Map<String, String> properties) {

        objectToPopulate.setStartOfRequestNanos(System.nanoTime());
        String sizeKey = configuration.getRequestParameterForSize();
        String idKey = configuration.getRequestParameterForId();
        try {
            String size = properties.remove(sizeKey);
            if(size!=null) {
                objectToPopulate.setMaxResults(Integer.parseInt(size));
            } else {
                objectToPopulate.setMaxResults(configuration.getDefaultNumberOfResults());
            }
        } catch(NumberFormatException e) {
            objectToPopulate.setMaxResults(configuration.getDefaultNumberOfResults());
        }

        objectToPopulate.setRelatedItemId(properties.remove(idKey));

        RelatedItemAdditionalProperties props = objectToPopulate.getAdditionalSearchCriteria();
        int maxPropertiesToCopy = Math.min(props.getMaxNumberOfAvailableProperties(),properties.size());
        log.debug("max properties to copy {}, from properties {}",maxPropertiesToCopy,properties);

        int i=0;
        List<String> sortedParameters = new ArrayList<String>(properties.keySet());
        Collections.sort(sortedParameters);
        for(String key : sortedParameters) {
            if(i==maxPropertiesToCopy) break;
            props.setProperty(key,properties.get(key),i++);
        }

        props.setNumberOfProperties(i);

        objectToPopulate.setRelatedItemSearchType(type);

        lookupKeyGenerator.setSearchRequestLookupKeyOn(objectToPopulate);


    }

    @Override
    public RelatedItemSearch newInstance() {
        return createSearchObject();
    }

}
