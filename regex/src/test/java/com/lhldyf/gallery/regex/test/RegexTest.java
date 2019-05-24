package com.lhldyf.gallery.regex.test;

import com.lhldyf.gallery.regex.RegexConstant;
import com.lhldyf.gallery.regex.RegexUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author lhldyf
 * @date 2019-05-22 10:51
 */
public class RegexTest {

    @Test
    public void testMatchInJson() {
        List<String> resultList = RegexUtil.getMatchValuesInJson("result", "{\"result\":\"true\"}");
        for (String result : resultList) {
            System.out.println(result);
        }
    }

    @Test
    public void testOr() {
        // 用竖线表示或关系
        Assert.assertTrue(RegexUtil.match("f(oo|ee)t", "foot"));

        Assert.assertTrue(RegexUtil.match("error|warn", "2019:05:22|error|info|warn"));
        Assert.assertFalse(RegexUtil.match("error|warn", "2019:05:22|info"));
    }

    @Test
    public void testStartWithNum() {
        Assert.assertTrue(RegexUtil.match(RegexConstant.START_WITH_NUM_0, "001"));
        Assert.assertFalse(RegexUtil.match(RegexConstant.START_WITH_NUM_0, "100"));
        Assert.assertFalse(RegexUtil.match(RegexConstant.START_WITH_NUM_0, "000a"));
        Assert.assertTrue(RegexUtil.match(RegexConstant.START_WITH_NUM_0_OR_2, "200"));
        Assert.assertTrue(RegexUtil.match(RegexConstant.START_WITH_NUM_0_OR_2, "001"));
        Assert.assertFalse(RegexUtil.match(RegexConstant.START_WITH_NUM_0_OR_2, "101"));
    }
}
