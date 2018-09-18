package com.yoyo.conferenceservice.controller;

import com.kanz.conferenceservice.model.*;
import com.kanz.conferenceservice.model.mongodb.Conference;
import com.kanz.conferenceservice.service.ConferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class ConferenceController {

    private ConferenceService conferenceService;


    public ConferenceController(ConferenceService conferenceService){
        this.conferenceService = conferenceService;
    }

    @PostMapping("/event/create")
    public ResponseEntity<?> createEvent(@RequestBody Conference newConference, Principal user) {
        String username = user.getName();
        log.debug("received new conference {} for {}", newConference, username);
        return conferenceService.createEvent(newConference, username);
    }

    @PostMapping("/event/nearby")
    public ResponseEntity<List<Conference>> getNearByLocation(@RequestBody Location location){
        return conferenceService.nearByConferences(location);
    }


    @GetMapping("/event/myevents/{page}")
    public ResponseEntity<List<Conference>> myEvents(@PathVariable("page")Integer page, Principal user){
       return conferenceService.myEvents(user.getName(),page);
    }

    @GetMapping("/event/myevents")
    public ResponseEntity<List<Conference>> myEvents(Principal user){
        return myEvents(1,user);
    }


    @PostMapping("/event/modify/{eventId}")
    public ResponseEntity<?> modify(@PathVariable("eventId") String eventId,
                                    @RequestBody ModificationParcel modificationParcel,
                                    Principal user){
        String fieldName = modificationParcel.getFieldName();
        Object fieldValue = modificationParcel.getFieldValue();
        return conferenceService.edit(user.getName(),eventId,fieldName,fieldValue);
    }

    @GetMapping("/event/logo")
    public ResponseEntity<InputStreamResource> getLogo(){
        return conferenceService.logo();
    }

    // This for the admin request fro qrcode when the conference is public
    @GetMapping("/event/qrcode/{eventId}")
    public ResponseEntity<InputStreamResource> getEventQRCode(@PathVariable("eventId")String eventId, Principal user){
        return conferenceService.QRcode(eventId,user.getName());
    }

    @GetMapping("/event/get/{eventId}")
    public ResponseEntity<Conference> conference(@PathVariable("eventId")String eventId, Principal user){
       return conferenceService.conference(eventId,user.getName());

    }


    @PostMapping("/event/bullet/{eventId}")
    public ResponseEntity<?> bullet(@RequestBody Bullet bullet ,@PathVariable("eventId")String eventId, Principal user){
        log.debug(" I am called one");
        return conferenceService.bullet(bullet,eventId,user.getName());
    }

    @PostMapping("/event/ownerofconf")
    public ResponseEntity<?> ownerOfConf(@RequestBody RequestConferenceConfirm confirm){
        String username = confirm.getUsername();
        String eventId = confirm.getEventId();
        System.out.println(username + ", "+eventId);
        return conferenceService.ownerOfConference(username,eventId);
    }
}
