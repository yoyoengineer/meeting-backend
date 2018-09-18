package com.yoyo.conferenceservice.event;


import com.kanz.conferenceservice.client.MyRestClient;
import com.kanz.conferenceservice.event.messagedriven.ActivitiesDispatcher;
import com.kanz.conferenceservice.event.messagedriven.ConferenceEventDispatcher;
import com.kanz.conferenceservice.event.model.ConferenceEvent;
import com.kanz.conferenceservice.event.model.ActivityEvent;
import com.kanz.conferenceservice.model.mongodb.Notification;
import com.kanz.conferenceservice.model.mongodb.Person;
import com.kanz.conferenceservice.repository.mongodb.NotificationRepository;
import com.yoyo.conferenceservice.client.MyRestClient;
import com.yoyo.conferenceservice.event.messagedriven.ActivitiesDispatcher;
import com.yoyo.conferenceservice.event.messagedriven.ConferenceEventDispatcher;
import com.yoyo.conferenceservice.event.model.ActivityEvent;
import com.yoyo.conferenceservice.event.model.ConferenceEvent;
import com.yoyo.conferenceservice.model.mongodb.Notification;
import com.yoyo.conferenceservice.model.mongodb.Person;
import com.yoyo.conferenceservice.repository.mongodb.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SimpleApplicationEventHandler {

    private MongoTemplate mongoTemplate;
    private MyRestClient myRestClient;
    private ActivitiesDispatcher activitiesDispatcher;
    private ConferenceEventDispatcher conferenceEventDispatcher;
    private NotificationRepository notificationRepository;

    @Autowired
    public SimpleApplicationEventHandler(MyRestClient myRestClient,
                                         MongoTemplate mongoTemplate,
                                         ActivitiesDispatcher activitiesDispatcher,
                                         ConferenceEventDispatcher conferenceEventDispatcher,
                                         NotificationRepository notificationRepository){

        this.myRestClient = myRestClient;
        this.mongoTemplate = mongoTemplate;
        this.activitiesDispatcher = activitiesDispatcher;
        this.conferenceEventDispatcher = conferenceEventDispatcher;
        this.notificationRepository = notificationRepository;
    }



    @EventListener
    public void handleConferenceEvent(ConferenceEvent conferenceEvent){
        if(conferenceEvent.isValid()){
            log.debug("Sending conference in event {}",conferenceEvent);
            conferenceEventDispatcher.publishEvent(conferenceEvent);
        }
    }

    @EventListener
    public void handleActivityEvent(ActivityEvent activityEvent){
        if(activityEvent.isValid()){
            if(activityEvent.getType()== ActivityEvent.Type.INVITATION){
                log.debug("Sending invitation");
                List<String> usernames = activityEvent.getUsernames();

                if(usernames!=null){
                    List<Notification> invitations =
                            usernames.stream()
                                    .map(username-> new Notification()
                                            .withEventId(activityEvent.getEventId())
                                            .withUsername(username)
                                            .withType(Notification.Type.INVITATION))
                                    .collect(Collectors.toList());
                    notificationRepository.save(invitations);
                }
            }
            else if(activityEvent.getType()!= ActivityEvent.Type.UPDATE
                    && activityEvent.getType()!= ActivityEvent.Type.NEW_VOTE
                    &&!activityEvent.isToAll()){
                log.debug("Sending other activities");
                activityEvent.setUsernames(findAttendants(activityEvent.getEventId()));
            }
            log.debug("Send the activity in event {}",activityEvent);

            activitiesDispatcher.publishActivity(activityEvent);

        }
    }



    private List<String> findAttendants(String eventId){

        List<Person> persons = mongoTemplate.find(
                new Query(Criteria
                        .where("eventId").is(eventId)
                        .and("state").is(Person.STATE.ATTEND)),Person.class);

        List<String> usernames = null;

        if(persons!=null) {
//            Set<Person> personSet = new HashSet<>(persons);
           usernames = persons.stream().map(Person::getUsername).collect(Collectors.toList());
            log.debug("Announcement to be sent to only {}",usernames);
        }

        if(usernames==null)
            usernames = Collections.emptyList();

        return usernames;
    }
}
