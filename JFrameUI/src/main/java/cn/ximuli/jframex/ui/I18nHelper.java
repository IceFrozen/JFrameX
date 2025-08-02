package cn.ximuli.jframex.ui;

import ch.qos.logback.classic.spi.EventArgUtil;
import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.model.UserType;
import cn.ximuli.jframex.model.constants.Sex;
import cn.ximuli.jframex.model.constants.Status;
import cn.ximuli.jframex.ui.storage.JFramePref;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for internationalization (i18n) support, managing locale-specific messages.
 * Provides methods to initialize message sources, retrieve messages, and convert enums to localized strings.
 */
@Slf4j
public class I18nHelper {
    private static final String RESOURCE_PATTERN = "classpath:i18n/messages*.properties";
    private static final Pattern LOCALE_PATTERN = Pattern.compile("messages[_-]([a-zA-Z]{2,3})(?:[_-]([A-Z]{2}))?\\.properties");
    private static final String COMMON_PREFIX = "app.message.table";

    private static ReloadableResourceBundleMessageSource messageSource;
    public static final List<Locale> SUPPORT_LANGUAGES = new ArrayList<>();
    @Getter
    private static Locale currentLocale;

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException Always thrown to prevent instantiation
     */
    private I18nHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Initializes the i18n message source and supported locales.
     *
     * @throws Exception If an error occurs during resource loading
     */
    public static void init() throws Exception {
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasenames("classpath:i18n/messages");
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
        SUPPORT_LANGUAGES.addAll(getSupportedLocales());
        currentLocale = getLocale();
        reloadableResourceBundleMessageSource.setDefaultLocale(currentLocale);
        messageSource = reloadableResourceBundleMessageSource;
        Locale.setDefault(currentLocale);
    }

    /**
     * Retrieves a localized message for the given code using the default locale.
     *
     * @param code Message key
     * @return Localized message
     */
    public static String getMessage(String code) {
        return getMessage(code, currentLocale);
    }

    /**
     * Retrieves a localized message with arguments using the default locale.
     *
     * @param code Message key
     * @param args Arguments for message placeholders
     * @return Localized message with formatted arguments
     */
    public static String getMessage(String code, Object... args) {
        return getMessage(code, currentLocale, args);
    }

    /**
     * Retrieves a localized message with arguments for the specified locale.
     *
     * @param code   Message key
     * @param locale Target locale
     * @param args   Arguments for message placeholders
     * @return Localized message with formatted arguments
     */
    public static String getMessage(String code, Locale locale, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }

    /**
     * Checks if a message exists for the given code.
     *
     * @param code Message key
     * @return true if the message exists, false otherwise
     */
    public static boolean has(String code) {
        try {
            getMessage(code);
            return true;
        } catch (NoSuchMessageException e) {
            return false;
        }
    }

    /**
     * Converts a UserType enum to its localized string representation.
     *
     * @param type UserType enum
     * @return Localized string for the UserType
     */
    public static String convert(UserType type) {
        String lowerCase = type.getName().toLowerCase();
        String messageCode = StringUtil.joinWith(".", COMMON_PREFIX, type.getClass().getSimpleName() + lowerCase);
        return getMessage(messageCode);
    }

    /**
     * Converts a Status enum to its localized string representation.
     *
     * @param status Status enum
     * @return Localized string for the Status
     */
    public static String i18nConvert(Status status) {
        return getMessage(status.getMessageCode());
    }

    /**
     * Converts a Sex enum to its localized string representation.
     *
     * @param sex Sex enum
     * @return Localized string for the Sex
     */
    public static String i18nConvert(Sex sex) {
        String messageCode = StringUtil.joinWith(".", COMMON_PREFIX, sex.toString());
        return getMessage(messageCode);
    }

    /**
     * Retrieves all supported language codes.
     *
     * @return Array of supported language codes
     */
    public static String[] getAllSupportLanguages() {
        Set<Locale> result = new LinkedHashSet<>();
        result.add(currentLocale);
        result.addAll(SUPPORT_LANGUAGES);
        return result.stream().map(Locale::getLanguage).toArray(String[]::new);
    }

    /**
     * Scans the classpath for i18n resource files and returns supported locales.
     *
     * @return List of supported Locale objects
     * @throws Exception If an error occurs during resource scanning
     */
    public static List<Locale> getSupportedLocales() throws Exception {
        List<Locale> locales = new ArrayList<>();

        // Scan resource files using ResourcePatternResolver
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(RESOURCE_PATTERN);

        for (Resource resource : resources) {
            // Extract filename
            String filename = resource.getFilename();
            if (filename != null) {
                // Parse language and country codes using regex
                Matcher matcher = LOCALE_PATTERN.matcher(filename);
                if (matcher.matches()) {
                    String language = matcher.group(1); // Language code, e.g., "en"
                    String country = matcher.group(2); // Country code, e.g., "US" (may be null)
                    // Create Locale object
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

    /**
     * Determines the default locale based on application settings or system default.
     *
     * @return Selected Locale object
     */
    private static Locale getLocale() {
        String appLanguage = System.getProperty(Application.APP_LANGUAGE, JFramePref.state.get(Application.APP_LANGUAGE, Application.APP_LANGUAGE_EN));
        Locale locale;
        try {
            locale = Locale.forLanguageTag(appLanguage);
        } catch (Exception e) {
            locale = Locale.forLanguageTag(Application.APP_LANGUAGE_EN);
            return locale;
        }

        Locale supportLanguage = findSupportLanguage(locale.getLanguage());
        if (supportLanguage != null) {
            return supportLanguage;
        }
        return Locale.forLanguageTag(Application.APP_LANGUAGE_EN);
    }

    public static Locale findSupportLanguage(String language) {
        for (Locale supportLanguage : SUPPORT_LANGUAGES) {
            if (supportLanguage.getLanguage().equals(language)) {
                return supportLanguage;
            }
        }
        return null;
    }

    public static boolean isCurrentLanguage(String lang) {
        return currentLocale.getLanguage().equals(lang);
    }

    public static void updateLanguage(String language) {
        Locale supportLanguage = findSupportLanguage(language);
        if (supportLanguage == null) {
            throw new RuntimeException("Language: " + language + " not support!");
        }
        System.setProperty(Application.APP_LANGUAGE, supportLanguage.getLanguage());
        JFramePref.state.put(Application.APP_LANGUAGE, supportLanguage.getLanguage());
    }
}