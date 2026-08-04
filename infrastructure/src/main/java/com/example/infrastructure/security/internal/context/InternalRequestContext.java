package com.example.infrastructure.security.internal.context;

public class InternalRequestContext {
    private static final ThreadLocal<String> SERVICE =
            new ThreadLocal<>();
    public static void setService(String service) {
        SERVICE.set(service);
    }
    public static String getService() {
        return SERVICE.get();
    }
    public static void clear() {
        SERVICE.remove();
    }
}