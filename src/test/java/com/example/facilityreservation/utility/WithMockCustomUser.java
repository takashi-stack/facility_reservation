package com.example.facilityreservation.utility;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;


@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "root";

    String password() default "admin00";

    String[] roles() default { "ADMIN" };

    int permissionLevel() default 1;

    String note() default "何でもできる";
}
