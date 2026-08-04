package com.example.infrastructure.security.internal.aspect;

import com.example.infrastructure.security.internal.annotation.InternalAllowedServices;
import com.example.infrastructure.security.internal.context.InternalRequestContext;
import com.example.infrastructure.security.internal.identity.ServiceIdentity;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class InternalAuthorizationAspect {

    @Before("@annotation(annotation)")
    public void authorize(
            InternalAllowedServices annotation
    ) {
        ServiceIdentity caller =
                InternalRequestContext.getService();
        boolean allowed =
                Arrays.stream(annotation.value())
                        .anyMatch(service -> service == caller);
        if (!allowed) {
            throw new AccessDeniedException(
                    "Internal service is not authorized."
            );

        }
    }
}