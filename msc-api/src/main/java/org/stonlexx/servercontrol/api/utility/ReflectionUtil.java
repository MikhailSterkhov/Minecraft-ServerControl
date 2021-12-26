package org.stonlexx.servercontrol.api.utility;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Method;
import java.util.Arrays;

@UtilityClass
public class ReflectionUtil {

    public boolean hasMethod(@NonNull Class<?> clazz, @NonNull Method method) {
        return Arrays.asList(clazz.getDeclaredMethods()).contains(method);
    }

    public boolean hasMethod(@NonNull Class<?> clazz, @NonNull String methodName) {
        return Arrays.stream(clazz.getDeclaredMethods()).anyMatch(method -> method.getName().equals(methodName));
    }

    public boolean hasMethod(@NonNull Class<?> clazz, @NonNull String methodName, int parameterCount) {
        return Arrays.stream(clazz.getDeclaredMethods()).anyMatch(method -> method.getName().equals(methodName) && method.getParameterCount() == parameterCount);
    }

    public boolean hasMethod(@NonNull Class<?> clazz, @NonNull String methodName, Class<?>[] parameterTypes) {
        return Arrays.stream(clazz.getDeclaredMethods()).anyMatch(method -> method.getName().equals(methodName) && method.getParameterTypes() == parameterTypes);
    }
}
