package com.lhldyf.gallery.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import org.springframework.stereotype.Component;

/**
 * @author lhldyf
 * @date 2019-05-21 16:59
 */
@Component
public class AlertHandler implements WorkHandler<MessageEventEntity>, EventHandler<MessageEventEntity> {

    @Override
    public void onEvent(MessageEventEntity event) throws Exception {
        System.out.println(event.getMessage() + " do alert");
    }

    @Override
    public void onEvent(MessageEventEntity event, long sequence, boolean endOfBatch) throws Exception {
        if(event.getMessage().contains("not")) {
            throw new RuntimeException("无需采集的数据");
        }
        this.onEvent(event);
    }
}
