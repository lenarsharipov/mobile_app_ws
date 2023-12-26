package com.apps.mobileappws;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**").allowedMethods("*").allowedOrigins("*") // All controllers methods. all origins allowed
        registry
                .addMapping("/users")
                .allowedMethods("POST")
                .allowedOrigins("https://www.google.com");
    }
}
