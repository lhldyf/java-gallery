package com.lhldyf.gallery.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * @author lhldyf
 * @date 2019-05-21 16:44
 */
public class MessageEventFactory implements EventFactory<MessageEventEntity> {

    @Override
    public MessageEventEntity newInstance() {
        return new MessageEventEntity();
    }
}
