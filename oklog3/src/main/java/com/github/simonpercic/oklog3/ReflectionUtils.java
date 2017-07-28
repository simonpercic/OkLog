package com.github.simonpercic.oklog3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Java Reflection utils.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
final class ReflectionUtils {

    private ReflectionUtils() {
        // no instance
    }

    /**
     * Is class available.
     *
     * @param className class name
     * @return true if class exists, false otherwise
     */
    static boolean hasClass(@NotNull String className) {
        return getClass(className) != null;
    }

    /**
     * Get method on class from class name, method name and param type.
     *
     * @param className class name
     * @param methodName method name
     * @param paramType param type
     * @return method on class
     */
    @Nullable static Method getMethod(@NotNull String className, @NotNull String methodName, Class<?> paramType) {
        Class<?> clazz = getClass(className);

        if (clazz == null) {
            return null;
        }

        try {
            return clazz.getMethod(methodName, paramType);
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    /**
     * Get class from class name.
     *
     * @param className class name
     * @return class from class name
     */
    @Nullable private static Class<?> getClass(@NotNull String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
