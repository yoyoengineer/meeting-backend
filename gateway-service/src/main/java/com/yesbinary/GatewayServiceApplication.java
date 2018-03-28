package com.yesbinary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}

	@Bean
	RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
		return builder.routes()

//				.route(r -> r
//						.path("/signup")
//						.or().path("/signin")
//						.or().path("/logout")
//						.uri("http://localhost:8901"))


				.route(r -> r
						.path("/signup")
						.or().path("/signin")
						.or().path("/logout")
						.uri("http://localhost:8080"))
//				.route(r->r
//						.path("/")
//						.or().path("/login")
//						.or().path("/register")
//						.or().path("/css/**")
//						.or().path("/img/**")
//						.or().path("/fonts/**")
//						.or().path("/js/**")
//						.or().path("/**/favicon.ico")
//						.or().path("/favicon.ico")
//						.or().path("/mailbox")
//						.uri("http://localhost:9999"))

				.route(r->r
//                        .path("/ws/info/**")
//                        .or()
								.order(8000)
								.path("/ws/**")
//                        .filters(r->r.removeNonProxyHeaders(""))
								.uri("ws://localhost:8999")
				)
				.route(r->r
						.path("/chat")
						.or().path("/spring")
						.or().path("/jquery.min.js")
						.or().path("/sockjs.min.js")
						.or().path("/webjars/**")
						.or().path("/app.js")
						.or().path("/stomp.min.js")
						.or().path("/ws/info/**")
						.or().path("/user/**")
						.or().path("/queue/**")
						.or().path("/topic/**")
//                        .or().path("/message")
						.uri("http://localhost:8999"))
				.route(r->r
						.path("/file/**")
						.uri("http://localhost:8099"))
				.route(r->r
						.path("/consumer/uploadPre")
						.uri("http://localhost:8093"))
				.route(r->r
						.path("/")
						.or().path("/assets/bootstrap/css/bootstrap.min.css")
						.or().path("/assets/font-awesome-4.5.0/css/font-awesome.min.css")
						.or().path("/assets/css/styles.css")
						.or().path("/assets/js/jquery-2.1.4.min.js")
						.or().path("/assets/bootstrap/js/bootstrap.min.js")
						.or().path("/login")
						.or().path("/home")
						.or().path("/admin/home")
						.or().path("/logout")
						.or().path("/logout")
						.uri("http://localhost:8080"))
				.route(r->r
						.path("/me")
						.uri("http://localhost:8082"))
//				.route(r->r
//						.path("/home")
//						.uri("http://localhost:8080/home"))
//				.route(r->r
//						.path("/post")
//						.uri("http://httpbin.org:80"))
				.build();
	}
}
