package org.greencheek.relatedproduct.api.indexing;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */
@Named
public class RelatedProductIndexingMessageFactory implements EventFactory<RelatedProductIndexingMessage> {

    private final Configuration configuration;

    @Inject
    public RelatedProductIndexingMessageFactory(Configuration configuration) {
        this.configuration=configuration;
    }

    @Override
    public RelatedProductIndexingMessage newInstance() {
        RelatedProductIndexingMessage message = new RelatedProductIndexingMessage(configuration);
        message.setByteBuffer(ByteBuffer.allocate(message.getSize()), 0);
        return message;
    }
}
