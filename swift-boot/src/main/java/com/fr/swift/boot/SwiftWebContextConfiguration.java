package com.fr.swift.boot;

import com.fr.startup.web.annotation.MappingJackson2HttpMessageConverter;
import com.fr.swift.http.servlet.interceptor.EndecryptionInterceptor;
import com.fr.third.springframework.context.annotation.ComponentScan;
import com.fr.third.springframework.context.annotation.Configuration;
import com.fr.third.springframework.context.annotation.EnableAspectJAutoProxy;
import com.fr.third.springframework.http.converter.HttpMessageConverter;
import com.fr.third.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.fr.third.springframework.web.servlet.config.annotation.InterceptorRegistry;
import com.fr.third.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author anchore
 * @date 2018/10/17
 */
@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@ComponentScan({
        "com.fr.swift.api",
        "com.fr.swift.http"
})
public class SwiftWebContextConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new EndecryptionInterceptor()).addPathPatterns("/**");
    }
}