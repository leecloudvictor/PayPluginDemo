package com.umpay.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.CHINA);
        return sdf.format(new Date());
    }

    public static SimpleDateFormat curDate = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
    public static String getCurDate() {
        return curDate.format(new Date());
    }
    public static SimpleDateFormat curTime = new SimpleDateFormat("HHmmss", Locale.CHINA);
    public static String getCurTime() {
        return curTime.format(new Date());
    }

    public static String getCurDate_() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(new Date());
    }
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
    public static String getDate(String date) {
        try {
            Date parse = curDate.parse(date);
            return dateFormat.format(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat.format(new Date());
    }

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
    public static SimpleDateFormat timeFormat1 = new SimpleDateFormat("HHmmss", Locale.CHINA);

    public static String getTime(String time) {
        try {
            Date parse = timeFormat1.parse(time);
            return timeFormat.format(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeFormat.format(new Date());
    }
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
    /**
     * 获取一个 格式为yyMMddHHmmssSSS 的时间字符串
     *
     * @return
     */
    public static String getDateStr() {
        return dateTimeFormat.format(new Date());
    }
    private static SimpleDateFormat dateTimeFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String getDateStr(long date) {
        return dateTimeFormat1.format(new Date(date));
    }

    public static String getDateAfter(int day) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
        Date d = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        String str = formatter.format(now.getTime());
        return str;
    }

    public static String getDateAfterOther(int day) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        Date d = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        String str = formatter.format(now.getTime());
        return str;
    }

    public static String getCurDate_2() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
        return sdf.format(Calendar.getInstance().getTime());
    }
}
