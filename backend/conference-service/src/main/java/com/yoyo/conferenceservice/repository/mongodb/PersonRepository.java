package com.yoyo.conferenceservice.repository.mongodb;

import com.kanz.conferenceservice.model.mongodb.Person;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonRepository extends MongoRepository<Person,String> {
    Person findByEventIdAndUsername(String eventId,String username);
}
