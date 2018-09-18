package com.yoyo.webui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@SpringBootApplication
public class WebUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebUiApplication.class, args);
	}

	@Bean
	Filter doFilter() {
		return new Filter() {
			@Override
			public void init(FilterConfig filterConfig) throws ServletException {

			}

			@Override
			public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

				HttpServletRequest res = (HttpServletRequest) servletRequest;
				Cookie[] cookies = res.getCookies();
				if(cookies!=null)
				for (Cookie cookie : res.getCookies()) {
					System.out.println("Name: " + cookie.getName());
					System.out.println("Value: " + cookie.getValue());

				}
                String header = res.getHeader("Authorization");
				if(header!=null)
				System.out.println("Authentication Header: " + header);

				filterChain.doFilter(servletRequest,servletResponse);

			}

			@Override
			public void destroy() {

			}
		};
	}

//	@Bean
//	public InternalResourceViewResolver internalResourceViewResolver() {
//		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//		viewResolver.setViewClass();
//		viewResolver.setOrder(1);
//		viewResolver.setPrefix("/templates/");
//		viewResolver.setSuffix(".html");
//		return viewResolver;
//	}
//
//	@Bean(name = "urlViewController")
//	public UrlFilenameViewController getUrlViewController() {
//		UrlFilenameViewController urlViewController = new UrlFilenameViewController();
//		urlViewController.setPrefix("/templates/");
//		urlViewController.setSuffix(".html");
//		return urlViewController;
//	}
//
//	@Bean
//	public SimpleUrlHandlerMapping getUrlHandlerMapping() {
//		SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
////		Properties mappings = new Properties();
////		mappings.put("/*.html", "urlViewController");
//
//
////		handlerMapping.setMappings(mappings);
//		return handlerMapping;
//	}


}

