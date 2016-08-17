package com.github.simonpercic.oklog3;

import android.support.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import okhttp3.Response;
import okhttp3.internal.http.HttpHeaders;

/**
 * Compat Response hasBody method manager.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
final class HasResponseBodyManager {

    private final boolean useNewMethod;
    private final Method legacyMethod;

    private HasResponseBodyManager(boolean useNewMethod, Method legacyMethod) {
        this.useNewMethod = useNewMethod;
        this.legacyMethod = legacyMethod;
    }

    /**
     * Creates instance of class based on what classes from OkHttp are available.
     *
     * @return HasResponseBodyManager instance
     */
    @NonNull static HasResponseBodyManager create() {
        if (ReflectionUtils.hasClass("okhttp3.internal.http.HttpHeaders")) {
            return new HasResponseBodyManager(true, null);
        } else {
            Method method = ReflectionUtils.getMethod("okhttp3.internal.http.HttpEngine", "hasBody", Response.class);

            if (method == null) {
                throw new IllegalStateException("Response hasBody method is not available");
            }

            return new HasResponseBodyManager(false, method);
        }
    }

    /**
     * Response hasBody.
     *
     * @param response response
     * @return true if response has body, false otherwise
     */
    boolean hasBody(Response response) {
        if (useNewMethod) {
            return HttpHeaders.hasBody(response);
        } else {
            try {
                return (boolean) legacyMethod.invoke(null, response);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException("Failed to invoke hasBody method: " + e.getMessage(), e);
            }
        }
    }
}
