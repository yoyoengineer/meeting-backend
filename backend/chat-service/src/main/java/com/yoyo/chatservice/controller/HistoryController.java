package com.yoyo.chatservice.controller;

import com.yoyo.chatservice.model.mongo.History;
import com.yoyo.chatservice.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class HistoryController {

    private ChatService chatService;

    public HistoryController(ChatService chatService){
        this.chatService = chatService;
    }


    @GetMapping("/private/history")
    public ResponseEntity<List<History>> myHistroy(Principal user){
        return chatService.myHistory(user.getName());
    }
}
