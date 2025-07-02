package cn.ximuli.jframex.ui;

import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.model.UserType;
import cn.ximuli.jframex.model.constants.Sex;
import cn.ximuli.jframex.model.constants.Status;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import java.util.Locale;

public class I18nHelper {

    private static final String commonPrefix = "app.message.table";
    private static MessageSource messageSource;

    private static Locale defaultLocale;

    private I18nHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void setMessageSource(MessageSource source, Locale locale) {
        messageSource = source;
        defaultLocale = locale;
    }

    public static String getMessage(String code) {
        return getMessage(code, defaultLocale, new Object[]{});
    }

    public static String getMessage(String code, Object... args) {
        return getMessage(code, defaultLocale, args);
    }

    public static String getMessage(String code, Locale defaultLocale, Object... args) {
        return messageSource.getMessage(code, args, defaultLocale);
    }
    public static boolean has(String code) {
        try {
            getMessage(code);
            return true;
        } catch (NoSuchMessageException e) {
            return false;
        }
    }

    public static String convert(UserType type) {
        String lowerCase = type.getName().toLowerCase();
        String messageCode = StringUtil.joinWith(".", commonPrefix, type.getClass().getSimpleName() + lowerCase);
        return getMessage(messageCode);
    }

    public static String i8nConvert(Status status) {
        return getMessage(status.getMessageCode());
    }

    public static String i8nConvert(Sex sex) {
        String messageCode = StringUtil.joinWith(".", commonPrefix, sex.toString());
        return getMessage(messageCode);
    }

}
