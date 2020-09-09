package com.lhldyf.gallery.utils.test;

import java.util.HashSet;

/**
 * @author lhldyf
 * @date 2020-03-25 16:20
 */
public class IDCardTest {

    static HashSet<String>[] hashSets = new HashSet[12];

    static void init() {
        for (int i = 0; i < 12; i++) {
            hashSets[i] = new HashSet<>();
        }
    }

    public static void main(String[] args) {

        init();


        for (int i = 0; i < 100000000; i++) {
            int temp = i % 12 + 1;
            if (temp >= 10) {
                put("3607311993" + temp + i);
            } else {
                put("36073119930" + temp + i);
            }
        }

        printTotal();
    }

    static void put(String str) {
        Integer integer = Integer.valueOf(str.substring(10, 12));
        hashSets[integer - 1].add(str);
    }

    static void printTotal() {
        int total = 0;
        for (int i = 0; i < hashSets.length; i++) {
            total += hashSets[i].size();
        }
        System.out.println("total: " + total);
    }
}
