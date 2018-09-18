package com.binary.websocketservice.chat.controller;

import com.binary.websocketservice.chat.model.Parcel;
import com.binary.websocketservice.chat.model.ParcelForFileMessage;
import com.binary.websocketservice.chat.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/websocket")
public class ChatController {

    private ChatService chatService;

    public ChatController(ChatService chatService){
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public void receiveParcel(@RequestBody Parcel parcel){
        log.debug("received parcel from chat-service {}",parcel);
        chatService.sendMessage(parcel);
    }

    @PostMapping("/chat/file")
    public void receiveParcelForFiles(@RequestBody ParcelForFileMessage parcelForFileMessage){
        log.debug("received parcel for file from chat-service {}", parcelForFileMessage);
        chatService.sendMessage(parcelForFileMessage);
    }
}
