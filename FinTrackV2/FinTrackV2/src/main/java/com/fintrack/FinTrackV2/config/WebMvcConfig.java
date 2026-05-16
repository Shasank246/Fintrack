package com.fintrack.FinTrackV2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final TwoFactorInterceptor twoFactorInterceptor;

    public WebMvcConfig(TwoFactorInterceptor twoFactorInterceptor) {
        this.twoFactorInterceptor = twoFactorInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(twoFactorInterceptor)
                .addPathPatterns("/dashboard", "/expenses/**", "/incomes/**", "/profile/**");
    }
}
