package cn.ximuli.jframex.ui;

import cn.ximuli.jframex.common.exception.CommonException;
import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.model.UserType;
import cn.ximuli.jframex.model.constants.Sex;
import cn.ximuli.jframex.model.constants.Status;
import cn.ximuli.jframex.ui.storage.JFramePref;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class I18nHelper {
    // 定义资源路径模式
    private static final String RESOURCE_PATTERN = "classpath:i18n/messages*.properties";
    // 正则表达式匹配 messages_xx_YY.properties 或 messages_xx.properties
    private static final Pattern LOCALE_PATTERN = Pattern.compile("messages[_-]([a-zA-Z]{2,3})(?:[_-]([A-Z]{2}))?\\.properties");

    private static final String commonPrefix = "app.message.table";
    private static ReloadableResourceBundleMessageSource messageSource;
    public static final List<Locale> SUPPORT_LANGUAGES = new ArrayList<>();
    @Getter
    private static Locale defaultLocale;

    private I18nHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void init() throws Exception {
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasenames("classpath:i18n/messages");
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
        SUPPORT_LANGUAGES.addAll(getSupportedLocales());
        defaultLocale = getLocal();
        reloadableResourceBundleMessageSource.setDefaultLocale(defaultLocale);
        messageSource = reloadableResourceBundleMessageSource;
    }


    public static String getMessage(String code) {
        return getMessage(code, defaultLocale);
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


    private static Locale getLocal() {
        String appLanguage = JFramePref.state.get(Application.APP_LANGUAGE, System.getProperty(Application.APP_LANGUAGE, Locale.getDefault().getLanguage()));
        Locale locale = Locale.forLanguageTag(appLanguage);

        for (Locale supportLanguage : SUPPORT_LANGUAGES) {
            if (supportLanguage.getLanguage().equals(locale.getLanguage())) {
                log.info("Service language: {}", supportLanguage.getLanguage());
                return supportLanguage;
            }
        }
        return defaultLocale;
    }

    public static String[] getAllSupportLanguages() {
        Set<Locale> result = new LinkedHashSet<>();
        result.add(defaultLocale);
        for (Locale supportLanguage : SUPPORT_LANGUAGES) {
            result.add(supportLanguage);
        }
        return result.stream().map(Locale::getLanguage).toArray(String[]::new);
    }


    public static List<Locale> getSupportedLocales() throws Exception {
        List<Locale> locales = new ArrayList<>();

        // 使用 ResourcePatternResolver 扫描文件
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(RESOURCE_PATTERN);

        for (Resource resource : resources) {
            // 获取文件名
            String filename = resource.getFilename();
            if (filename != null) {
                // 使用正则表达式解析语言和国家代码
                Matcher matcher = LOCALE_PATTERN.matcher(filename);
                if (matcher.matches()) {
                    String language = matcher.group(1); // 语言代码，如 "en"
                    String country = matcher.group(2); // 国家代码，如 "US"（可能为空）
                    // 创建 Locale 对象
                    Locale locale;
                    if (country != null && !country.isEmpty()) {
                        locale = new Locale(language, country);
                    } else {
                        locale = Locale.forLanguageTag(language);
                    }
                    locales.add(locale);
                }
            }
        }

        return locales;
    }

}
