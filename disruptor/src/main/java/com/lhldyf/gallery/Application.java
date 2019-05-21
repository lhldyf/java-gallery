package com.lhldyf.gallery;

import com.lhldyf.gallery.disruptor.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lhldyf
 * @date 2019-05-21 16:39
 */
@SpringBootApplication
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    MessageProducer messageProducer;


    @GetMapping("{message}")
    public String test(@PathVariable String message) {
        messageProducer.onData(message);
        return "success";
    }
}
