package com.lhldyf.gallery.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author lhldyf
 * @date 2019-05-30 17:25
 */
public class ServerStringHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.err.println("server:" + msg.toString());
        if (msg.toString().contains("DATA")) {
            while (true) {

                ctx.writeAndFlush("55,101,服务器数据,1560305109,1|[{22.11,22.22},{33.33,33.44}]\n");
                // ctx.writeAndFlush(
                //         "  121 ,110,root,1560305109,2|[{0.0.0.0,1554453980,5851129,101,3.29,0.00}],{192.168.40.1,"
                //                 + "1560242542,62567,101,3.29,0.00}]]\n");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (msg.toString().contains("LOG")) {
            while (true) {
                ctx.writeAndFlush("2,/usr/share/kxf-service/logs/error.log,1560305144,1000\n");
                ctx.writeAndFlush("这是第一行日志\n");
                ctx.writeAndFlush("这里才结束，一共两行日志 FENDFEND\n");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
