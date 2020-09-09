package com.lhldyf.gallery.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.io.File;

/**
 * @author lhldyf
 * @date 2020-01-14 22:26
 */
public class FileDemo {
    public static void main(String[] args) throws Exception {
        File inboxDirectory = new File("data/inbox");
        System.out.println(inboxDirectory.exists());
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // noop=true: 表示此操作为复制，如果去掉此参数则为移动
                from("file://data/inbox?noop=true")
                        //                from("file:data/inbox")
                        .to("file://data/outbox");
            }
        });
        context.start();
        Thread.sleep(10000);
        context.stop();
    }
}
