package com.proxyapi.cryptomiddleware.config;

import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

public class VersionedRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    private final String apiPathPrefix;

    public VersionedRequestMappingHandlerMapping(String apiPathPrefix) {
        this.apiPathPrefix = apiPathPrefix;
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        if (info == null) return null;

        String[] patterns = info.getPatternsCondition().getPatterns().stream()
                .map(pattern -> apiPathPrefix + pattern)
                .toArray(String[]::new);

        return RequestMappingInfo.paths(patterns)
                .options(getBuilderConfiguration())
                .build()
                .combine(info);
    }
}
