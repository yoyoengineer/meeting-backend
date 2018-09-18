package com.yoyo.conferenceservice.service;

import com.kanz.conferenceservice.model.Bullet;
import com.kanz.conferenceservice.model.Location;
import com.kanz.conferenceservice.model.mongodb.Conference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ConferenceService {

    ResponseEntity<?> createEvent(Conference newConference, String username);
    ResponseEntity<List<Conference>> myEvents(String username, Integer page);
    ResponseEntity<List<Conference>> nearByConferences(Location location);
    ResponseEntity<?> edit(String username, String eventId, String fieldName, Object fieldValue );
    ResponseEntity<InputStreamResource> logo();
    ResponseEntity<InputStreamResource> QRcode(String eventId, String username);
    ResponseEntity<Conference> conference(String eventId, String username);
    ResponseEntity<?> bullet(Bullet bullet, String eventId, String username);
    ResponseEntity<?> ownerOfConference(String username, String eventId);
}
