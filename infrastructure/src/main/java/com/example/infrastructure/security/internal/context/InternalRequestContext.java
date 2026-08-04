package com.example.infrastructure.security.internal.context;

import com.example.infrastructure.security.internal.identity.ServiceIdentity;

public class InternalRequestContext {
    private static final ThreadLocal<ServiceIdentity> SERVICE =
            new ThreadLocal<>();
    public static void setService(ServiceIdentity service) {
        SERVICE.set(service);
    }
    public static ServiceIdentity getService() {
        return SERVICE.get();
    }
    public static void clear() {
        SERVICE.remove();
    }
}