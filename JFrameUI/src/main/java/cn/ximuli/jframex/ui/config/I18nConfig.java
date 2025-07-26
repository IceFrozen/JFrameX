package cn.ximuli.jframex.ui.config;

import cn.ximuli.jframex.ui.Application;
import cn.ximuli.jframex.ui.I18nHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;

import java.util.Locale;


@Configuration
@Slf4j
public class I18nConfig {

    @Bean
    public MessageSource messageSource(Locale defaultLocal) {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        I18nHelper.setMessageSource(messageSource, defaultLocal);

        messageSource.setDefaultLocale(defaultLocal);
        log.info("Service language: {}", defaultLocal);
        return messageSource;
    }

    @Bean
    public Locale defaultLocal(Environment environment) {
        return Locale.forLanguageTag(environment.getProperty(Application.APP_LANGUAGE, Locale.getDefault().getLanguage()));
    }
}