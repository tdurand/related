package org.greencheek.relatedproduct.indexing;

import com.lmax.disruptor.EventTranslator;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 03/06/2013
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
public interface IndexingRequestConverter extends EventTranslator<RelatedProductIndexingMessage> {
    public void translateTo(RelatedProductIndexingMessage convertedTo,
                                                  long sequence);
}
