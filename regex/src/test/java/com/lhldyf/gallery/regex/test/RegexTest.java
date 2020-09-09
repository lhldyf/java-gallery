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
    public void testShuxian() {
        System.out.println(RegexUtil.match("IP\\|MAC*", "aaa|IP|MAC|aaa"));
    }

    @Test
    public void testMatchInJson() {
        List<String> resultList = RegexUtil.getMatchValuesInJson("result", "{\"result\":\"true\"}");
        for (String result : resultList) {
            System.out.println(result);
        }
    }


    // "(?<=\\[)(.+?)(?=\\])"
    @Test
    public void testMatchList() {
        List<String> resultList = RegexUtil.getMatchValues(RegexConstant.MATCH_SQUARE_BRACKET,
                                                           "[{10.00,0.00,5.00,1.00,0.00,84.00,1.20,1.00,0.50}],"
                                                                   + "[{1024,1024.00,0.00,128,64.00,50.00},{0.50,0"
                                                                   + ".40,512,400}],[{/dev/sda1,/,40,10.00,25.00, "
                                                                   + "1024000, 10240, 1.00},{/dev/sda2,/data,40,10"
                                                                   + ".00,25.00, 1024000, 10240, 1.00}]");
        Assert.assertNotNull(resultList);
        resultList.forEach(System.out::println);

        resultList.forEach(x -> {
            List<String> list2 = RegexUtil.getMatchValues(RegexConstant.MATCH_BIG_BRACKET, x);
            Assert.assertNotNull(list2);
            list2.forEach(System.out::println);
        });

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
