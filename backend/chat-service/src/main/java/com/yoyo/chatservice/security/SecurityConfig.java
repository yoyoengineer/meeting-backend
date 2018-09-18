package com.yoyo.chatservice.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import javax.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request,response,error)->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,error.getMessage()))
                .and()
                .authorizeRequests().antMatchers("/**").permitAll()
                    .anyRequest().authenticated();



    }
}
