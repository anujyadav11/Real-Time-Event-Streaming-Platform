package com.example.infrastructure.security.internal.annotation;

import com.example.infrastructure.security.internal.identity.ServiceIdentity;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InternalAllowedServices {
    ServiceIdentity[] value ();
}
