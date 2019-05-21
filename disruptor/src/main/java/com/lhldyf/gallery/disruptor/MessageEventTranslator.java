package com.lhldyf.gallery.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;

/**
 * @author lhldyf
 * @date 2019-05-21 16:48
 */
public class MessageEventTranslator implements EventTranslatorOneArg<MessageEventEntity, String> {
    @Override
    public void translateTo(MessageEventEntity messageEntity, long l, String message) {
        messageEntity.setMessage(message);
    }
}
