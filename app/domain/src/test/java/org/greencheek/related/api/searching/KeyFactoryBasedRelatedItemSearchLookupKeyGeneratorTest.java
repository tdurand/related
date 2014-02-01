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

package org.greencheek.related.api.searching;

import org.greencheek.related.api.searching.lookup.RelatedItemSearchLookupKeyGenerator;
import org.greencheek.related.api.searching.lookup.SipHashSearchRequestLookupKeyFactory;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 13/10/2013
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class KeyFactoryBasedRelatedItemSearchLookupKeyGeneratorTest extends RelatedItemSearchLookupKeyGeneratorTest {

    private final Configuration configuration = new SystemPropertiesConfiguration();
    @Override
    public RelatedItemSearchLookupKeyGenerator getGenerator() {
        return new KeyFactoryBasedRelatedItemSearchLookupKeyGenerator(configuration, new SipHashSearchRequestLookupKeyFactory());
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
