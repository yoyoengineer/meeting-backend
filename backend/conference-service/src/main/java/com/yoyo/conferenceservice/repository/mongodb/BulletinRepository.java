package com.yoyo.conferenceservice.repository.mongodb;

import com.kanz.conferenceservice.model.mongodb.Bulletin;
import com.yoyo.conferenceservice.model.mongodb.Bulletin;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BulletinRepository extends MongoRepository<Bulletin,String> {
    List<Bulletin> findByEventIdOrderByIdDesc(String eventId);
}
