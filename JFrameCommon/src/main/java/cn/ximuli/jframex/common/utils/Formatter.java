package cn.ximuli.jframex.common.utils;

/**
 * Formatter Utility Class
 * Provides string formatting functionality
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class Formatter {
    /**
     * Format string with arguments
     *
     * @param strPattern String pattern with placeholders
     * @param argArray Arguments to replace placeholders
     * @return Formatted string
     */
    public static String format(final String strPattern, final Object... argArray) {
        return ParameterFormatter.format(strPattern, argArray);
    }
}
