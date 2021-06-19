package com.hh.config;

import com.hh.util.PathUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${file.img.path}")
    private String IMG_PATH;

    /**
     * 图片ResourceHandler
     * @param registry ResourceHandlerRegistry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String imgPath="file:"+PathUtil.getRootPath()+IMG_PATH;
        registry.addResourceHandler("img//**").addResourceLocations(imgPath);
    }
}
