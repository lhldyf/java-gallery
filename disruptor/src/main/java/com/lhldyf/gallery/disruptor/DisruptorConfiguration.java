package com.lhldyf.gallery.disruptor;

import com.lhldyf.gallery.util.BeanRegisterUtils;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * @author lhldyf
 * @date 2019-05-21 17:26
 */
@Configuration
public class DisruptorConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private StatisticHandler statisticHandler;

    @Autowired
    private StorageHandler storageHandler;

    @Autowired
    private AlertHandler alertHandler;

    @Autowired
    private TransformHandler transformHandler;

    @Bean
    public MessageProducer messageProducer() {
        MessageProducer messageProducer = new MessageProducer();
        Disruptor disruptor = new Disruptor<>(new MessageEventFactory(), 1024 * 1024, Executors.defaultThreadFactory());

        // handleEventsWithWorkerPool 在同一个work组的是竞争关系，也就是只有一个handler能拿到数据进行处理
        // disruptor.handleEventsWithWorkerPool(transformHandler)
        //          .thenHandleEventsWithWorkerPool(alertHandler,storageHandler)
        //          .thenHandleEventsWithWorkerPool(statisticHandler);

        disruptor.handleEventsWith(transformHandler).then(alertHandler, storageHandler).then(statisticHandler);
        messageProducer.setRingBuffer(disruptor.getRingBuffer());
        BeanRegisterUtils.registerSingleton(applicationContext, "MessageEventDisruptorLifeCycleContainer",
                                            new DisruptorLifeCycleContainer("MessageEventDisruptor", disruptor,
                                                                            1));
        return messageProducer;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
