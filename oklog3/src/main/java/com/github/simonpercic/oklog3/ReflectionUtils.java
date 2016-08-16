package com.github.simonpercic.oklog3;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    @Nullable static Method getMethod(@NonNull String className, @NonNull String methodName, Class<?> paramType) {
        Class<?> clazz;

        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }

        try {
            return clazz.getMethod(methodName, paramType);
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }
}
