package org.bem.iot.config;

import jakarta.annotation.Resource;
import org.bem.iot.config.head.HeadInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private HeadInterceptor headInterceptor;

	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(headInterceptor).addPathPatterns("/**");
    }

	@Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
		String rootDir = "";
		try {
			rootDir = new File("/storage").getCanonicalPath() + "/";
			rootDir = rootDir.replace("\\", "/");
		} catch (IOException ignored) {
		}
        registry.addResourceHandler("/storage/**").addResourceLocations("file:" + rootDir);
    }
	
	/**
     * 跨域访问配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //.allowedOrigins("*")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }
}
