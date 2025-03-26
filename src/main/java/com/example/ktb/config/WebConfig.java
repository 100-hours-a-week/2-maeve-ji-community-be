package com.example.ktb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
//
//  @Override
//  public void addCorsMappings(CorsRegistry registry) {
//    registry.addMapping("/**")
//        .allowedOriginPatterns("http://localhost:5500", "http://127.0.0.1:5500")
//        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//        .allowedHeaders("*")
//        .allowCredentials(true);
//  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/images/**")
        .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");

  }
}
