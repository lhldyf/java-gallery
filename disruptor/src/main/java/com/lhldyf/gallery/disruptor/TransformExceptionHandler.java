package com.lhldyf.gallery.disruptor;

import com.lmax.disruptor.ExceptionHandler;

/**
 * @author lhldyf
 * @date 2019-05-21 18:14
 */
public class TransformExceptionHandler implements ExceptionHandler<MessageEventEntity> {
    @Override
    public void handleEventException(Throwable ex, long sequence, MessageEventEntity event) {
        System.out.println("transform exception handler");
    }

    @Override
    public void handleOnStartException(Throwable ex) {

    }

    @Override
    public void handleOnShutdownException(Throwable ex) {

    }
}
