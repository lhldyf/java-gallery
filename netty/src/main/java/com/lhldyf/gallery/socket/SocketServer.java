package com.lhldyf.gallery.socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * @author lhldyf
 * @date 2019-05-22 18:16
 */
public class SocketServer {

    static ExecutorService executorService = newSingleThreadExecutor();

    public static void main(String[] args) throws Exception {
        //服务端在20006端口监听客户端请求的TCP连接
        ServerSocket server = new ServerSocket(20006);
        Socket client = null;
        boolean f = true;
        while (f) {
            //等待客户端的连接，如果没有获取连接
            client = server.accept();
            System.out.println("与客户端连接成功！");
            //为每个客户端连接开启一个线程
            executorService.submit(new ServerThread(client));
        }
        server.close();
    }


}
