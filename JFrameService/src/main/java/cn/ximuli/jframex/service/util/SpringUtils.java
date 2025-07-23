package cn.ximuli.jframex.service.util;

import cn.ximuli.jframex.common.utils.StringUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public final class SpringUtils implements BeanFactoryPostProcessor, ApplicationContextAware {
    private static ConfigurableListableBeanFactory beanFactory;

    public static ApplicationContext applicationContext;

    private static Environment env;


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtils.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
        SpringUtils.env = applicationContext.getEnvironment();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

    public static <T> T getBean(Class<T> clz) throws BeansException {
        T result = (T) beanFactory.getBean(clz);
        return result;
    }

    public static <T> T getBean(Class<T> clz, Object... args) throws BeansException {
        T result = (T) beanFactory.getBean(clz, args);
        return result;
    }

    public static <T> List<T> getBean(Class<T> clz, Class<? extends Annotation> annotationType) throws BeansException {
        String[] beanNamesForAnnotation = beanFactory.getBeanNamesForAnnotation(annotationType);
        return Arrays.stream(beanNamesForAnnotation).map(name -> beanFactory.getBean(name, clz)).toList();
    }


    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }


    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(name);
    }


    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name);
    }


    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getAliases(name);

    }


    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }


    public static String[] getActiveProfiles() {
        return applicationContext.getEnvironment().getActiveProfiles();
    }


    public static String getActiveProfile() {
        final String[] activeProfiles = getActiveProfiles();
        return StringUtil.isNotEmpty(activeProfiles) ? activeProfiles[0] : null;
    }

    public static Environment getEnv() {
        return env;
    }

    public static Locale getDefaultLocale() {
        return applicationContext.getBean(Locale.class);
    }
}