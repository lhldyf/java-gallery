package com.lhldyf.gallery.regex;

/**
 * @author lhldyf
 * @date 2019-05-22 10:46
 */
public class RegexConstant {
    private RegexConstant() {}
    /**
     * 匹配一个JSON字符串中key为 %s 占位符替换后的值，使用 String.format(MATCH_JSON_KEY, key); 来得到key
     */
    public static final String MATCH_JSON_KEY = "\"%s\":\"?(\\w+)\"?";

    /**
     * 匹配一个以0开头的数字的字符串，其中^表示以后一位字符开头，\\d表示数字 *表示任意长度, $表示结尾
     */
    public static final String START_WITH_NUM_0 = "^0\\d*$";

    /**
     * 匹配一个以0或2开头的，其中 | 表示或
     */
    public static final String START_WITH_NUM_0_OR_2 = "^[0,2]\\d*$";
}
