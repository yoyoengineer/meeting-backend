package com.yoyo.conferenceservice.service.activities;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanz.conferenceservice.model.cassandra.Announcement;
import com.kanz.conferenceservice.event.model.ActivityEvent;
import com.kanz.conferenceservice.model.mongodb.Conference;
import com.kanz.conferenceservice.model.mongodb.Person;
import com.kanz.conferenceservice.repository.cassandra.AnnouncementRepository;
import com.kanz.conferenceservice.repository.mongodb.ConferenceRepository;
import com.kanz.conferenceservice.repository.mongodb.PersonRepository;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.yoyo.conferenceservice.repository.cassandra.AnnouncementRepository;
import com.yoyo.conferenceservice.repository.mongodb.ConferenceRepository;
import com.yoyo.conferenceservice.repository.mongodb.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.cassandra.repository.support.BasicMapId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AnnouncementServiceImpl implements AnnouncementService {

    private final String MESSAGE = "message";
    private AnnouncementRepository announcementRepository;
    private ConferenceRepository conferenceRepository;
    private ApplicationEventPublisher applicationEventPublisher;
    private PersonRepository personRepository;
    private ObjectMapper objectMapper;

    @Autowired
    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository,
                                   ConferenceRepository conferenceRepository,
                                   ApplicationEventPublisher applicationEventPublisher,
                                   PersonRepository personRepository,
                                   ObjectMapper objectMapper){
        this.announcementRepository = announcementRepository;
        this.conferenceRepository = conferenceRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.objectMapper = objectMapper;
        this.personRepository = personRepository;
    }
    @Override
    public ResponseEntity<?> newAnnouncement(Announcement announcement, String owner) {
        Map<String,String>  reason = new HashMap<>();

        log.debug("received new announcement {} by user {}",announcement,owner);

        if(announcement==null || !announcement.isValid() || !UtilsClass.isStringNotNullOrEmpty(owner)){
            log.debug("Something is missing");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String eventId = announcement.getEventId();
        Conference conference = conferenceRepository.findOne(eventId);

        if(conference==null) {
            log.debug("No conference is registered with eventId {} ", eventId);
            reason.put(MESSAGE,"no conference registered to this id");
            return new ResponseEntity<>(reason,HttpStatus.NOT_FOUND);
        }

        if(!conference.getUsername().equals(owner)){
            log.debug("user {} is not owner of conference {}",owner,conference);
            reason.put(MESSAGE,"user is not the owner of this conference");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }


        // To set a UUIDtime
        announcement.setUUIDtime();
        announcementRepository.save(announcement);

        String announcementString = null;

        try {
            announcementString = objectMapper.writeValueAsString(announcement);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ActivityEvent activityEvent =
                new ActivityEvent(this,
                        announcementString,
                        announcement.getEventId(),ActivityEvent.Type.ANNOUNCEMENT);

        if(conference.isEventPublicState()){
            activityEvent.setToAll(true);
        }
        else{
            // Add the owner so that him too can receive the announcement
            activityEvent.setUsernames(Collections.singletonList(owner));
        }



        log.debug("sending the announcement to the application event handler");
        applicationEventPublisher.publishEvent(activityEvent);
        log.debug("Application sent to the application event handler");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<Announcement>> getAnnouncements(String eventId, String username, UUID UUIDtime) {

        log.debug("Received eventId {} for user {}", eventId, username);

        if (!UtilsClass.isStringNotNullOrEmpty(eventId, username)) {
            log.debug("Something is missing");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);

        if (conference == null) {
            log.debug("No conference is registered with eventId {} ", eventId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(!conference.isEventPublicState() &&!conference.getUsername().equals(username)){
            Person person = personRepository.findByEventIdAndUsername(eventId,username);
            if(person==null || person.getState()!= Person.STATE.ATTEND) {
                log.debug("user {} is not attendee of conference {}", username, conference);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        List<Announcement> announcements = announcementRepository.findByEventIdBeforeTime(eventId, UUIDtime);

        if (announcements == null)
            announcements = Collections.emptyList();
        convertInUnixTimeStamp(announcements);
        return ResponseEntity.ok().body(announcements);

    }

    @Override
    public ResponseEntity<?> deleteAnnouncement(String eventId, UUID UUIDtime, String owner) {
        log.debug("Received delete request for eventId {} at {}", eventId, UUIDtime);

        if(!UtilsClass.isStringNotNullOrEmpty(eventId)){
            log.debug("Something is missing");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);

        if(conference==null) {
            log.debug("No conference is registered with eventId {} ", eventId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(!conference.getUsername().equals(owner)) {
            log.debug("user {} is not owner of conference {}", owner, conference);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        announcementRepository.delete(BasicMapId.id("eventId",eventId).with("uuidTime", UUIDtime));

        return ResponseEntity.ok().build();
    }

    private void convertInUnixTimeStamp(List<Announcement> announcements){
        if(announcements!=null){
            announcements.forEach(Announcement::setUUIDtime);
        }
    }
}
