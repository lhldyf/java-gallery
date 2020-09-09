package com.lhldyf.gallery.utils.test;

import com.alibaba.fastjson.JSON;
import com.lhldyf.gallery.utils.test.entity.ObjEntity;

/**
 * @author lhldyf
 * @date 2019-07-09 10:03
 */
public class JSONTest {

    public static void main(String[] args) {
        String string = "{\"labelFlex\":12,\"inputFlex\":13,\"itemLists\":[[{\"label\":\"创建时间\",\"type\":\"Input\"},"
                + "{\"label\":\"主  键  \",\"type\":\"Input\"},{\"label\":\"真实名  \",\"type\":\"Input\"},"
                + "{\"label\":\"更新者  \",\"type\":\"Input\"}],[{\"label\":\"创建者  \",\"type\":\"Input\"},{\"label\":\"邮"
                + "  编  \",\"type\":\"Input\"},{\"label\":\"状  态  \",\"type\":\"Input\"},{\"label\":\"用户名  \","
                + "\"type\":\"Input\"}],[{\"label\":\"邮  箱  \",\"type\":\"Input\"},{\"label\":\"手机号  \","
                + "\"type\":\"Input\"},{\"label\":\"电  话  \",\"type\":\"Input\"}]]}";

        ObjEntity objEntity = JSON.parseObject(string, ObjEntity.class);
        objEntity.getItemLists().forEach(System.out::println);

        System.out.println(JSON.toJSONString(new double[] {0.1, 0.22}));
        System.out.println(JSON.toJSONString(new String[] {"1", "2"}));

    }
}
