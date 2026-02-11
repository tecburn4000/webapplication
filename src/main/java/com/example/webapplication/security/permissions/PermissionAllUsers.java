package com.example.webapplication.security.permissions;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
//@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_NONE') or securityPropertiesPermitsAll")
@PreAuthorize("isAuthenticated()  or securityPropertiesPermitsAll")
public @interface PermissionAllUsers {
}
