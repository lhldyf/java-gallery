package com.lhldyf.gallery.netty;

import io.netty.channel.Channel;

/**
 * @author lhldyf
 * @date 2019-05-30 17:27
 */
public class ClientMain {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 2222;
        Channel channel = new Connection().connect(host, port);
        channel.writeAndFlush("yinjihuan");
    }
}
