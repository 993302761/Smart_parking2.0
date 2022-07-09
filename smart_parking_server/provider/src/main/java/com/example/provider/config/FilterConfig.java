package com.example.provider.config;

import com.example.provider.filter.UUIDFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class FilterConfig implements WebMvcConfigurer {

    @Resource
    private UUIDFilter uuidFilter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册自己的拦截器,并设置拦截的请求路径
        //addPathPatterns为拦截此请求路径的请求
        //excludePathPatterns为不拦截此路径的请求
        registry.addInterceptor(uuidFilter)
                .addPathPatterns("com.example.provider.controller.OrderController.*(..))")
                .excludePathPatterns("/admin/oldLogin");
    }

}