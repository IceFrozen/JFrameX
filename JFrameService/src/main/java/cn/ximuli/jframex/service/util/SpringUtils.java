package cn.ximuli.jframex.service.util;

import cn.ximuli.jframex.common.utils.StringUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.*;
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

    public static <T> List<T> getBeanForType(Class<T> clz) throws BeansException {
        String[] beanNamesForType = beanFactory.getBeanNamesForType(clz);
        return Arrays.stream(beanNamesForType).map(name -> beanFactory.getBean(name, clz)).toList();
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

    /**
     * Scans all classes in the specified package and its sub-packages that implement or extend the given type.
     *
     * @param basePackage The base package to scan (e.g., "com.example").
     * @param type        The interface or class to match (e.g., MyInterface.class or MyBaseClass.class).
     * @return A set of classes that implement or extend the specified type.
     * @throws ClassNotFoundException If a class cannot be loaded.
     */
    public static <T> Set<Class<? extends T>> scanClasses(String basePackage, Class<T> type) throws ClassNotFoundException {
        // Create a ClassPathScanningCandidateComponentProvider without default filters
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        // Add a filter to include classes that implement or extend the specified type
        scanner.addIncludeFilter(new AssignableTypeFilter(type));

        // Set to store the matching classes
        Set<Class<? extends T>> classes = new HashSet<>();

        // Scan the specified package
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {
            // Load the class by its name
            Class<?> candidateClass = Class.forName(beanDefinition.getBeanClassName());
            // Cast and add to the result set
            classes.add(candidateClass.asSubclass(type));
        }

        return classes;
    }
}