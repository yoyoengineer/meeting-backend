package com.yoyo.conferenceservice.repository.mongodb;

import com.kanz.conferenceservice.model.mongodb.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification,String>{
    List<Notification> findByUsername(String username);
    List<Notification> findByEventId(String eventId);
}
