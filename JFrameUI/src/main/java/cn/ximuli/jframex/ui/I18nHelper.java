package cn.ximuli.jframex.ui;

import org.springframework.context.MessageSource;
import java.util.Locale;
import java.util.Objects;

public class I18nHelper {
    private static MessageSource messageSource;

    private static Locale defaultLocale;

    public static void setMessageSource(MessageSource source, Locale locale) {
        messageSource = source;
        defaultLocale = locale;
    }

    public static String getMessage(String code) {
        return getMessage(code, defaultLocale, new Objects[]{});
    }

    public static String getMessage(String code, Object... args) {
        return getMessage(code, defaultLocale, args);
    }

    public static String getMessage(String code, Locale defaultLocale, Object... args) {
        return messageSource.getMessage(code, args, defaultLocale);
    }
}
