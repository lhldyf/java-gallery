package com.lhldyf.gallery.utils.test;

/**
 * @author lhldyf
 * @date 2019-06-19 19:02
 */
public class ExceptionTest {

    public static final int count = 10000000;

    public static void main(String[] args) {
        int a;
        long time1 = System.nanoTime();
        for (int i = 0; i < count; i++) {
            try {
                a = i / 0;
            } catch (Exception e) {
            }
        }
        long time2 = System.nanoTime();
        System.out.println(time2 - time1);

        for (int i = 0; i < count; i++) {
            try {
                a = i / 1;
            } catch (Exception e) {
            }
        }

        long time3 = System.nanoTime();
        System.out.println(time3 - time2);

        System.out.println((time2 - time1) / (time3 - time2));
    }
}
