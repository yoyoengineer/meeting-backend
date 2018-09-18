package com.yoyo.conferenceservice.controller;

import com.kanz.conferenceservice.model.mongodb.Comment;
import com.kanz.conferenceservice.service.activities.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class CommentController {

    private CommentService commentService;

    @Autowired
    public CommentController(CommentService commentServices){
        this.commentService = commentServices;
    }

    @PostMapping("/event/comment")
    public ResponseEntity<?> comment(@RequestBody Comment comment, Principal user){
        String username = user.getName();
        if(comment!=null)
            comment.setUsername(username);
        return commentService.comment(comment);
    }

    @GetMapping("/event/comments/{eventId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable("eventId")String eventId, Principal user){
        // The user should be checked to see if he is allowed to see the comments
        return getComments(eventId,"",user);
    }

    @GetMapping("/event/comments/{eventId}/{id}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable("eventId")String eventId,
                                                     @PathVariable("id")String id,
                                                     Principal user){
        String username = user.getName();
        return commentService.commentsGet(eventId,id,username);
    }

    @PostMapping("/event/comment/delete/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id")String id, Principal user){
        String username = user.getName();
        return commentService.deleteComment(id,username);

    }
}
