package com.stayeasy.stayeasyspringangular.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class SpaWebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")                           // serve all non-API paths as static
      .addResourceLocations("classpath:/static/")             // where Angular build is (has /browser/)
      .resourceChain(true)
      .addResolver(new PathResourceResolver() {
        @Override
        protected @Nullable Resource getResource(@NonNull String resourcePath, @NonNull Resource location) throws IOException {
          // let /api/** be handled by controllers
          if (resourcePath.startsWith("api/")) return null;

          // serve real static files if they exist (js, css, assets, etc.)
          Resource requested = location.createRelative(resourcePath);
          if (requested.exists() && requested.isReadable()) {
            return requested;
          }

          return location.createRelative("/browser/index.html");
        }
      });
  }
}

