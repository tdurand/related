package org.greencheek.relatedproduct.indexing.jsonrequestprocessing;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperty;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductInfo;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.indexing.InvalidIndexingRequestException;
import org.greencheek.relatedproduct.indexing.util.ISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 03/06/2013
 * Time: 20:34
 * To change this template use File | Settings | File Templates.
 */
public class JsonSmartIndexingRequestConverter implements IndexingRequestConverter {

    private static final Logger log = LoggerFactory.getLogger(JsonSmartIndexingRequestConverter.class);

    private final int maxNumberOfAdditionalProperties;
    private final int maxNumberOfRelatedProducts;
    private final String productKey;
    private final String dateKey;
    private final String idKey;

    private final String date;
    private final Object[] products;

    private final JSONObject object;

    public JsonSmartIndexingRequestConverter(Configuration config, ISO8601UTCCurrentDateAndTimeFormatter dateCreator, ByteBuffer requestData) {
        this(config,dateCreator,requestData,config.getMaxNumberOfRelatedProductProperties(), config.getMaxNumberOfRelatedProductsPerPurchase());
    }

    public JsonSmartIndexingRequestConverter(Configuration config, ISO8601UTCCurrentDateAndTimeFormatter dateCreator, ByteBuffer requestData,
        int maxNumberOfAllowedProperties,int maxNumberOfRelatedProducts) {

        this.maxNumberOfAdditionalProperties = maxNumberOfAllowedProperties;
        this.maxNumberOfRelatedProducts = maxNumberOfRelatedProducts;

        JSONParser parser = new JSONParser(JSONParser.MODE_RFC4627);
        try
        {
            object = (JSONObject) parser.parse(new ByteBufferBackedInputStream(requestData));
        }
        catch (Exception e)
        {
            throw new InvalidIndexingRequestException(e);
        }

        productKey = config.getKeyForIndexRequestProductArrayAttr();
        dateKey = config.getKeyForIndexRequestDateAttr();
        idKey = config.getKeyForIndexRequestIdAttr();

        Object products = object.remove(productKey);
        if(products == null) {
            throw new InvalidIndexingRequestException("No products in request data");
        }

        if(!(products instanceof JSONArray)) {
            throw new InvalidIndexingRequestException("No parsable products in request.  Product list must be an array of related products");
        } else {
            Object[] relatedProducts = ((JSONArray)products).toArray();
            int numberOfRelatedProducts = Math.min(relatedProducts.length,maxNumberOfRelatedProducts);
            if(numberOfRelatedProducts>maxNumberOfRelatedProducts) {
                if(config.shouldDiscardIndexRequestWithTooManyRelations()) {
                    throw new InvalidIndexingRequestException("Too many related products in request.  Not Parsing.");
                }
                else {
                    log.warn("Too many related products in request.  Ignored later related prodcuts");
                }
            }

            this.products = new Object[numberOfRelatedProducts];
            for(int i=0;i<numberOfRelatedProducts;i++) {
                this.products[i] = relatedProducts[i];
            }
        }

        String date = (String)object.remove(dateKey);
        if(date==null) {
            this.date = dateCreator.getCurrentDay();
        } else {
            this.date = dateCreator.formatToUTC(date);
        }
    }

    @Override
    public void translateTo(RelatedProductIndexingMessage convertedTo,
                            long sequence) {
        convertedTo.setValidMessage(true);
        convertedTo.setUTCFormattedDate(date);
        parseProductArray(convertedTo,maxNumberOfAdditionalProperties);
        parseAdditionalProperties(convertedTo.additionalProperties, object, maxNumberOfAdditionalProperties);
    }

    private void parseAdditionalProperties(RelatedProductAdditionalProperties properties,JSONObject map, int maxPropertiesThanCanBeRead) {
        int mapSize = map.size();
        if(mapSize==0) {
            properties.setNumberOfProperties(0);
            return;
        }

        int minNumberOfAdditionalProperties = Math.min(maxPropertiesThanCanBeRead, mapSize);
        RelatedProductAdditionalProperty[] additionalProperties = properties.getAdditionalProperties();

        int i=0;
        int safeNumberOfProperties = minNumberOfAdditionalProperties;

        for(String key : map.keySet()) {
            Object value = map.get(key);
            if(value instanceof String) {
                try {
                    additionalProperties[i].setName(key);
                    additionalProperties[i].setValue((String) value);
                } catch (Exception e) {
                    log.error("map: {}",map.toJSONString());
                    log.error("additional property: {}, {}",new Object[]{key,value,e});
                }
            } else {
                safeNumberOfProperties--;
            }
            if(++i==minNumberOfAdditionalProperties) break;
        }

        properties.setNumberOfProperties(safeNumberOfProperties);
    }

    private void parseProductArray(RelatedProductIndexingMessage event,
                                   int maxNumberOfAdditionalProperties) {


        int i = 0;
        RelatedProductInfo[] relatedProductInfos = event.getRelatedProducts().getListOfRelatedProductInfomation();
        for (Object product : this.products) {
            if (product instanceof JSONObject) {
                JSONObject productObj = (JSONObject) product;
                Object id = productObj.remove(idKey);
                if (id instanceof String) {
                    relatedProductInfos[i].setId((String) id);
                    parseAdditionalProperties(relatedProductInfos[i].getAdditionalProperties(), productObj, maxNumberOfAdditionalProperties);
                    productObj.put(idKey, id);
                } else {
                    productObj.put(idKey, id);
                    continue;
                }


            } else {
                try {
                    relatedProductInfos[i].setId((String) product);
                } catch (ClassCastException exception) {
                    continue;
                }
            }
            i++;
        }

        if (i == 0) {
            invalidateMessage(event);
        } else {
            event.relatedProducts.setNumberOfRelatedProducts(i);
        }

    }

    private void invalidateMessage(RelatedProductIndexingMessage message) {
        message.setValidMessage(false);
        message.relatedProducts.setNumberOfRelatedProducts(0);
    }

    class ByteBufferBackedInputStream extends InputStream {

        ByteBuffer buf;

        public ByteBufferBackedInputStream(ByteBuffer buf) {
            this.buf = buf;
        }

        public int read() throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }
            return buf.get() & 0xFF;
        }

        public int read(byte[] bytes, int off, int len)
                throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }

            len = Math.min(len, buf.remaining());
            buf.get(bytes, off, len);
            return len;
        }
    }
}
