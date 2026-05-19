package com.newton.zaocycle.certification.infrastructure.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(name = "zaocycle.storage.provider", havingValue = "LOCAL_FS")
public class StorageConfig implements WebMvcConfigurer {

    private final StorageProperties props;

    public StorageConfig(StorageProperties props) {
        this.props = props;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourceLocation = "file:" + props.localFs().basePath() + "/";
        registry.addResourceHandler("/files/**").addResourceLocations(resourceLocation);
    }
}
