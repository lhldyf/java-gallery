package com.lhldyf.gallery.disruptor;


import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import org.springframework.stereotype.Component;

/**
 * @author lhldyf
 * @date 2019-05-21 16:51
 */
@Component
public class TransformHandler implements EventHandler<MessageEventEntity>, WorkHandler<MessageEventEntity> {
    @Override
    public void onEvent(MessageEventEntity event, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(event);
    }

    @Override
    public void onEvent(MessageEventEntity event) throws Exception {
        System.out.println("before transform, message: " + event.getMessage());
        event.setMessage(event.getMessage() + " has been transformed");
        System.out.println("after transform, message: " + event.getMessage());
    }
}
