package com.lhldyf.gallery.utils;

import java.util.Arrays;

/**
 * @author lhldyf
 * @date 2019-05-23 19:42
 */
public class StringUtil {
    public static void main(String[] args) {
        String temp = "abc|def";
        System.out.println(temp.split("\\|")[1]);

        String str = "a,b,c,d,e,f";
        Arrays.stream(str.split(",", 5)).forEach(System.out::println);

        String str1 = "20190528101112,error||127.0.0.1||10";
        Arrays.stream(str1.split("\\|\\|")).forEach(System.out::println);
    }
}
