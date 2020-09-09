package com.lhldyf.gallery.regex.test;

import java.util.regex.Matcher;

/**
 * @author lhldyf
 * @date 2019-11-08 14:17
 */
public class CommonTest {
    public static void main(String[] args) {
        System.out.println("aaa $test bbb".replaceAll(Matcher.quoteReplacement("$test"), "123"));
    }
}
