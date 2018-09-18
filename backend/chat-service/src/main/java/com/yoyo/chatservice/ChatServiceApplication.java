package com.yoyo.chatservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@SpringBootApplication
public class ChatServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatServiceApplication.class, args);

//		for(int i=0; i<10; i++)
//		System.out.println(UUIDs.timeBased());

	}


	Filter doFilter() {
		return new Filter() {
			@Override
			public void init(FilterConfig filterConfig) throws ServletException {

			}

			@Override
			public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

				HttpServletRequest res = (HttpServletRequest) servletRequest;
				System.out.println("Received request: "+servletRequest);
				Cookie[] cookies = res.getCookies();
				if(cookies!=null)
					for (Cookie cookie : res.getCookies()) {
						System.out.println("Name: " + cookie.getName());
						System.out.println("Value: " + cookie.getValue());

					}

				for (Enumeration<String> e = res.getHeaderNames(); e.hasMoreElements();) {
					String he = e.nextElement();
					System.out.println(he+": "+res.getHeader(he));
				}
				String header = res.getHeader("Authorization");
				if(header!=null) {
					System.out.println("Authentication Header: " + header);
//					header = Base64.getDecoder().decode(header.getBytes()).toString();
//					System.out.println("Converted header: "+header);
				}

				filterChain.doFilter(servletRequest,servletResponse);

			}

			@Override
			public void destroy() {

			}
		};
	}

}



