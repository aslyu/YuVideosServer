package com.yu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.yu.config.ResourceConfig;
import com.yu.controller.interceptor.Interceptor;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
	
	@Autowired
	private ResourceConfig resourceConfig;
	
	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
				.addResourceLocations("classpath:/META-INF/resources/")
				.addResourceLocations(resourceConfig.getDirectory());
	}
	
	@Bean(initMethod="init")
	public ZKCuratorClient zKCuratorClient() {
		return new ZKCuratorClient();
	}
	
	@Bean
	public Interceptor interceptor() {
		return new Interceptor();
	}
	
	
	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(interceptor()).addPathPatterns("/user/**")
												  .addPathPatterns("/bgm/**")
												  .addPathPatterns("/video/upload","/video/saveComment")
												  .addPathPatterns("/video/userLike","/video/userUnLike")
												  .excludePathPatterns("/bgm/*.mp3","/bgm/*.flac")
												  .excludePathPatterns("/user/queryPublisher");
												
		
		super.addInterceptors(registry);
	}
	
}
