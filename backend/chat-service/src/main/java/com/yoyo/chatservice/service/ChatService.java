package com.yoyo.chatservice.service;

import com.yoyo.chatservice.model.mongo.History;
import com.yoyo.chatservice.model.cassandra.InstantMessage;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ChatService {
    String USERNAME = "username";
    String FROM_USER = "fromUser";
    String TO_USER = "toUser";
    String FILE_NAME = "fileName";
    String CONTENT_TYPE = "content-type";
    String SIZE = "size";
    String FILE_ID = "fileId";
    String COUNT ="count";
    String ID ="_id";
    String METADATA ="metadata";
    String chatURL = "http://localhost:8080/websocket/chat";
    String chatFile = "http://localhost:8080/websocket/chat/file";
    String notificationURL = "http://localhost:8080/websocket/notifications";

    ResponseEntity<?> sendPlainTextMessage(History history);

    List<InstantMessage> findMessageForBeforeTime(String username, UUID UUIDtime);

    ResponseEntity<?> uploadFile(MultipartFile file, Map<String,String> usersInvolved);

    ResponseEntity<InputStreamResource> getFile(String fileId,String username);

    ResponseEntity<?> deleteMessage(String username, long time);

    ResponseEntity<List<History>> myHistory(String username);
}
