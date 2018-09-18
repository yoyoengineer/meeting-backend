package com.yoyo.webui.security;

import com.yoyo.webui.utils.Utils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
            .and()

            .csrf().disable()

            .authorizeRequests()
                .antMatchers("/essay.html","loginFirst","/login","/register","/css/**","/img/**","/fonts/**","/js/**","/mailbox").permitAll()
                .anyRequest().authenticated()
            .and()
            .exceptionHandling()
                .authenticationEntryPoint((req,res,auth)-> {
//                    res.setStatus(HttpServletResponse.SC_OK);
//                    PrintWriter writer = res.getWriter();
//                    writer.print("<!DOCTYPE html>\n" +
//                            "<html lang=\"en\">\n" +
//                            "<head>\n" +
//                            "    <meta charset=\"UTF-8\">\n" +
//                            "    <title>Title</title>\n" +
//                            "</head>\n" +
//                            "<body>\n" +
//                            "    <a href=\"http://localhost:8080/login\">Login First</a>\n" +
//                            "</body>\n" +
//                            "</html>");
//                    writer.flush();
//                    writer.close();
                    res.sendRedirect(Utils.API_GATEWAY+"/login");
//                    res.sendRedirect("http:/localhost:8080/login");
//                    req.getRequestDispatcher("http://localhost:8080/login").forward(req,res);

                });
    }
}
