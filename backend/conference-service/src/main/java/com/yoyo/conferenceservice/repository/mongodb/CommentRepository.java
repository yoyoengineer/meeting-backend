package com.yoyo.conferenceservice.repository.mongodb;

import com.kanz.conferenceservice.model.mongodb.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.List;

public interface CommentRepository extends MongoRepository<Comment,String> {
    List<Comment> findByEventIdOrderByIdDesc(String eventId);
}
