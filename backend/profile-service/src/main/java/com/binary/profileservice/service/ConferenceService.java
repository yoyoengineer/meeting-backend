package com.binary.profileservice.service;

import com.binary.profileservice.model.neo4j.Conference;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ConferenceService {

    ResponseEntity<?> newConference(Conference conference);
    ResponseEntity<List<Map<String,Object>>> myConferences(String username, Integer page);
    ResponseEntity<List<Map<String,Object>>> conferencesAttended(String username,Integer page);
    ResponseEntity<?> edit(String eventId,String fieldName,String fieldValue);
    ResponseEntity<List<Map<String,Object>>> conferenceLikedPeopleList(String eventId);
    ResponseEntity<List<Map<String,Object>>> conferenceAttendedPeopleList(String eventId);
    ResponseEntity<Integer> numberOfPeopleWhoLiked(String eventId);
    ResponseEntity<List<Map<String,Object>>> recommendFromMostAttendedOrganizer(String username);
    ResponseEntity<List<Map<String,Object>>> recommendFromMostPopular(String username);
    ResponseEntity<List<Map<String,Object>>> conferenceParticipate(String username);
    ResponseEntity<List<Map<String,Object>>> userConferences(String username);
    ResponseEntity<List<Map<String,Object>>> latest(String username);

}
