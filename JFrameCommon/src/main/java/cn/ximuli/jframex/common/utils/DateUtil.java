package cn.ximuli.jframex.common.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author lizhipeng
 */
public class DateUtil {

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static boolean isIn(Date date, Date beginDate, Date endDate) {
        long beginMills = beginDate.getTime();
        long endMills = endDate.getTime();
        long thisMills = date.getTime();
        return thisMills >= Math.min(beginMills, endMills) && thisMills <= Math.max(beginMills, endMills);
    }

    // TODO 这个方法有点草率，先暂时这么写
    public static Date parse(long time) {
        return new Date(time);
    }

    /**
     * 是否在给定日期之后
     */
    public static boolean isAfter(Date now, Date endTime) {
        if (null == now) {
            throw new NullPointerException("Date to compare is null !");
        }
        return now.getTime() > endTime.getTime();
    }

    public static boolean isAfterNow(Date target) {
        return isAfter(target, new Date());
    }

    public static boolean isAfterNow(LocalDateTime target) {
        return target.isAfter(LocalDateTime.now());
    }

    public static LocalDateTime plusTime(long pulsCount, TemporalUnit unit) {
        return plusTime(LocalDateTime.now(), pulsCount, unit);
    }

    public static LocalDateTime plusTime(LocalDateTime start, long pulsCount, TemporalUnit unit) {
        return start.plus(pulsCount, unit);
    }

    public static long between(LocalDateTime start, LocalDateTime endDate, TemporalUnit unit, boolean isAbs) {
        Duration between = Duration.between(endDate, LocalDateTime.now());
        long ret = between.get(unit);
        return isAbs ? Math.abs(ret) : ret;
    }

    public static long betweenNowSec(LocalDateTime endDate) {
        return between(LocalDateTime.now(), endDate, ChronoUnit.SECONDS, true);
    }

    public static String formatTime(LocalDateTime dateTime, String ofPattern) {
        return DateTimeFormatter.ofPattern(ofPattern).format(dateTime);
    }

    public static String formatTimeNow() {
        return formatTime(LocalDateTime.now(), DEFAULT_PATTERN);
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());  
    }

    public static LocalDateTime toLocalDateTime(Long timesLong) {
        Date date = new Date(timesLong);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
