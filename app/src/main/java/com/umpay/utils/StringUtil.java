package com.umpay.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xiaoyang on 2015/11/10.
 */
public class StringUtil {

    /*
       * 将字符串"分"转换成"元"（长格式），如：100分被转换为1.00元。
       */
    public static String convertCent2Dollar(String s) {
        if (StringUtil.isEmpty(s)) {
            return "";
        }

        long l = 0;
        try {
            if (s.charAt(0) == '+') {
                s = s.substring(1);
            }
            l = Long.parseLong(s);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        boolean negative = false;
        if (l < 0) {
            negative = true;
            l = Math.abs(l);
        }
        s = Long.toString(l);
        if (s.length() == 1)
            return (negative ? ("-0.0" + s) : ("0.0" + s));
        if (s.length() == 2)
            return (negative ? ("-0." + s) : ("0." + s));
        else
            return (negative ? ("-" + s.substring(0, s.length() - 2) + "." + s
                    .substring(s.length() - 2)) : (s.substring(0,
                    s.length() - 2)
                    + "." + s.substring(s.length() - 2)));
    }

    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyMMddHHmmssSSS");

    /**
     * 生成测试订单号
     * @return
     */
    public static String createOrderId(){
        return "0101" + dateTimeFormat.format(new Date());
    }

    /**
     * 是否是空的字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str)) {
            return true;
        }
        return false;
    }
}
