package com.yoyo.conferenceservice.repository.mongodb;

import com.kanz.conferenceservice.model.mongodb.Conference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConferenceRepository extends MongoRepository<Conference, String> {
    Page<Conference> findByUsernameOrderByTimeDesc(String organizerUsername, Pageable pageable);
    List<Conference> findByLocNear(Point point, Distance dist);
    List<Conference> findByUsernameOrderByTimeDesc(String username);
}
