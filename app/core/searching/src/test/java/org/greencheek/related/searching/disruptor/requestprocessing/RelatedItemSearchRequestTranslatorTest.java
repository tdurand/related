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

package org.greencheek.related.searching.disruptor.requestprocessing;

import org.greencheek.related.api.searching.KeyFactoryBasedRelatedItemSearchLookupKeyGenerator;
import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.api.searching.lookup.RelatedItemSearchFactoryWithSearchLookupKeyFactory;
import org.greencheek.related.api.searching.lookup.SipHashSearchRequestLookupKeyFactory;
import org.greencheek.related.searching.domain.RelatedItemSearchRequest;
import org.greencheek.related.searching.requestprocessing.AsyncServletSearchResponseContext;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import javax.servlet.AsyncContext;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class RelatedItemSearchRequestTranslatorTest {


    @Test
    public void testTranslateTo() throws Exception {
        Configuration config = new SystemPropertiesConfiguration();

        RelatedItemSearchRequest request = new RelatedItemSearchRequest(config);
        Map<String,String> properties = new HashMap<String,String>();
        properties.put(config.getRequestParameterForId(),"id1");
        properties.put("channel","com");

        SearchResponseContext[] contexts = new SearchResponseContext[] {new AsyncServletSearchResponseContext(mock(AsyncContext.class))};

        RelatedItemSearchRequestTranslator translator = new RelatedItemSearchRequestTranslator(
                new RelatedItemSearchFactoryWithSearchLookupKeyFactory(config,new KeyFactoryBasedRelatedItemSearchLookupKeyGenerator(config,new SipHashSearchRequestLookupKeyFactory())));



        translator.translateTo(request, 1, RelatedItemSearchType.FREQUENTLY_RELATED_WITH,properties,contexts);

        assertSame(request.getRequestContexts(), contexts);

        assertEquals(request.getSearchRequest().getRelatedItemId(),"id1");

        assertEquals(1,request.getSearchRequest().getAdditionalSearchCriteria().getNumberOfProperties());

        assertEquals("channel",request.getSearchRequest().getAdditionalSearchCriteria().getPropertyName(0));
        assertEquals("com",request.getSearchRequest().getAdditionalSearchCriteria().getPropertyValue(0));
    }
}
