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

}
