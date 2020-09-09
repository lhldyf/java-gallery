package com.lhldyf.gallery.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Zhang Peng
 * @date 2018-12-05
 */
public class DateUtil {
    public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_SHORT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public final static String DATE_FORMAT_YYYYMMDD = "yyyy-MM-dd";

    public final static String DATE_FORMAT_HH = "HH";

    public final static String DATE_FORMAT_MMDD = "MM-dd";

    public static LocalDateTime date2LocalDateTime(Date date) {
        // A time-zone ID, such as {@code Europe/Paris}.(时区)
        Instant instant = date.toInstant(); // Date转Instant:
        // A time-zone ID, such as {@code Europe/Paris}.(时区)
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static String formatTansferShort(String date) {
        return transfer(date, DATE_FORMAT_YYYYMMDDHHMMSS, DATE_SHORT_YYYYMMDDHHMMSS);
    }

    public static String shortTansferFormat(String date) {
        return transfer(date, DATE_SHORT_YYYYMMDDHHMMSS, DATE_FORMAT_YYYYMMDDHHMMSS);
    }

    private static String transfer(String date, String param, String result) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(param);
        SimpleDateFormat resultDateFormat = new SimpleDateFormat(result);
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultDateFormat.format(date1);
    }



    /**
     * 获取当前时间过去指定小时内的时列表
     * @param lastHourNum
     * @return
     */
    public static List<String> getLastHourStrs(Integer lastHourNum, boolean isAsc) {
        List<String> result = new ArrayList<>();

        Date currentTime = getDate();

        if (isAsc) {
            for (int i = lastHourNum - 1; i >= 0; i--) {
                Date tempTime = addHours(currentTime, 0 - i);
                result.add(formatDate(tempTime, DATE_FORMAT_HH));
            }
        } else {
            for (int i = 0; i < lastHourNum; i++) {
                Date tempTime = addHours(currentTime, 0 - i);
                result.add(formatDate(tempTime, DATE_FORMAT_HH));
            }
        }

        return result;
    }

    /**
     * 获取当前时间过去指定天数内的日期列表
     * @param lastDayNum
     * @return
     */
    public static List<String> getLastDaysStrs(Integer lastDayNum, boolean isAsc) {
        List<String> result = new ArrayList<>();

        Date currentTime = getDate();
        if (isAsc) {
            for (int i = lastDayNum - 1; i >= 0; i--) {
                Date tempTime = addDays(currentTime, 0 - i);
                result.add(formatDate(tempTime, DATE_FORMAT_MMDD));
            }
        } else {
            for (int i = 0; i < lastDayNum; i++) {
                Date tempTime = addDays(currentTime, 0 - i);
                result.add(formatDate(tempTime, DATE_FORMAT_MMDD));
            }
        }
        return result;
    }

    /**
     * 生成当前时间date
     * @return
     */
    public static Date getDate() {
        return new Date();
    }

    /**
     * 生成指定时间date
     * @return
     */
    public static Date getDate(Long time) {
        return new Date(time);
    }

    /**
     * 格式化时间为字符串
     * @param date
     * @param dateFormat
     * @return
     */
    public static String formatDate(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);
    }

    /**
     * 解析str转成date
     * @param dateStr
     * @param dateFormat
     * @return
     */
    public static Date parseDate(String dateStr, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 计算两个日期（不考虑time）相差天数（取正）
     * @param dateA
     * @param dateB
     * @return
     */
    public static Integer calculate2DayDiff(Date dateA, Date dateB) {
        Date compareDateA = parseDate(formatDate(dateA, DATE_FORMAT_YYYYMMDD), DATE_FORMAT_YYYYMMDD);
        Date compareDateB = parseDate(formatDate(dateB, DATE_FORMAT_YYYYMMDD), DATE_FORMAT_YYYYMMDD);

        long day = (compareDateB.getTime() - compareDateA.getTime()) / transDay2MiSecond(1);
        return Long.valueOf(Math.abs(day)).intValue();
    }

    /**
     * 日期操作：加秒
     * @param date
     * @param seconds
     * @return
     */
    public static Date addSeconds(Date date, Integer seconds) {
        Date result = new Date(date.getTime() + transSecond2MiSecond(seconds));
        return result;
    }

    /**
     * 日期操作：加分
     * @param date
     * @param minutes
     * @return
     */
    public static Date addMinutes(Date date, Integer minutes) {
        Date result = new Date(date.getTime() + transMinutes2MiSecond(minutes));
        return result;
    }

    /**
     * 日期操作：加时
     * @param date
     * @param hours
     * @return
     */
    public static Date addHours(Date date, Integer hours) {
        Date result = new Date(date.getTime() + transHour2MiSecond(hours));
        return result;
    }

    /**
     * 日期操作：加天
     * @param date
     * @param days
     * @return
     */
    public static Date addDays(Date date, Integer days) {
        Date result = new Date(date.getTime() + transDay2MiSecond(days));
        return result;
    }

    private static Long transSecond2MiSecond(Integer seconds) {
        return Long.valueOf((Long.valueOf(seconds) * 1000));
    }

    private static Long transMinutes2MiSecond(Integer minutes) {
        return Long.valueOf((transSecond2MiSecond(minutes * 60)));
    }

    private static Long transHour2MiSecond(Integer hour) {
        return Long.valueOf((transMinutes2MiSecond(hour * 60)));
    }

    private static Long transDay2MiSecond(Integer day) {
        return Long.valueOf((transHour2MiSecond(day * 24)));
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.formatDate(new Date(1561343764889L), DATE_SHORT_YYYYMMDDHHMMSS));
    }
}
