package org.bem.iot.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /**
     * 获取指定年月的当月天数
     * @param year 年
     * @param month 月
     * @return 当月天数
     */
    public static int getMonthDayCount(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 时间戳转日期字符串
     * @param timestamp 时间戳
     * @param formatDate 输出格式
     * @return 日期字符串
     */
    public static String timestampToString(long timestamp, String formatDate) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
        return sdf.format(date);
    }



    /**
     * 时间戳转日期字符串
     * @param timestamp 时间戳
     * @return 日期字符串
     */
    public static String timeToMinute(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }
}
