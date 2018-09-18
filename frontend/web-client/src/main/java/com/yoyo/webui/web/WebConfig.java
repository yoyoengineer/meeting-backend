package com.yoyo.webui.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
//@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter{
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("register").setViewName("register.html");
//        registry.addViewController("login").setViewName("login.html");
//        registry.addViewController("loginFirst").setViewName("loginFirst.html");
//        registry.addViewController("mailbox").setViewName("mailbox.html");
//
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/webjars/**",
                "/img/**",
                "/css/**",
                "/js/**",
                "/fonts/**")
                .addResourceLocations(
                        "classpath:/META-INF/resources/webjars/",
                        "classpath:/templates/img/",
                        "classpath:/templates/css/",
                        "classpath:/templates/js/",
                        "classpath:/templates/fonts/");
    }
}
