package com.lhldyf.gallery.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import org.springframework.stereotype.Component;

/**
 * @author lhldyf
 * @date 2019-05-21 16:59
 */
@Component
public class StatisticHandler implements EventHandler<MessageEventEntity>, WorkHandler<MessageEventEntity> {
    @Override
    public void onEvent(MessageEventEntity event, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(event);
    }

    @Override
    public void onEvent(MessageEventEntity event) throws Exception {
        // System.out.println(event.getMessage() + " do statistic in the end");
    }
}
