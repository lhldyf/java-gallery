package com.lhldyf.gallery.disruptor;

import com.lmax.disruptor.ExceptionHandler;

/**
 * @author lhldyf
 * @date 2019-05-21 19:14
 */
public class DefaultExceptionHandler implements ExceptionHandler<MessageEventEntity> {
    @Override
    public void handleEventException(Throwable ex, long sequence, MessageEventEntity event) {
        System.out.println("handleEventException");
        ex.printStackTrace();
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        System.out.println("start exception");
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        System.out.println("shutdown exception");
    }
}
