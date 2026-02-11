package com.example.webapplication.config.security.dev_security_allows_all;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

@Configuration
@Profile("debug-security-allows-all")
public class SecurityConfigAllowsAllConfig {

    MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler =
                new DefaultMethodSecurityExpressionHandler();

        handler.setPermissionEvaluator(new PermissionEvaluator() {

            @Override
            public boolean hasPermission(
                    Authentication authentication,
                    Object targetDomainObject,
                    Object permission) {
                return true;
            }

            @Override
            public boolean hasPermission(
                    Authentication authentication,
                    Serializable targetId,
                    String targetType,
                    Object permission) {
                return true;
            }
        });

        return handler;
    }
}
