package com.binary.profileservice.controller;

import com.binary.profileservice.service.ConferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
public class ConferenceController {

    private ConferenceService conferenceService;

    @Autowired
    public ConferenceController(ConferenceService conferenceService){
        this.conferenceService = conferenceService;
    }


    @GetMapping("/profile/conferences/mine/{page}")
    public ResponseEntity<List<Map<String,Object>>> myConferences(@PathVariable("page")Integer page, Principal user){
        return conferenceService.myConferences(user.getName(),page);
    }

    @GetMapping("/profile/conferences/mine")
    public ResponseEntity<List<Map<String,Object>>> myConferences( Principal user){
        return myConferences(0,user);
    }

    @GetMapping("/profile/conferences/user/{username}")
    public ResponseEntity<List<Map<String,Object>>> userConferences(@PathVariable("username")String username){
        return conferenceService.userConferences(username);
    }
    @GetMapping("/profile/conferences/attended/{page}")
    public ResponseEntity<List<Map<String,Object>>> conferencesAttended(@PathVariable("page")Integer page, Principal user){
        return conferenceService.conferencesAttended(user.getName(),page);
    }

    @GetMapping("/profile/conferences/participate")
    public ResponseEntity<List<Map<String,Object>>> conferenceParticipate(Principal user){

        return conferenceService.conferenceParticipate(user.getName());
    }

    @GetMapping("/profile/conferences/attended")
    public ResponseEntity<List<Map<String,Object>>> conferencesAttended( Principal user){
        return conferencesAttended(0,user);
    }

    @GetMapping("/profile/conference/likes/number/{eventId}")
    public ResponseEntity<Integer> nbrOfLikes(@PathVariable("eventId")String eventId){
        return conferenceService.numberOfPeopleWhoLiked(eventId);
    }

    @GetMapping("/profile/conference/likes/people/{eventId}")
    public ResponseEntity<List<Map<String,Object>>> conferenceLikedBy(@PathVariable("eventId")String eventId){
        return conferenceService.conferenceLikedPeopleList(eventId);
    }

    @GetMapping("/profile/conference/attendees/people/{eventId}")
    public ResponseEntity<List<Map<String,Object>>> conferenceAttendeddBy(@PathVariable("eventId")String eventId){
        return conferenceService.conferenceAttendedPeopleList(eventId);
    }

    @GetMapping("/profile/conference/mac")
    public ResponseEntity<List<Map<String,Object>>> getFromMostMostAttendedByOrganizer(Principal user){
        return conferenceService.recommendFromMostAttendedOrganizer(user.getName());
    }

    @GetMapping("/profile/conference/mpcct")
    public ResponseEntity<List<Map<String,Object>>> getFromMostPopular(Principal user){
        return conferenceService.recommendFromMostPopular(user.getName());
    }

    @GetMapping("/profile/conference/latest")
    public ResponseEntity<List<Map<String,Object>>> getLatest(Principal user){
        return conferenceService.latest(user.getName());
    }



}
