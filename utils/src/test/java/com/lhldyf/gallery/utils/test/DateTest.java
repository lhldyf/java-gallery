package com.lhldyf.gallery.utils.test;

import com.lhldyf.gallery.utils.DateUtil;

/**
 * @author lhldyf
 * @date 2019-06-10 10:40
 */
public class DateTest {

    public static final String DATE = "2019-05-20";

    public static String replace(String date) {
        return date.replaceAll("-", "");
    }

    public static String subString(String date) {
        return date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            replace(DATE);
            subString(DATE);
        }

        long start1 = System.nanoTime();
        for (int i = 0; i < 10000000; i++) {
            replace(DATE);
        }

        long end1 = System.nanoTime();
        for (int i = 0; i < 10000000; i++) {
            subString(DATE);
        }

        long end2 = System.nanoTime();

        System.out.println(end1 - start1);
        System.out.println(end2 - end1);
        System.out.println("replaceAll 的耗时是 subString耗时的 " + new Double(end1 - start1) / (end2 - end1) + " 倍");


        long start = DateUtil.getDate().getTime();
        long end = DateUtil.addMinutes(DateUtil.getDate(), 1).getTime();
        System.out.println(end - start);
    }
}
