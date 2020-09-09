package com.lhldyf.gallery.utils.test;

/**
 * @author lhldyf
 * @date 2019-07-02 16:00
 */
public class TimeTest {
    public static void main(String[] args) {
        for (int i = 0; i < (args.length > 0 ? Integer.valueOf(args[0]) : 1); i++) {
            new Thread(() -> {
                while (true) {
                    long time = System.currentTimeMillis();
                    try {
                        if (args.length > 1) {
                            Thread.sleep(Integer.valueOf(args[1]));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
