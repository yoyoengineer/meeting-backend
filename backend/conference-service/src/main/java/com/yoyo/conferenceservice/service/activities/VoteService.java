package com.yoyo.conferenceservice.service.activities;

import com.kanz.conferenceservice.model.Choice;
import com.kanz.conferenceservice.model.mongodb.Bulletin;
import com.yoyo.conferenceservice.model.mongodb.Bulletin;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface VoteService {

    ResponseEntity<?> newVote(Bulletin bulletin, String username);
    ResponseEntity<Bulletin> vote(Choice choice, String username);
    ResponseEntity<List<Bulletin>> bulletinsGet(String eventId, String bulletinId, String username);
    ResponseEntity<Bulletin> bulletinGet(String bulletinId, String username);
}
