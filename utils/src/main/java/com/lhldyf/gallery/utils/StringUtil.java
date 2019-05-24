package com.lhldyf.gallery.utils;

/**
 * @author lhldyf
 * @date 2019-05-23 19:42
 */
public class StringUtil {
    public static void main(String[] args) {
        String temp = "abc|def";
        System.out.println(temp.split("\\|")[1]);
    }
}
