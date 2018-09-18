package com.yoyo.conferenceservice.service.characters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanz.conferenceservice.event.model.ActivityEvent;
import com.kanz.conferenceservice.event.model.ConferenceEvent;
import com.kanz.conferenceservice.model.mongodb.Conference;
import com.kanz.conferenceservice.model.mongodb.Invitation;
import com.kanz.conferenceservice.model.mongodb.Notification;
import com.kanz.conferenceservice.model.mongodb.Person;
import com.kanz.conferenceservice.repository.mongodb.ConferenceRepository;
import com.kanz.conferenceservice.repository.mongodb.InvitationRepository;
import com.kanz.conferenceservice.repository.mongodb.NotificationRepository;
import com.kanz.conferenceservice.repository.mongodb.PersonRepository;
import com.kanz.conferenceservice.service.QRCodeGenerator;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.yoyo.conferenceservice.repository.mongodb.ConferenceRepository;
import com.yoyo.conferenceservice.repository.mongodb.InvitationRepository;
import com.yoyo.conferenceservice.repository.mongodb.NotificationRepository;
import com.yoyo.conferenceservice.repository.mongodb.PersonRepository;
import com.yoyo.conferenceservice.service.QRCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final String MESSAGE = "message";

    enum Action {INCREMENT,DECREMENT }

    private ConferenceRepository conferenceRepository;
    private PersonRepository personRepository;
    private MongoTemplate mongoTemplate;
    private ApplicationEventPublisher applicationEventPublisher;
    private ObjectMapper objectMapper;
    private NotificationRepository notificationRepository;
    private InvitationRepository invitationRepository;
    private QRCodeGenerator qrCodeGenerator;

    public AttendanceServiceImpl(ConferenceRepository conferenceRepository,
                                 PersonRepository personRepository,
                                 MongoTemplate mongoTemplate,
                                 ApplicationEventPublisher applicationEventPublisher,
                                 NotificationRepository notificationRepository,
                                 ObjectMapper objectMapper,
                                 QRCodeGenerator qrCodeGenerator,
                                 InvitationRepository invitationRepository){
        this.conferenceRepository = conferenceRepository;
        this.personRepository = personRepository;
        this.mongoTemplate = mongoTemplate;
        this.applicationEventPublisher = applicationEventPublisher;
        this.objectMapper = objectMapper;
        this.notificationRepository = notificationRepository;
        this.invitationRepository = invitationRepository;
        this.qrCodeGenerator = qrCodeGenerator;
    }


    @Override
    public ResponseEntity<?> participate(String eventId, String username) {
        Map<String,String> reason = new HashMap<>();
        log.debug("received eventId {} and owner {}", eventId,username);

        Map<String,String> map = new HashMap<>(); // Reason for not conflicts response

        if(!UtilsClass.isStringNotNullOrEmpty(eventId,username)) {
            log.debug("Something is missing eventId: {} , owner: {}",eventId,username);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);
        if(conference==null) {
            log.debug("conference does not exists");
            map.put(MESSAGE,"conference does not exists");
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }

        // Check first if the participant is already a participant
        if(personExists(eventId,username)){
            log.debug("participant exists already");
            map.put(MESSAGE,"Exists Already");
            return new ResponseEntity<>(map,HttpStatus.CONFLICT);
        }

        if(!conference.isEventPublicState()){
            Invitation invitation = invitationRepository.findByEventIdAndUsername(eventId,username);

            if(invitation==null){
                log.debug("This is a private ");
                reason.put(MESSAGE,"This is a private conference");
                return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
            }

            invitationRepository.delete(invitation);
        }


        if(isAlreadingPending(username,eventId)){
            log.debug("participation is under process");
            map.put(MESSAGE,"Pending participation");
            return  ResponseEntity.ok(map);
        }


        //Update the conference before saving the new participant
        if(updateParticipantConference(eventId,Action.DECREMENT)){
            Person newPerson = new Person(eventId,username, Person.STATE.PARTICIPATE);
            personRepository.save(newPerson);

            log.debug("Person {} persisted",newPerson);


            boolean removed = removeFromPending(username,eventId);
            log.debug("transaction removed? {} ", removed);

            Map<String,String> content = new HashMap<>();
            content.put("username",username);
            content.put("eventId",eventId);
            String contentString = null;

            try {
                contentString = objectMapper.writeValueAsString(content);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            ConferenceEvent conferenceEvent =new ConferenceEvent(this,contentString,ConferenceEvent.Type.PARTICIPATE);
            log.debug("Sending notification event {} ",conferenceEvent);
            applicationEventPublisher.publishEvent(conferenceEvent);
            return new ResponseEntity(HttpStatus.OK);
        }

        boolean removed = removeFromPending(username,eventId);
        log.debug("transaction outside removed? {} ", removed);

        map.put("message","No vacant place");
        log.debug("no vacant place");
        return new ResponseEntity<Map>(map,HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> countPersons(String eventId,Person.STATE state) {
        log.debug("received eventId for countPersons {} of state",eventId,state);
        if(UtilsClass.isStringNotNullOrEmpty(eventId) && state!=null) {
            long numberOfParticipants = count(eventId, state);
            log.debug("counted number is {}", numberOfParticipants);
            return ResponseEntity.ok(numberOfParticipants);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> deleteParticipant(String eventId, String username) {
        Map<String,String> reason = new HashMap<>();
        log.debug("received eventId {} and username {}", eventId,username);


        if(!UtilsClass.isStringNotNullOrEmpty(eventId,username)) {
            log.debug("Something is missing owner {}, eventId {}",username,eventId);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Person participant  = personRepository.findByEventIdAndUsername(eventId,username);

        if(participant==null) {
            log.debug("The user is not a participant");
            reason.put(MESSAGE,"The user is not a participant");
            return new ResponseEntity<>(reason,HttpStatus.NOT_FOUND);
        }

        if(participant.getState()==Person.STATE.ATTEND){
            log.debug("Can not disengage a already attending user");
            reason.put(MESSAGE,"Can not disengage a already attending user");
            return new ResponseEntity<>(reason,HttpStatus.CONFLICT);
        }


        Query query = new Query(Criteria.where("username").is(username)
                .and("eventId").is(eventId)
                .and("state").is(Person.STATE.PARTICIPATE));
        Update update = new Update().unset("state");

        boolean updated = mongoTemplate.updateFirst(query,update,Person.class).isUpdateOfExisting();
        log.debug("updated? {}",updated);

        if(updated) {
            // Otherwise
            String participantId = participant.getId();
            personRepository.delete(participantId);

            Map<String, String> content = new HashMap<>();
            content.put("username", username);
            content.put("eventId", eventId);
            String contentString = null;

            try {
                contentString = objectMapper.writeValueAsString(content);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            ConferenceEvent conferenceEvent = new ConferenceEvent(this, contentString, ConferenceEvent.Type.DISENGAGE);
            log.debug("Sending notification event {} ", conferenceEvent);
            applicationEventPublisher.publishEvent(conferenceEvent);

            updateParticipantConference(eventId, Action.INCREMENT);
        }


        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> deleteParticipant(String eventId, String username, String owner) {
        Map<String,String> reason = new HashMap<>();

        log.debug("received eventId {}, owner {} and owner {}", eventId,username,owner);

        if(!UtilsClass.isStringNotNullOrEmpty(eventId,username,owner))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        Conference conference = conferenceRepository.findOne(eventId);

        if(conference!=null){

            if(!owner.equals(conference.getUsername())){
                log.debug("not owner of the conference belong to {}",conference.getUsername());
                reason.put(MESSAGE,"not owner of the conference");
                return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
            }
            return deleteParticipant(eventId,username);

        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @Override
    @Transactional
    public ResponseEntity<?> attend(String eventId, String username) {

        log.debug("In attend method here");
        Map<String,String> map = new HashMap<>();
        log.debug("Attendee Service received eventId {} and username {}", eventId,username);
        Map<String,String> reason = new HashMap<>();
        if(!UtilsClass.isStringNotNullOrEmpty(eventId,username)){
            log.debug("Something is missing");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);
        if(conference==null) {
            log.debug("conference does not exists");
            map.put(MESSAGE,"conference does not exists");
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }

        if(!conference.isEventPublicState()){
            log.debug("Has to be signed in by the administrator");
            map.put(MESSAGE,"Have to be signed in by the administrator");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }

        return proceed(eventId,username);

    }


    @Override
    @Transactional
    public ResponseEntity<?> attend(String eventId, String username, String owner) {
        Map<String,String> map = new HashMap<>();

        if(!UtilsClass.isStringNotNullOrEmpty(eventId,username,owner)){
            map.put(MESSAGE,"Something is missing");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }

        log.debug("received request by admin {} for attendance in event {} on behalf of {}",owner,eventId,username);
        Conference conference = conferenceRepository.findOne(eventId);
        if(conference==null){
            log.debug("This conference does not exist");
            map.put(MESSAGE,"This conference does not exist");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }

        if(conference.getUsername().equals(owner)){
            return proceed(eventId,username);
        }
        log.debug("no owner of the meeting");
        map.put(MESSAGE,"not owner of the meeting");
        return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
    }

    @Transactional
    public ResponseEntity<?> proceed(String eventId,String username){

        if(!UtilsClass.isStringNotNullOrEmpty(username,eventId)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Map<String,String> reason = new HashMap<>();

//        Query query = new Query(Criteria
//                .where("username").is(username)
//                .and("eventId").is(eventId)
//                .and("state").is(Person.STATE.PARTICIPATE));
//        Update update = new Update().set("state",Person.STATE.ATTEND);
//        boolean updated = mongoTemplate.updateFirst(query,update,Person.class).isUpdateOfExisting();

        Person newPerson = new Person(eventId,username, Person.STATE.ATTEND);
        newPerson = personRepository.save(newPerson);

        boolean updated = newPerson != null;

        if(updated){

            Map<String,String> content = new HashMap<>();
            content.put("username",username);
            content.put("eventId",eventId);
            String contentString = null;

            try {
                contentString = objectMapper.writeValueAsString(content);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            ConferenceEvent conferenceEvent =new ConferenceEvent(this,contentString,ConferenceEvent.Type.ATTEND);
            log.debug("Sending conference event {} ",conferenceEvent);
            applicationEventPublisher.publishEvent(conferenceEvent);
            ActivityEvent activityEvent = new ActivityEvent(this,contentString,eventId, ActivityEvent.Type.ATTENDANCE);
            log.debug("Sending activity event {}",activityEvent);
            applicationEventPublisher.publishEvent(activityEvent);

            log.debug("Done");
            reason.put(MESSAGE,"Done");
            return  ResponseEntity.ok(reason);

        }

        Person person = personRepository.findByEventIdAndUsername(eventId,username);

        if(person==null){
            log.debug("User has to participate first");
            reason.put(MESSAGE,"User has to participate first");

        }

        else {
            log.debug("Already attending");
            reason.put(MESSAGE, "user is attending already");
        }
        return  ResponseEntity.ok(reason);

    }

    private boolean updateParticipantConference(String eventId, Action action){

        int delta;
        Query query;
        if(action==Action.INCREMENT) {
            delta = 1;
            query = new Query(Criteria.where("_id").is(eventId));
        }

        else {
            delta = -1;
            query = new Query(Criteria.where("_id").is(eventId)
                    .and("remainingNbOfParticipants").gt(0));
        }

        Update update = new Update().inc("remainingNbOfParticipants",delta);

        return mongoTemplate.updateFirst(query,update,Conference.class).isUpdateOfExisting();
    }


    private boolean conferenceExists(String eventId){

        Conference conference = conferenceRepository.findOne(eventId);
        return conference!=null;
    }

    private boolean personExists(String eventId, String username){
        Person person = personRepository.findByEventIdAndUsername(eventId,username);
        return person!=null;
    }

    private long count(String eventId, Person.STATE state){
        Query query = new Query(Criteria.where("eventId").is(eventId)
                .and("state").is(state));
        return mongoTemplate.count(query,Person.class);

    }


    @Async
    public void notifyParticipants(String eventId, String fieldName,Object fieldValue){

        log.debug("method called");
        if(!UtilsClass.isStringNotNullOrEmpty(eventId,fieldName)|| fieldValue==null){
            log.debug("Something is missing");
            return;

        }

        Query query = new Query(Criteria.where("eventId").is(eventId).and("state").is(Person.STATE.PARTICIPATE));
        query.fields().include("username");

        List<Person> persons = mongoTemplate.find(query,Person.class);

        if(persons!=null && !persons.isEmpty()){
            List<String> usernames = persons.stream()
                    .map(Person::getUsername).collect(Collectors.toList());

            List<Notification> notifications = usernames.stream()
                    .map(u->new Notification().withEventId(eventId)
                            .withFieldName(fieldName).withFieldValue(fieldValue)
                            .withUsername(u)
                            .withType(Notification.Type.MODIFICATION))
                    .collect(Collectors.toList());

            notificationRepository.save(notifications);

            String contentString = null;
            Map<String, Object> content = new HashMap<>();
            content.put(fieldName, fieldValue);
            content.put("eventId",eventId);


            try {
                contentString = objectMapper.writeValueAsString(content);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            ActivityEvent activityEvent =
                    new ActivityEvent(this,
                            contentString,
                            eventId, ActivityEvent.Type.UPDATE);
            activityEvent.setUsernames(usernames);

            log.debug("sending the city modification  activity to the application event handler");
            applicationEventPublisher.publishEvent(activityEvent);
            log.debug("activity sent to the activity event handler");
        }


    }

    @Override
    public ResponseEntity<InputStreamResource> userQRCode(String eventId, String username) {
        log.debug("received a request for logo by {} for event {} ",username,eventId);

        if(!UtilsClass.isStringNotNullOrEmpty(eventId,username)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);

        if(conference==null){
            log.debug("This conference does not exist");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        Query query = new Query(Criteria.where("username").is(username).and("eventId").is(eventId).and("state").is(Person.STATE.PARTICIPATE));
        Person participant = mongoTemplate.findOne(query,Person.class);

        boolean canSend= false;
        String dataToEncodeInQRCode = null;
        if(username.equals(conference.getUsername())){

            if(!conference.isEventPublicState()){
                log.debug("No need of a qr code");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            dataToEncodeInQRCode = eventId;
            canSend = true;
        }

        else if(!conference.isEventPublicState() &&( participant!=null)){
            dataToEncodeInQRCode = eventId+"/"+username;
            log.debug("Entered here");
            canSend = true;
        }


        if(canSend && dataToEncodeInQRCode!=null){

            try {
                ByteArrayOutputStream ouputStream = qrCodeGenerator.generateQRcode(dataToEncodeInQRCode);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(ouputStream.toByteArray());

                return ResponseEntity.ok()
                        .contentType(MediaType.valueOf("image/png"))
                        .contentLength(ouputStream.size())
                        .body(new InputStreamResource(inputStream));

            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }

        log.debug("user not allowed");
        // You can think about adding photos for responses that expect photos
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    private boolean isAlreadingPending(String username, String eventId){
        Query query = new Query(Criteria.where("_id").is(eventId).and("pendingParticipations").ne(username));
        Update update = new Update().push("pendingParticipations",username);
        return !mongoTemplate.updateFirst(query,update,Conference.class).isUpdateOfExisting();
    }

    private boolean removeFromPending(String username,String eventId){
        Query query = new Query(Criteria.where("_id").is(eventId));
        Update update = new Update().pull("pendingParticipations",username);
        return mongoTemplate.updateFirst(query,update,Conference.class).isUpdateOfExisting();
    }
}
