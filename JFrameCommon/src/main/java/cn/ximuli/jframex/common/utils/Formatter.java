package cn.ximuli.jframex.common.utils;

public class Formatter {
    public static String format(final String strPattern, final Object... argArray) {
        return ParameterFormatter.format(strPattern, argArray);
    }

}
