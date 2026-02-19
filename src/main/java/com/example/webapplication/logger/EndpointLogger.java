package com.example.webapplication.logger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Comparator;

@Slf4j
@Component
public class EndpointLogger {

    private final RequestMappingHandlerMapping handlerMapping;

    public EndpointLogger(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logEndpoints() {
        // Log all Endpoints
        log.debug("Registered Endpoints:");

        handlerMapping.getHandlerMethods().entrySet().stream()
                .sorted(Comparator.comparing(e ->
                        e.getKey().getPatternValues().toString()
                ))
                .forEach(e -> {
                    var mapping = e.getKey();
                    var method = e.getValue();

                    log.debug("\t{} {} -> {}.{}",
                            mapping.getMethodsCondition(),
                            mapping.getPatternValues(),
                            method.getBeanType().getSimpleName(),
                            method.getMethod().getName()
                    );
                });
    }
}