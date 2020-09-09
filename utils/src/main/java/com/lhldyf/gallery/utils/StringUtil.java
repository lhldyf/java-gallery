package com.lhldyf.gallery.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;

/**
 * @author lhldyf
 * @date 2019-05-23 19:42
 */
public class StringUtil {
    public static void main(String[] args) {
        String temp = "abc|def";
        System.out.println(temp.split("\\|")[1]);

        String str = "a,b,c,d,e,f";
        Arrays.stream(str.split(",", 5)).forEach(System.out::println);

        String str1 = "20190528101112,error||127.0.0.1||10";
        Arrays.stream(str1.split("\\|\\|")).forEach(System.out::println);

        String str2 = "1,2,3";
        System.out.println(str2.substring(0, str2.indexOf(",")));


        StringBuffer sb = new StringBuffer("2,/usr/share/kxf-service/logs/error.log,20190531155200,1000\n");
        sb.append("lhldyf hello\n");
        sb.append("这里才结束，一共两行日志 FENDFEND");
        String message = sb.toString();
        int size = Integer.valueOf(message.substring(0, message.indexOf(",")));
        int logRecord = message.split("\n").length;
        String[] mArray = message.split("\n");
        String metadata = mArray[0];
        String logPath = metadata.split(",")[1];
        String logName = logPath.substring(logPath.lastIndexOf("/") + 1);

        System.out.println(size);
        System.out.println(logRecord);
        System.out.println(logName);

        System.out.println(String.format("% 4d", 1));
        System.out.println(String.format("%04d", 1));

        StringBuilder stringBuffer = new StringBuilder("lhldyf");
        stringBuffer.insert(0, "hhh");
        stringBuffer.insert(0, String.format("% 4d", stringBuffer.length()));

        System.out.println(stringBuffer);

        Long second = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        Date date = new Date(second);
        System.out.println(second);
        System.out.println(date.getTime());
        System.out.println(DateUtil.formatDate(new Date(second), DateUtil.DATE_SHORT_YYYYMMDDHHMMSS));
    }
}
