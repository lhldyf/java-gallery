package com.lhldyf.gallery.mvel;

import org.mvel2.MVEL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lhldyf
 * @date 2019-05-24 16:39
 */
public class MvelTest {
    public static void main(String[] args) {
        System.out.println(MVEL.eval("'a'=='a'"));
        Map<String, Object> map = new HashMap<>();
        map.put("a", 2);
        map.put("b", 12);
        System.out.println(MVEL.eval("a>b", map));
        System.out.println(MVEL.eval("(a==b || a !=b) && a != b", map));


        List<String> list = new ArrayList<>();
    }
}
