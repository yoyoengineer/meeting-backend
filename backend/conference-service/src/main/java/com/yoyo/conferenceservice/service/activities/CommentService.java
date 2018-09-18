package com.yoyo.conferenceservice.service.activities;

import com.kanz.conferenceservice.model.mongodb.Comment;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommentService {

    ResponseEntity<?> comment(Comment comment);
    ResponseEntity<List<Comment>> commentsGet(String eventId, String id, String username);
    ResponseEntity<?> deleteComment(String id, String username);
}
