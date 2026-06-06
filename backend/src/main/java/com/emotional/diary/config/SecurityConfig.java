package com.emotional.diary.config;

import com.alibaba.fastjson.JSON;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration() {
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AuthFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    /**
     * 认证Filter：检查Session，公开路径放行
     */
    public static class AuthFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            String path = req.getRequestURI();

            // TODO: 调试阶段暂时放行所有路径，确认登录功能正常后再启用权限控制
            // if (isPublicPath(req, path)) {
            // chain.doFilter(request, response);
            // return;
            // }
            chain.doFilter(request, response);
            return;

            /*
             * // 检查Session登录状态
             * HttpSession session = req.getSession(false);
             * if (session != null && session.getAttribute("currentUser") != null) {
             * chain.doFilter(request, response);
             * return;
             * }
             * 
             * // 未登录返回401
             * res.setStatus(401);
             * res.setContentType("application/json;charset=UTF-8");
             * Map<String, Object> result = new HashMap<>();
             * result.put("code", 401);
             * result.put("message", "未登录或登录已过期");
             * res.getWriter().write(JSON.toJSONString(result));
             */
        }

        private boolean isPublicPath(HttpServletRequest req, String path) {
            // 登录/注册/公开接口/error放行
            if (path.contains("/auth/") || path.contains("/public/") || path.equals("/error")) {
                return true;
            }
            // OPTIONS预检请求放行
            if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
                return true;
            }
            return false;
        }
    }

    /**
     * CORS跨域Filter
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CorsFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registration;
    }

    public static class CorsFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;

            res.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
            res.setHeader("Access-Control-Allow-Credentials", "true");
            res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.setHeader("Access-Control-Allow-Headers", "*");
            res.setHeader("Access-Control-Max-Age", "3600");

            if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
                res.setStatus(200);
                return;
            }
            chain.doFilter(request, response);
        }
    }
}
