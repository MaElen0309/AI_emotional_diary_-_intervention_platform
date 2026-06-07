package com.emotional.diary.config;

import com.emotional.diary.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import javax.annotation.Resource;
import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**",
                        "/public/**",
                        "/error",
                        // 前端静态资源（Docker 部署时）
                        "/",
                        "/index.html",
                        "/assets/**",
                        "/*.js",
                        "/*.css",
                        "/*.ico",
                        "/*.svg",
                        "/*.png");
    }

    /**
     * 配置前端静态资源服务（Docker 部署时使用）
     * 将 classpath:/static/ 下的文件映射到根路径
     * 支持 Vue Router 的 History 模式（SPA fallback）
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 检查前端构建产物是否存在
        Resource indexResource = new ClassPathResource("static/index.html");
        if (indexResource.exists()) {
            registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/static/")
                    .resourceChain(true)
                    .addResolver(new PathResourceResolver() {
                        @Override
                        protected Resource getResource(String resourcePath, Resource location) throws IOException {
                            Resource requested = location.createRelative(resourcePath);
                            if (requested.exists() && requested.isReadable()) {
                                return requested;
                            }
                            // SPA fallback: 所有未匹配的请求返回 index.html
                            return new ClassPathResource("static/index.html");
                        }
                    });
        }
    }
}
