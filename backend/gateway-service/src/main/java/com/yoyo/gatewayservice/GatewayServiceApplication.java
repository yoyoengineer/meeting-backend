package com.yoyo.gatewayservice;

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

                .route(r -> r
                        .path("/signup")
                        .or().path("/signin")
                        .or().path("/logout")
                        .uri("http://localhost:8901"))

                .route(r->r
//                        .path("/")
//                        .or().path("/signin")
                        .path("/myevents")
                        .or().path("/events/**")
                        .or().path("/announcements/**")
                        .or().path("/comments/**")
                        .or().path("/friends/**")
                        .or().path("/myprofile")
                        .uri("http://localhost:5555"))

                .route(r->r
                        .path("/")
                        .or().path("/create_event.html")
                        .or().path("/essay.html")
                        .or().path("/event")
                        .or().path("/attended")
                        .or().path("/edit-profile")
                        .or().path("/create_event")
//                        .or().path("/admin-event.html")
                        .or().path("/login")
                        .or().path("/web/**")
                        .or().path("/register")
                        .or().path("/css/**")
                        .or().path("/img/**")
                        .or().path("/fonts/**")
                        .or().path("/js/**")
                        .or().path("/**/favicon.ico")
                        .or().path("/favicon.ico")
                        .or().path("/mailbox")
                        .uri("http://localhost:9999"))

                .route(r->r
                        .order(8000)
                        .path("/stompwebsocket/**")
//                        .filters(r->r.removeNonProxyHeaders(""))
                        .uri("ws://localhost:8855")
                        )
                .route(r->r
                        .path("/websocket/**")
                        .or()
                        .path("/stompwebsocket/info/**")
                        .uri("http://localhost:8855")
                            )
                .route(r->r
                        .path("/private/**")
                        .uri("http://localhost:8999"))

                .route(r->r
                        .path("/post")
                        .uri("http://httpbin.org:80"))

                .route(r->r
                        .path("/profile/**")
                        .uri("http://localhost:8866"))


                .route(r->r
                        .path("/event/**")
                        .uri("http://localhost:8899"))
                .build();
    }
}