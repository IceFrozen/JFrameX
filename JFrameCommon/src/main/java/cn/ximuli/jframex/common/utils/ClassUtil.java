package cn.ximuli.jframex.common.utils;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * ClassUtil Utility Class
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class ClassUtil extends ClassUtils {

    /**
     * Creates a new instance of the specified class using reflection.
     *
     * @param clazz The class to instantiate.
     * @param <T>   The type of the class.
     * @return An instance of the specified class.
     * @throws IllegalArgumentException If the class is null or cannot be instantiated.
     */
    public static <T> T newInstance(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        try {
            // Try to get the no-arg constructor and create an instance
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true); // Allow access to private constructors
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No no-arg constructor found for class: " + clazz.getName(), e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to instantiate class: " + clazz.getName(), e);
        }
    }

    /**
     * Creates a new instance of the specified class using reflection with constructor parameters.
     *
     * @param clazz       The class to instantiate.
     * @param paramTypes  The parameter types of the constructor.
     * @param paramValues The parameter values to pass to the constructor.
     * @param <T>         The type of the class.
     * @return An instance of the specified class.
     * @throws IllegalArgumentException If the class or parameters are invalid or instantiation fails.
     */
    public static <T> T newInstance(Class<T> clazz, Class<?>[] paramTypes, Object[] paramValues) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        if (paramTypes == null || paramValues == null || paramTypes.length != paramValues.length) {
            throw new IllegalArgumentException("Parameter types and values must be non-null and of equal length");
        }

        try {
            // Get the constructor with the specified parameter types
            Constructor<T> constructor = clazz.getDeclaredConstructor(paramTypes);
            constructor.setAccessible(true); // Allow access to private constructors
            return constructor.newInstance(paramValues);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No constructor found for class: " + clazz.getName() +
                    " with specified parameter types", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to instantiate class: " + clazz.getName(), e);
        }
    }
}
