package com.yoyo.conferenceservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanz.conferenceservice.event.model.ActivityEvent;
import com.kanz.conferenceservice.event.model.ConferenceEvent;
import com.kanz.conferenceservice.model.Bullet;
import com.kanz.conferenceservice.model.ConferenceParcel;
import com.kanz.conferenceservice.model.Location;
import com.kanz.conferenceservice.model.mongodb.Conference;
import com.kanz.conferenceservice.model.mongodb.Invitation;
import com.kanz.conferenceservice.model.mongodb.Person;
import com.kanz.conferenceservice.repository.mongodb.ConferenceRepository;
import com.kanz.conferenceservice.repository.mongodb.InvitationRepository;
import com.kanz.conferenceservice.repository.mongodb.PersonRepository;
import com.kanz.conferenceservice.service.characters.AttendanceService;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.mongodb.gridfs.GridFSDBFile;
import com.yoyo.conferenceservice.repository.mongodb.ConferenceRepository;
import com.yoyo.conferenceservice.repository.mongodb.InvitationRepository;
import com.yoyo.conferenceservice.repository.mongodb.PersonRepository;
import com.yoyo.conferenceservice.service.characters.AttendanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConferenceServiceImpl implements ConferenceService {

    private final static int PAGE_SIZE = 10;


    private ConferenceRepository conferenceRepository;
    private ApplicationEventPublisher applicationEventPublisher;
    private MongoTemplate mongoTemplate;
    private ObjectMapper objectMapper;
    private AttendanceService attendanceService;
    private PersonRepository personRepository;
    private GridFsTemplate gridFsTemplate;
    private InvitationRepository invitationRepository;
    private QRCodeGenerator qrCodeGenerator;
    private final String MESSAGE = "message";


    @Autowired
    public ConferenceServiceImpl(ConferenceRepository conferenceRepository,
                                 ApplicationEventPublisher applicationEventPublisher,
                                 MongoTemplate mongoTemplate,
                                 AttendanceService attendanceService,
                                 ObjectMapper objectMapper,
                                 GridFsTemplate gridFsTemplate,
                                 InvitationRepository invitationRepository,
                                 PersonRepository personRepository,
                                 QRCodeGenerator qrCodeGenerator){
        this.conferenceRepository=conferenceRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
        this.attendanceService = attendanceService;
        this.gridFsTemplate = gridFsTemplate;
        this.invitationRepository = invitationRepository;
        this.qrCodeGenerator = qrCodeGenerator;
        this.personRepository = personRepository;

    }

    @Override
    @Transactional
    public ResponseEntity<?> createEvent(Conference newConference, String username) {

        if(newConference!=null && newConference.isValid()){

            // Move the address from the location object to the string address
            newConference.initializeAddressAndGeoPoint();
            // Set the number of remaining Participants to the capacity
            newConference.setRemainingNbOfParticipants(newConference.getCapacity());
            newConference.setUsername(username);

           // Save the conference and get the id number of the event
            newConference = conferenceRepository.save(newConference);
            String eventId = newConference.getId();
            log.debug("This is the eventId {} collected from the database ",eventId);

           if(eventId==null){
               log.debug("There is an internal server error");
               return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
           }


            String contentString = null;
            Map<String,Object> content = new HashMap<>();
            content.put("topic",newConference.getTopic());
            content.put("description",newConference.getDescription());
            content.put("time",newConference.getTime());
            content.put("username",username);
            content.put("town",newConference.getLocation().getTown());
            content.put("eventId",eventId);
            content.put("eventPublicState",newConference.isEventPublicState());


            try {
                contentString = objectMapper.writeValueAsString(content);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            ConferenceEvent conferenceEvent =new ConferenceEvent(this,contentString,ConferenceEvent.Type.NEW_CONFERENCE);
            log.debug("Sending conference event {} ",conferenceEvent);
            applicationEventPublisher.publishEvent(conferenceEvent);



            List<String> inviteeUsernames = newConference.getInvitees();
           if(inviteeUsernames!=null && !inviteeUsernames.isEmpty()){

               if(!newConference.isEventPublicState()){
                  List<Invitation> invitations =  inviteeUsernames.stream()
                           .map(invitee -> new Invitation().withEventId(eventId).withUsername(invitee)).collect(Collectors.toList());

                  invitationRepository.save(invitations);

               }

               contentString = null;
               content.clear();
               content.put("eventId",eventId);
               content.put("topic",newConference.getTopic());


               try {
                   contentString = objectMapper.writeValueAsString(content);
               } catch (JsonProcessingException e) {
                   e.printStackTrace();
               }

               ActivityEvent activityEvent =
                       new ActivityEvent(this,
                               contentString,
                               eventId,ActivityEvent.Type.INVITATION);
               activityEvent.setUsernames(inviteeUsernames);

               log.debug("sending the invitation activity to the application event handler");
               applicationEventPublisher.publishEvent(activityEvent);
               log.debug("activity sent to the activity event handler");
           }



           return new ResponseEntity(HttpStatus.OK);

        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    @Override
    public ResponseEntity<List<Conference>> nearByConferences(Location location) {
        log.debug("Received Location {}",location);
        if(location!=null && location.hasValidateCoordinates() ){

            /**
             * TRY TO THINK ABOUT PAGINATION TOO.
             */
            Point  point = new Point(location.getLongitude(),location.getLatitude());
            Distance distance = new Distance(50000, Metrics.KILOMETERS);
           List<Conference> nearbyConferences =  conferenceRepository.findByLocNear(point,distance);


           if(nearbyConferences==null)
               nearbyConferences = Collections.emptyList();

           else{
               for (Conference c: nearbyConferences)
                   c.initializeLocation();
           }

           return ResponseEntity.ok(nearbyConferences);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Better come from the profile-service
     * **/
    @Override
    public ResponseEntity<List<Conference>> myEvents(String username, Integer pageNumber) {
        log.debug("received request for events for user {} and pageNumber {}",username,pageNumber);

        if(!UtilsClass.isStringNotNullOrEmpty(username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

//        if(pageNumber<1)
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        PageRequest pageRequest = new PageRequest(pageNumber-1,PAGE_SIZE, Sort.Direction.DESC,"time");
        Page<Conference> page = conferenceRepository.findByUsernameOrderByTimeDesc(username,pageRequest);

        List<Conference> conferences = conferenceRepository.findByUsernameOrderByTimeDesc(username);
//        if(conferences==null)
//            conferences = Collections.emptyList();
//        if(page==null){
//            return ResponseEntity.ok(new ConferenceParcel(0,0,Collections.emptyList()));
//        }
        ConferenceParcel conferenceParcel =
                new ConferenceParcel(page.getNumber()+1,page.getTotalPages(),page.getContent());
        log.debug("conference to be sent to the user {}",conferences);
        return ResponseEntity.ok(conferences);
    }

    @Override
    public ResponseEntity<?> edit(String username, String eventId, String fieldName, Object fieldValue) {
        Map<String,String> reason = new HashMap<>();
        log.debug("received request for modification by {} for {} of event {}",username,fieldName,eventId);

        if(!UtilsClass.isStringNotNullOrEmpty(username,eventId,fieldName) || fieldName==null){
            log.debug("Something is missing");
            reason.put(MESSAGE,"Something is missing");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);

        if(!conference.getUsername().equals(username)){
            log.debug("Not authorized");
            reason.put(MESSAGE,"Not authorized");
            return new ResponseEntity<>(reason,HttpStatus.NOT_FOUND);
        }

        try {
            switch (fieldName) {
                case "topic":
                    String topic = (String) fieldValue;
                    if (!UtilsClass.isStringNotNullOrEmpty(topic)) {
                        log.debug("Bad topic request");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    updateField(eventId, "topic", topic);
                    publishConferenceEvent(eventId,fieldName,topic);
                    break;
                case "description":
                    String description = (String) fieldValue;
                    if (!UtilsClass.isStringNotNullOrEmpty(description)) {
                        log.debug("Bad description request");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    updateField(eventId, "description", description);
                    publishConferenceEvent(eventId,fieldName,description);
                    break;
                case "otherSpeakers":
                    List<String> speakers = (List<String>) fieldValue;
                    if (speakers == null || speakers.isEmpty()) {
                        log.debug("Bad speakers request");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    updateField(eventId, "otherSpeakers", speakers);
                    break;

                case "location":
                     Location location = objectMapper.convertValue(fieldValue,Location.class);
                    if (!location.isValid()) {
                        log.debug("Bad location request");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    updateField(eventId, "loc",
                            new GeoJsonPoint(location.getLongitude(),location.getLatitude()));
                    updateField(eventId,"address",location.getAddress());
                    publishConferenceEvent(eventId,"town",location.getTown());
                    attendanceService.notifyParticipants(eventId,"address",location.getAddress());
                    break;

                case "time":
                    Long time = objectMapper.convertValue(fieldValue,Long.class);
                    if (time==null || time <= 0) {
                        log.debug("Bad time request");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    updateField(eventId, "time", time);
                    publishConferenceEvent(eventId,fieldName,time);
                    attendanceService.notifyParticipants(eventId,"time",time);
                    break;
                case "capacity":
                    Integer capacity = objectMapper.convertValue(fieldValue,Integer.class);

                    if(capacity == null){
                        log.debug("capacity request is NULL");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }

                    capacity -=conference.getCapacity();
                    if(capacity<=0){
                        log.debug("Bad capacity request");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    updateCapacity(eventId,capacity);
                    break;
            }
        }
        catch(ClassCastException c){
            log.debug("Bad format");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<InputStreamResource> logo() {

        GridFSDBFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(QRCodeGenerator.APP_LOGO_ID)));


        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(file.getContentType()))
                .contentLength(file.getLength())
                .body(new InputStreamResource(file.getInputStream()));
    }

    @Override
    public ResponseEntity<InputStreamResource> QRcode(String eventId, String username) {
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

    @Override
    public ResponseEntity<?> bullet(Bullet bullet, String eventId, String username) {
        Map<String,String> reason = new HashMap<>();
        log.debug("bullet received {}",bullet);

        if(!bullet.isValid()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Conference conference = conferenceRepository.findOne(eventId);

        if(conference==null) {
            log.debug("No conference is registered with eventId {} ", eventId);
            reason.put(MESSAGE,"no conference registered to this id");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }

        if(!conference.isEventPublicState() && !conference.getUsername().equals(username)){
            Person person = personRepository.findByEventIdAndUsername(eventId,username);
            if(person==null || person.getState()!= Person.STATE.ATTEND) {
                log.debug("user not authorized");;
                reason.put(MESSAGE, "user not authorized");
                return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
            }
        }



        String commentString = null;

        try {
            commentString = objectMapper.writeValueAsString(bullet);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ActivityEvent activityEvent =
                new ActivityEvent(this,
                        commentString,
                        eventId,ActivityEvent.Type.BULLET);

        if(conference.isEventPublicState()){

            activityEvent.setToAll(true);
        }
        else{
            activityEvent.setUsernames(Collections.singletonList(conference.getUsername()));
        }

        log.debug("About to publish the activity event {}",activityEvent);
        applicationEventPublisher.publishEvent(activityEvent);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Conference> conference(String eventId, String username) {
        log.debug("Received a request for conference {} from {}",eventId, username);

        if( !UtilsClass.isStringNotNullOrEmpty(username,eventId)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);

        if(conference==null){
            log.debug("The bulletin refers a conference which do not exists");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        conference.initializeLocation();

        log.debug("The conference to be sent is {}",conference);

//
//        if(!conference.isEventPublicState() && !conference.getUsername().equals(username)){
//            Person person = personRepository.findByEventIdAndUsername(eventId,username);;
//
//            if(person==null || person.getState()!= Person.STATE.ATTEND){
//                log.debug("user not authorized");
//                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            }
//        }

        return ResponseEntity.ok(conference);
    }

    private boolean updateField(String eventId, String fieldName, Object fieldValue){
        Query query = new Query(Criteria.where("_id").is(eventId));
        Update update = new Update().set(fieldName,fieldValue);

        return mongoTemplate.updateFirst(query,update,Conference.class).isUpdateOfExisting();
    }

    private boolean updateCapacity(String eventId, int delta){
        Query query = new Query(Criteria.where("_id").is(eventId));
        Update update = new Update().inc("capacity",delta)
                .inc("remainingNbOfParticipants",delta);

        return mongoTemplate.updateFirst(query,update,Conference.class).isUpdateOfExisting();
    }

    private void publishConferenceEvent(String eventId,String fieldName, Object fieldValue) {
        String contentString = null;
        Map<String, Object> content = new HashMap<>();
        content.put("fieldName", fieldName);
        content.put("fieldValue",fieldValue);
        content.put("eventId",eventId);


        try {
            contentString = objectMapper.writeValueAsString(content);

            ConferenceEvent conferenceEvent = new ConferenceEvent(this, contentString, ConferenceEvent.Type.UPDATE);
            log.debug("Sending conference event {} ", conferenceEvent);
            applicationEventPublisher.publishEvent(conferenceEvent);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }

    @Override
    public ResponseEntity<?> ownerOfConference(String username, String eventId) {
        log.debug("Received a request for owner of conference {} from {}",eventId, username);

        if( !UtilsClass.isStringNotNullOrEmpty(username,eventId)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);

        if(conference==null){
            log.debug("The bulletin refers a conference which do not exists");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(conference.getUsername().equals(username))
            return ResponseEntity.ok().build();
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
