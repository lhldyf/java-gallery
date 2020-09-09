package com.lhldyf.gallery.camel;

import org.apache.camel.spring.Main;

/**
 * @author lhldyf
 * @date 2020-01-14 22:50
 */
public class CamelConsoleMain {
    public static void main(String[] args) throws Exception {
        // Main makes it easier to run a Spring application
        Main main = new Main();
        // configure the location of the Spring XML file
        main.setApplicationContextUri("META-INF/spring/camel-context.xml");
        // run and block until Camel is stopped (or JVM terminated)
        main.run();
    }
}
