package com.yoyo.chatservice.controller;


import com.datastax.driver.core.utils.UUIDs;
import com.yoyo.chatservice.model.mongo.History;
import com.yoyo.chatservice.model.cassandra.InstantMessage;
import com.yoyo.chatservice.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.*;

@RestController
@Slf4j
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/private/message")
    public ResponseEntity<?> privateMessage(@RequestBody History history, Principal user){
        log.debug("received history {}",history);
       return chatService.sendPlainTextMessage(history.withFromUser(user.getName()));
    }

    @GetMapping("/private/messages/{id}/{time}")
    public ResponseEntity<List<InstantMessage>> getMyMessages(@PathVariable("id") String username, @PathVariable("time") UUID time, Principal user){

        System.out.println("Long's value equal: " + time);
        System.out.println("Username is " + username);
        if(username.equals(user.getName())) {
            return ResponseEntity.ok(chatService.findMessageForBeforeTime(username, time));
        }

        System.out.println("Forbidden resource");
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/private/messages/{id:.+}")
    public ResponseEntity<List<InstantMessage>> getMyMessages(@PathVariable("id") String historyId, Principal user){
            System.out.println("Loading messages for time "+ new Date().getTime()+" and user "+user.getName());
            return ResponseEntity.ok(chatService.findMessageForBeforeTime(historyId, UUIDs.timeBased()));
    }

    @PostMapping("/private/file/{toUser:.+}")
    public ResponseEntity<?> privateFile(@RequestParam("file")MultipartFile file, @PathVariable("toUser") String toUser, Principal user){
        Map<String, String> usersInvolved = new HashMap<>();
        usersInvolved.put("toUser",toUser);
        usersInvolved.put("fromUser",user.getName()); // Do not forget to uncomment this

        /** To be removed **/
        String fileName = file.getOriginalFilename();
        System.out.println(fileName);
        System.out.println(file.getSize());
        System.out.println(file.getContentType());
        /** **/

        return chatService.uploadFile(file,usersInvolved);
    }

    @GetMapping("/private/file/{fileId}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("fileId") String fileId, Principal user){

        return chatService.getFile(fileId, "username");
    }

    @PostMapping("/private/delete/{time}")
    public ResponseEntity<?> deleteMessage(@PathVariable("time") long time, Principal user){
        return  chatService.deleteMessage(user.getName(),time);
    }


}
