package com.std.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 资源映射路径映射congfig
 * @author Schaw
 *
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Value("${resourceLocations.uploadPath}")
	private String uploadPath;
	@Value("${resourceHandler.pathHandler}")
	private String pathHandler;
	
    @Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*
         * 文件资源路径映射
         */
        registry.addResourceHandler(pathHandler + "**")
        		.addResourceLocations("file:///"+uploadPath);
    }


}

