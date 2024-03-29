//package com.marella.config;
//
//import java.io.IOException;
//import java.util.Collection;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.stereotype.Component;
//
//@Component
//public class SameSiteFilter implements Filter {
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//    }
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        chain.doFilter(request, response);
//        addSameSiteAttribute((HttpServletResponse) response); // add SameSite=strict cookie attribute
//    }
//    private void addSameSiteAttribute(HttpServletResponse response) {
//        Collection<String> headers = response.getHeaders("Set-Cookie");
//        boolean firstHeader = true;
//        for (String header : headers) {
//            if (firstHeader) {
//                response.setHeader("Set-Cookie", String.format("%s; %s", header, "SameSite=None"));
//                firstHeader = false;
//                continue;
//            }
//            response.addHeader("Set-Cookie", String.format("%s; %s", header, "SameSite=None"));
//        }
//    }
//    @Override
//    public void destroy() {
//    }
//}