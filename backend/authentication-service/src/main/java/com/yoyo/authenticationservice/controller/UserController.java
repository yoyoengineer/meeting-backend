package com.yoyo.authenticationservice.controller;

import com.yoyo.authenticationservice.model.User;
import com.yoyo.authenticationservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }


    @RequestMapping("/signin")
    public ResponseEntity login(Principal user){
        Map<String,String> map = new HashMap<>();
        map.put("username",user.getName());
        map.put("password","********");
        return ResponseEntity.ok(map);

    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody User user){
        log.debug("In signup controller");
      return userService.register(user);
    }

}
