package com.lhldyf.gallery.java.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lhldyf
 * @date 2020-04-10 22:08
 */
public class ReadTest {

    static List list = new ArrayList();


    public static class MyReadListener extends AnalysisEventListener<Map<Integer, String>> {
        @Override
        public void invoke(Map<Integer, String> data, AnalysisContext context) {
            list.add(data);
            System.out.println(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            System.out.println("读取完成");
        }
    }

    public static void main(String[] args) {

        EasyExcel.read("E:\\var\\test\\1.xlsx", new MyReadListener()).sheet(0).doRead();
        System.out.println(list);
    }
}
