package com.yoyo.conferenceservice.service.characters;

import com.kanz.conferenceservice.model.mongodb.Person;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;

public interface AttendanceService {
    ResponseEntity<?> participate(String eventId,String username);
    ResponseEntity<?> countPersons(String eventId, Person.STATE state);
    ResponseEntity<?> deleteParticipant(String eventId,String username, String owner);
    ResponseEntity<?> deleteParticipant(String eventId,String username);
    ResponseEntity<?> attend(String eventId,String username);
    ResponseEntity<?> attend(String eventId,String username, String owner);
    @Async
    void notifyParticipants(String eventId, String fieldName,Object fieldValue);
    ResponseEntity<InputStreamResource> userQRCode(String eventId, String username);
}
