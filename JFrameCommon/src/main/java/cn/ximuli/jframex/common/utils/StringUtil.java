package cn.ximuli.jframex.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * String Utility Class
 * Extends Apache Commons StringUtils with additional functionality
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class StringUtil extends StringUtils {
    /**
     * Remove specified prefix ignoring case
     *
     * @param str    String
     * @param prefix Prefix
     * @return String after cutting, if prefix is not prefix, return original string
     */
    public static String removePrefixIgnoreCase(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str == null ? "" : str.toString();
        }

        final String str2 = str.toString();
        if (str2.toLowerCase().startsWith(prefix.toString().toLowerCase())) {
            return str2.substring(prefix.length(), str.length());
        }
        return str2;
    }

    /**
     * Check if array is not empty
     *
     * @param objects Object array
     * @return true if not empty, false otherwise
     */
    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }

    /**
     * Check if array is empty
     *
     * @param objects Object array
     * @return true if empty, false otherwise
     */
    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || (objects.length == 0);
    }

    /**
     * Check if object is null
     *
     * @param object Object
     * @return true if null, false otherwise
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * Check if string is contained in string array (ignore case)
     *
     * @param str  String to validate
     * @param strs String array
     * @return true if contained, false otherwise
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Convert strings to list
     *
     * @param str String array
     * @return List of strings
     */
    public static List<String> str2List(String ... str) {
        return Arrays.stream(str).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Convert string to set
     *
     * @param str String
     * @param sep Separator
     * @return Set collection
     */
    public static Set<String> str2Set(String str, String sep) {
        return new HashSet<String>(str2List(str, sep, true, false));
    }

    /**
     * Convert string to list
     *
     * @param str         String
     * @param sep         Separator
     * @param filterBlank Filter blank strings
     * @param trim        Trim leading and trailing whitespace
     * @return List collection
     */
    public static List<String> str2List(String str, String sep, boolean filterBlank, boolean trim) {
        List<String> list = new ArrayList<String>();
        if (StringUtil.isEmpty(str)) {
            return list;
        }

        // Filter blank strings
        if (filterBlank && StringUtil.isBlank(str)) {
            return list;
        }
        String[] split = str.split(sep);
        for (String string : split) {
            if (filterBlank && StringUtil.isBlank(string)) {
                continue;
            }
            if (trim) {
                string = string.trim();
            }
            list.add(string);
        }

        return list;
    }
}
