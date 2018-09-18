package com.yoyo.webui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @GetMapping("/login")
    public String login(HttpSession session){
//        if(!session.isNew())
//            return "redirect:http://localhost:8080 ";

        return "login";
    }

    @GetMapping("/register")
    public String register(HttpSession session){
        return "register";
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

}
