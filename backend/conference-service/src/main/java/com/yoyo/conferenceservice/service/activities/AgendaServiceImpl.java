package com.yoyo.conferenceservice.service.activities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanz.conferenceservice.event.model.ActivityEvent;
import com.kanz.conferenceservice.model.mongodb.Agenda;
import com.kanz.conferenceservice.model.mongodb.Conference;
import com.kanz.conferenceservice.model.mongodb.Person;
import com.kanz.conferenceservice.repository.mongodb.AgendaRepository;
import com.kanz.conferenceservice.repository.mongodb.ConferenceRepository;
import com.kanz.conferenceservice.repository.mongodb.PersonRepository;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.yoyo.conferenceservice.repository.mongodb.AgendaRepository;
import com.yoyo.conferenceservice.repository.mongodb.ConferenceRepository;
import com.yoyo.conferenceservice.repository.mongodb.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AgendaServiceImpl implements AgendaService{

    private AgendaRepository agendaRepository;
    private ObjectMapper objectMapper;
    private ConferenceRepository conferenceRepository;
    private ApplicationEventPublisher applicationEventPublisher;
    private PersonRepository personRepository;
    private final String MESSAGE = "message";

    @Autowired
    public AgendaServiceImpl(AgendaRepository agendaRepository,
                                   ConferenceRepository conferenceRepository,
                                   ApplicationEventPublisher applicationEventPublisher,
                                   PersonRepository personRepository,
                                   ObjectMapper objectMapper){
        this.agendaRepository = agendaRepository;
        this.conferenceRepository = conferenceRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.objectMapper = objectMapper;
        this.personRepository = personRepository;
    }

    @Override
    public ResponseEntity<List<Agenda>> getAgenda(String eventId, String username, UUID uuid) {
        log.debug("Received eventId {} for user {}", eventId, username);

        if (!UtilsClass.isStringNotNullOrEmpty(eventId, username)) {
            log.debug("Something is missing");;

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

                log.debug("user {} is not attendee of conference {}", username, conference);;
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        List<Agenda> agendas = agendaRepository.findByEventIdOrderByIdDesc(eventId);

        if (agendas == null)
            agendas = Collections.emptyList();
        return ResponseEntity.ok().body(agendas);
    }

    @Override
    public ResponseEntity<?> newAgenda(Agenda agenda, String owner) {

        Map<String,String> reason = new HashMap<>();

        log.debug("received new agenda {} by user {}",agenda,owner);

        if(agenda==null || !agenda.isValid() || !UtilsClass.isStringNotNullOrEmpty(owner)){
            log.debug("Something is missing");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String eventId = agenda.getEventId();
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


        agendaRepository.save(agenda);

        String agendaString = null;

        try {
            agendaString = objectMapper.writeValueAsString(agenda);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ActivityEvent activityEvent =
                new ActivityEvent(this,
                        agendaString,
                        agenda.getEventId(),ActivityEvent.Type.AGENDA);

        if(conference.isEventPublicState()){
            activityEvent.setToAll(true);
        }
        else{
            // Add the owner so that him too can receive the announcement
            activityEvent.setUsernames(Collections.singletonList(owner));
        }


        log.debug("sending the agenda to the application event handler");
        applicationEventPublisher.publishEvent(activityEvent);
        log.debug("Application sent to the application event handler");
        return ResponseEntity.ok().build();

    }


}
