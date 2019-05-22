package com.lhldyf.gallery.disruptor;

import com.lmax.disruptor.RingBuffer;

/**
 * disruptor的生命周期由程序启动关闭时需特殊处理，因此在DisruptorConfiguration中添加该bean的实例化
 * @author lhldyf
 * @date 2019-05-21 17:01
 */
public class MessageProducer {

    private static final MessageEventTranslator TRANSLATOR = new MessageEventTranslator();

    private RingBuffer<MessageEventEntity> ringBuffer;


    public void onData(String message) {
        try {
            ringBuffer.publishEvent(TRANSLATOR, message);
        } catch (Exception e) {
            System.out.println("异常");
            e.printStackTrace();
        }

    }


    public void setRingBuffer(RingBuffer<MessageEventEntity> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }
}
