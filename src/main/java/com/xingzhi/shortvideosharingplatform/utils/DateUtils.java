package com.xingzhi.shortvideosharingplatform.utils;

import java.time.LocalDate;

public class DateUtils {
    // 计算 LocalDate 对应的 Julian Day
    private static long toJulianDay(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        if (month < 3) {
            month += 12;
            year--;
        }

        int a = year / 100;
        int b = 2 - a + a / 4;

        return (long) ((long) (365.25 * (year + 4716)) +
                        (long) (30.6001 * (month + 1)) +
                        day + b - 1524.5);
    }

    // 计算两个 LocalDate 之间的天数差
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return (long) Math.floor(toJulianDay(endDate) - toJulianDay(startDate));
    }
}