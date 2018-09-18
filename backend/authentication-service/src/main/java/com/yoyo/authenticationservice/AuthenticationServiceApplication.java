package com.yoyo.authenticationservice;

import com.yoyo.authenticationservice.event.messagedriven.MyChannels;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@SpringBootApplication
@Slf4j
@EnableBinding(MyChannels.class)
public class AuthenticationServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(AuthenticationServiceApplication.class, args);
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
				System.out.println("Received request: "+servletRequest);
				Cookie[] cookies = res.getCookies();
				if(cookies!=null)
					for (Cookie cookie : res.getCookies()) {
						System.out.println("Name: " + cookie.getName());
						System.out.println("Value: " + cookie.getValue());

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

