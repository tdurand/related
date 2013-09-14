package org.greencheek.relatedproduct.api.indexing;

import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperty;
import org.greencheek.relatedproduct.domain.RelatedProduct;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 19:33
 */
public class BasicRelatedProductIndexingMessageConverter implements RelatedProductIndexingMessageConverter{

    private final Configuration configuration;

    public BasicRelatedProductIndexingMessageConverter(Configuration configuration) {
        this.configuration = configuration;
    }

    public RelatedProduct[] convertFrom(RelatedProductIndexingMessage message) {

        RelatedProductSet products = message.getRelatedProducts();
        short numberOfRelatedProducts = products.getNumberOfRelatedProducts();

        RelatedProduct[] relatedProducts = new RelatedProduct[numberOfRelatedProducts];
//        String[] ids = new String[numberOfRelatedProducts];
//        RelatedProductInfo[] productInfos = new RelatedProductInfo[numberOfRelatedProducts];

//        populateRelatedProductInfo(products.getListOfRelatedProductInfomation(),productInfos,numberOfRelatedProducts);

        RelatedProductInfo[][] idLists = relatedIds(products.getListOfRelatedProductInfomation(),products.getNumberOfRelatedProducts());

        int length = numberOfRelatedProducts-1;
//        String[][] indexProperties = message.getIndexingMessageProperties().convertToStringArray();
        for(int i =0;i<numberOfRelatedProducts;i++) {
            RelatedProductInfo id = idLists[i][length];
            relatedProducts[i] = createRelatedProduct(message,id,idLists[i],message.getIndexingMessageProperties());
        }


//        relatedProducts[length] = createRelatedProduct(message,productInfos[length],
//                                                  idLists[0],indexProperties);
        return relatedProducts;

    }

    private RelatedProduct createRelatedProduct(RelatedProductIndexingMessage message,RelatedProductInfo info,
                                                RelatedProductInfo[] ids,
                                                RelatedProductAdditionalProperties indexProperties) {
        RelatedProductAdditionalProperties productProperties = info.getAdditionalProperties();


//        String[][] productProperties = info.additionalProperties.convertToStringArray();

//        int indexSize = productProperties.length+indexProperties.length;
        int numberOfProductProperties = productProperties.getNumberOfProperties();
        int numberOfIndexRequestProperties = indexProperties.getNumberOfProperties();
        int numberOfProperties = numberOfProductProperties +  numberOfIndexRequestProperties;
        RelatedProductAdditionalProperty[] properties = new RelatedProductAdditionalProperty[numberOfProperties];

        int propertyIndex=0;

        RelatedProductAdditionalProperty[] productProps = productProperties.getAdditionalProperties();
        for(int i=0;i<numberOfProductProperties;i++) {
            properties[propertyIndex++] = productProps[i];
        }

        productProps = indexProperties.getAdditionalProperties();
        for(int j=0;j<numberOfIndexRequestProperties;j++) {
            properties[propertyIndex++] = productProps[j];
        }

//        String[][] additionalProperties = new String[indexSize--][2];

//        // properties from the index request
//        for(int i=0;i<indexProperties.length;i++) {
//            additionalProperties[indexSize][0]    = indexProperties[i][0];
//            additionalProperties[indexSize--][1]  = indexProperties[i][1];
//        }
//
//        // properties specific to the product
//        for(int i=0;i<productProperties.length;i++) {
//            additionalProperties[indexSize][0]    = productProperties[i][0];
//            additionalProperties[indexSize--][1]  = productProperties[i][1];
//        }

        int relatedIdLength = ids.length-1;
        char[][] relatedIds = new char[relatedIdLength][];
        for(int i=0;i<relatedIdLength;i++) {
            relatedIds[i] = ids[i].getId().duplicate();
        }

        return new RelatedProduct(info.getId().duplicate(),message.dateUTC,relatedIds,properties);
    }


    /**
     * Given a list of ids {"1","2","3","5"}
     *
     * The method returns list of ids, where each id, is returned
     * with a link to the other ids in the list that it is related to.
     *
     * for example:
     *
     * 1 -> 2, 3, 5
     * 2 -> 3, 5, 1
     * 3 -> 5, 1, 2
     * 5 -> 1, 2, 3
     *
     * The item that is related to the the other items, is the last element in the returned
     * array (X marks the spot below)
     *
     * i.e.
     *              X
     * [
     *   [ 2, 3, 5, 1 ],
     *   [ 3, 5, 1, 2 ],
     *   [ 5, 1, 2, 3 ],
     *   [ 1, 2, 3, 5 ]
     * ]
     *
     *
     *
     */
    public static RelatedProductInfo[][] relatedIds(RelatedProductInfo[] ids, int length) {
        int len = length;
        int lenMinOne = len-1;
        RelatedProductInfo[][] idSets = new RelatedProductInfo[len][len];

        for(int i=0;i<len;i++) {
            int start = i;
            int missingElem = i;
            for(int j = 0;j<lenMinOne;j++) {
                int elem = start++;
                missingElem++;
                if(elem>lenMinOne) {
                    elem-=len;
                }
                idSets[i][j] = ids[elem];
            }
            idSets[i][lenMinOne] = ids[(missingElem>lenMinOne)? missingElem-len : missingElem];
        }

        return idSets;
    }



//    private void populateRelatedProductInfo(RelatedProductInfo[] message, RelatedProductInfo[] products, short numberOfRelatedProducts ) {
//
//        for(int i =0;i<numberOfRelatedProducts;i++) {
//            products[i] = message[i];
////            ids[i] = message[i].id.toString();
//        }
//
//    }





}
