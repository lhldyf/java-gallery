package com.lhldyf.gallery.regex;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lhldyf.gallery.regex.RegexConstant.MATCH_JSON_KEY;

/**
 * @author lhldyf
 * @date 2019-05-22 10:46
 */
public class RegexUtil {
    private RegexUtil() {}

    public static boolean match(Pattern pattern, String content) {
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }

    public static boolean match(String pattern, String content) {
        return match(Pattern.compile(pattern), content);
    }

    public static List<String> getMatchValues(Pattern pattern, String content) {
        Matcher matcher = pattern.matcher(content);
        List<String> list = new LinkedList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    public static List<String> getMatchValues(String pattern, String content) {
        return getMatchValues(Pattern.compile(pattern), content);
    }


    public static List<String> getMatchValuesInJson(String key, String content) {
        String pattern = String.format(MATCH_JSON_KEY, key);
        return getMatchValues(pattern, content);
    }

}
