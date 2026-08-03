package com.example.eventstream.common.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize(
        "hasAuthority(T(com.example.eventstream.common.security.Permission).ORDER_DELETE.name())"
)
public @interface CanDeleteOrder {
}