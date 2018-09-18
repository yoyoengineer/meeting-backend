package com.binary.profileservice.event;


import com.binary.profileservice.event.model.ConferenceEvent;
import com.binary.profileservice.event.model.NewUserEvent;
import com.binary.profileservice.model.EventPersonParcel;
import com.binary.profileservice.model.neo4j.Conference;
import com.binary.profileservice.model.neo4j.Person;
import com.binary.profileservice.service.ConferenceService;
import com.binary.profileservice.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import java.io.IOException;
import java.util.Map;

@EnableBinding(MyChannels.class)
@Slf4j
public class MyEventHandlers {

    private PersonService personService;
    private ConferenceService conferenceService;
    public ObjectMapper objectMapper;

    @Autowired
    public MyEventHandlers(PersonService personService,
                           ConferenceService conferenceService,
                           ObjectMapper objectMapper){
        this.personService = personService;
        this.conferenceService = conferenceService;
        this.objectMapper = objectMapper;
    }

    @StreamListener("conferenceEvent")
    public void getNotification(ConferenceEvent conferenceEvent){
        log.debug("conference event received: {}",conferenceEvent);

        if(!conferenceEvent.isValid()){
            log.debug("something is missing");
            return ;
        }


        switch(conferenceEvent.getType()){

            case PARTICIPATE:
                try {
                    EventPersonParcel eventPersonParcel = objectMapper.readValue(conferenceEvent.getContent(),
                            EventPersonParcel.class);
                personService
                        .participate(eventPersonParcel.getUsername(),
                                eventPersonParcel.getEventId());
                }
                catch(IOException e){
                    log.debug("failed");
                }
                break;

            case ATTEND:
                try {
                    EventPersonParcel eventPersonParcel = objectMapper.readValue(conferenceEvent.getContent(),
                            EventPersonParcel.class);
                personService
                        .attend(eventPersonParcel.getUsername(),
                                eventPersonParcel.getEventId());
                }
                catch(IOException e){
                    log.debug("failed");
                }
                break;
            case NEW_CONFERENCE:
                try {
                    Conference  conference = objectMapper
                            .readValue(conferenceEvent.getContent()
                                    ,Conference.class);
                    conferenceService.newConference(conference);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.debug("Errror");
                }
                break;


            case UPDATE:
                break;

            case DISENGAGE:
        }

    }

    @StreamListener("newUserEvent")
    public void getNotification(NewUserEvent newUserEvent){
        log.debug("New user is {}", newUserEvent);
        if(!newUserEvent.isValid()){
            log.debug("Not valid");
            return;
        }
        Person person = new Person();
        person.setUsername(newUserEvent.getUsername());
        personService.newUser(person);
    }

}
