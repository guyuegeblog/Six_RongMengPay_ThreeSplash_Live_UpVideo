package com.app.Tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ASUS on 2016/12/6.
 */
public class DateTool {


    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 将时间字符串转换成Date
     */
    public static Date str2Date(String str, String format) {
        if (str == null || str.length() == 0) {
            return null;
        }
        if (format == null || format.length() == 0) {
            format = FORMAT;
        }
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;

    }

    public static Date str2Date(String str) {
        return str2Date(str, null);
    }

    /**
     * 将时间字符串转换成Calendar
     *
     * @param str
     * @param format
     * @return
     */
    public static Calendar str2Calendar(String str, String format) {

        Date date = str2Date(str, format);
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;

    }

    public static Calendar str2Calendar(String str) {
        return str2Calendar(str, null);

    }

    public static String date2Str(Calendar c) {// yyyy-MM-dd HH:mm:ss
        return date2Str(c, null);
    }

    public static String date2Str(Date d) {// yyyy-MM-dd HH:mm:ss
        return date2Str(d, null);
    }

    public static String date2Str(Calendar c, String format) {
        if (c == null) {
            return null;
        }
        return date2Str(c.getTime(), format);
    }


    public static String date2Str(Date d, String format) {// yyyy-MM-dd HH:mm:ss
        if (d == null) {
            return null;
        }
        if (format == null || format.length() == 0) {
            format = FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String s = sdf.format(d);
        return s;
    }

    /**
     * 获得当前日期的字符串格式
     * 2016-05-01
     *
     * @return
     */
    public static String getCurDateStr() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-"
                + c.get(Calendar.DAY_OF_MONTH) + "-"
                + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE)
                + ":" + c.get(Calendar.SECOND);
    }

    /**
     * 获得当前日期的字符串格式
     */
    public static String getCurDateStr(String format) {
        Calendar c = Calendar.getInstance();
        return date2Str(c, format);
    }

    /**
     * 获得当前日期的字符串格式,格式到秒
     *
     * @return time -> yyyy-MM-dd-HH-mm-ss
     */
    public static String getMillon(long time) {

        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(time);

    }

    /**
     * 格式到天
     *
     * @return time -> yyyy-MM-dd
     */
    public static String getDay(long time) {

        return new SimpleDateFormat("yyyy-MM-dd").format(time);

    }

    /**
     * 格式到毫秒
     *
     * @return time -> yyyy-MM-dd-HH-mm-ss-SSS
     */
    public static String getSMillon(long time) {

        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(time);

    }


    /**
     * 输入的是String，格式诸如20120102，实现加一天的功能，返回的格式为String，诸如20120103
     */
    public static String strAddOneDay(String str) throws ParseException {
        String year = str.substring(0, 4);
        String month = str.substring(4, 6);
        String day = str.substring(6);
        String date1 = year + "-" + month + "-" + day;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse(date1);
        Calendar cd = Calendar.getInstance();
        cd.setTime(startDate);
        cd.add(Calendar.DATE, 1);
        String dateStr = sdf.format(cd.getTime());
        String year1 = dateStr.substring(0, 4);
        String month1 = dateStr.substring(5, 7);
        String day1 = dateStr.substring(8);
        return year1 + month1 + day1;
    }

    /**
     * 输入的是String，格式诸如20120102，实现减一天的功能，返回的格式为String，诸如20120101
     */
    public static String strDecreaseOneDay(String row) throws ParseException {
        String year = row.substring(0, 4);
        String month = row.substring(4, 6);
        String day = row.substring(6);
        String date1 = year + "-" + month + "-" + day;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse(date1);
        Calendar cd = Calendar.getInstance();
        cd.setTime(startDate);
        cd.add(Calendar.DATE, -1);
        String dateStr = sdf.format(cd.getTime());
        String year1 = dateStr.substring(0, 4);
        String month1 = dateStr.substring(5, 7);
        String day1 = dateStr.substring(8);
        return year1 + month1 + day1;
    }

    /**
     * 输入的格式为String，诸如20120101，返回的格式为String，诸如2012-01-01
     */
    public static String stringDateChange(String date) {
        if (date.length() == "20120101".length()) {
            String year = date.substring(0, 4);
            String month = date.substring(4, 6);
            String day = date.substring(6);
            return year + "-" + month + "-" + day;
        } else {
            return date;
        }


    }

    /**
     * 获取昨天 Data
     *
     * @param date
     * @return
     */
    public static Date getLastdayDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    /**
     * 获取明天Date
     *
     * @param date
     * @return
     */
    public static Date getNextdayDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     * 判断是否是同一天
     *
     * @param one
     * @param another
     * @return
     */
    public static boolean isTheSameDay(Date one, Date another) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(one);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(another);
        int oneDay = calendar.get(Calendar.DAY_OF_YEAR);
        int anotherDay = calendar2.get(Calendar.DAY_OF_YEAR);
        return oneDay == anotherDay;
    }

    /***
     * 获取当前系统时间
     */
    public String getSystemTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());
        String time = sdf.format(new Date());
        return time;
    }

    /***
     * 比对时间大小
     */
    public static String compareTime(String currentSysTime, String vipTime) {
        //result  1过期 2未过期
        String result = "1";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Calendar sysC1 = Calendar.getInstance();
        Calendar vipC2 = Calendar.getInstance();
        try {
            sysC1.setTime(sdf.parse(currentSysTime));
            vipC2.setTime(sdf.parse(vipTime));
        } catch (java.text.ParseException e) {
            System.err.println("格式不正确");
        }
        int code = sysC1.compareTo(vipC2);
        if (code == 0)
            //System.out.println("c1相等c2");
            result = "1";
        else if (code < 0)
            //System.out.println("c1小于c2");
            result = "2";
        else
            //System.out.println("c1大于c2");
            result = "1";
        return result;
    }


    /***
     * 比对时间大小
     */
    public static String compareTime2(String currentSysTime, String vipTime) {
        //result  1过期 2未过期
        String result = "1";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar sysC1 = Calendar.getInstance();
        Calendar vipC2 = Calendar.getInstance();
        try {
            sysC1.setTime(sdf.parse(currentSysTime));
            vipC2.setTime(sdf.parse(vipTime));
        } catch (java.text.ParseException e) {
            System.err.println("格式不正确");
        }
        int code = sysC1.compareTo(vipC2);
        if (code == 0)
            //System.out.println("c1相等c2");
            result = "1";
        else if (code < 0)
            //System.out.println("c1小于c2");
            result = "2";
        else
            //System.out.println("c1大于c2");
            result = "1";
        return result;
    }

    /***
     * 比对时间大小
     */
    public static String compareTimeByFormat(String currentSysTime, String vipTime, SimpleDateFormat sdf) {
        //result  1过期 2未过期
        String result = "1";
        Calendar sysC1 = Calendar.getInstance();
        Calendar vipC2 = Calendar.getInstance();
        try {
            sysC1.setTime(sdf.parse(currentSysTime));
            vipC2.setTime(sdf.parse(vipTime));
        } catch (java.text.ParseException e) {
            System.err.println("格式不正确");
        }
        int code = sysC1.compareTo(vipC2);
        if (code == 0)
            //System.out.println("c1相等c2");
            result = "1";
        else if (code < 0)
            //System.out.println("c1小于c2");
            result = "2";
        else
            //System.out.println("c1大于c2");
            result = "1";
        return result;
    }

    /***
     * 计算2个日期的差值
     *
     * @return
     */
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public static long[] getTime(Date endDate, Date curDate) {
        long[] time = new long[4];
        if (curDate == null || endDate == null) {
            return null;
        }
        long diff = endDate.getTime() - curDate.getTime();//毫秒

        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        long second = diff / 1000;
        time[0] = days;//天数
        time[1] = hours;//小时
        time[2] = minutes;//分钟
        time[3] = second;

        return time;
    }
}
