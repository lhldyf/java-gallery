package com.lhldyf.gallery.netty;

/**
 * @author lhldyf
 * @date 2019-05-30 17:26
 */
public class ServerBootstrap {
    public static void main(String[] args) {
        int port = 9999;
        new Thread(() -> {
            new Server().run(port);
        }).start();
    }
}
