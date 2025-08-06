package cn.ximuli.jframex.common.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

/**
 * Date Utility Class
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class DateUtil {

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * Check if the date is within the given range
     */
    public static boolean isIn(Date date, Date beginDate, Date endDate) {
        long beginMills = beginDate.getTime();
        long endMills = endDate.getTime();
        long thisMills = date.getTime();
        return thisMills >= Math.min(beginMills, endMills) && thisMills <= Math.max(beginMills, endMills);
    }

    // TODO This method is a bit hasty, temporarily written this way
    public static Date parse(long time) {
        return new Date(time);
    }

    /**
     * Check if the date is after the given date
     */
    public static boolean isAfter(Date now, Date endTime) {
        if (null == now) {
            throw new NullPointerException("Date to compare is null !");
        }
        return now.getTime() > endTime.getTime();
    }

    /**
     * Check if the date is after current time
     */
    public static boolean isAfterNow(Date target) {
        return isAfter(target, new Date());
    }

    /**
     * Check if the LocalDateTime is after current time
     */
    public static boolean isAfterNow(LocalDateTime target) {
        return target.isAfter(LocalDateTime.now());
    }

    /**
     * Add time to current time
     */
    public static LocalDateTime plusTime(long pulsCount, TemporalUnit unit) {
        return plusTime(LocalDateTime.now(), pulsCount, unit);
    }

    /**
     * Add time to the given start time
     */
    public static LocalDateTime plusTime(LocalDateTime start, long pulsCount, TemporalUnit unit) {
        return start.plus(pulsCount, unit);
    }

    /**
     * Calculate the time difference between two dates
     */
    public static long between(LocalDateTime start, LocalDateTime endDate, TemporalUnit unit, boolean isAbs) {
        Duration between = Duration.between(endDate, LocalDateTime.now());
        long ret = between.get(unit);
        return isAbs ? Math.abs(ret) : ret;
    }

    /**
     * Calculate the time difference in seconds between the given date and current time
     */
    public static long betweenNowSec(LocalDateTime endDate) {
        return between(LocalDateTime.now(), endDate, ChronoUnit.SECONDS, true);
    }

    /**
     * Format LocalDateTime with the given pattern
     */
    public static String formatTime(LocalDateTime dateTime, String ofPattern) {
        return DateTimeFormatter.ofPattern(ofPattern).format(dateTime);
    }

    /**
     * Format current time with default pattern
     */
    public static String formatTimeNow() {
        return formatTime(LocalDateTime.now(), DEFAULT_PATTERN);
    }

    /**
     * Convert LocalDateTime to Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Convert timestamp to LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Long timesLong) {
        Date date = new Date(timesLong);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
