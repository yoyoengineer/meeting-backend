package com.yoyo.authenticationservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletResponse;


@Configuration
public class SecurityConf extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
            .and()

            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
            .and()

            .httpBasic()
            .and()

            .csrf().disable()

            .exceptionHandling()
                .authenticationEntryPoint(
                        (request,response,error)->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,error.getMessage()))
            .and()
            .authorizeRequests()
                .antMatchers("/signup").permitAll()
                .anyRequest().authenticated()
            .and()
            .logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()).permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);

    }

}
