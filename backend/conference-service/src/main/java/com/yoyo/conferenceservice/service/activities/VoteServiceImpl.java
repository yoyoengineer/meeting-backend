package com.yoyo.conferenceservice.service.activities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanz.conferenceservice.model.Choice;
import com.kanz.conferenceservice.model.cassandra.Voter;
import com.kanz.conferenceservice.event.model.ActivityEvent;
import com.kanz.conferenceservice.model.mongodb.Conference;
import com.kanz.conferenceservice.model.mongodb.Bulletin;
import com.kanz.conferenceservice.model.mongodb.Person;
import com.kanz.conferenceservice.repository.cassandra.VoterRepository;
import com.kanz.conferenceservice.repository.mongodb.BulletinRepository;
import com.kanz.conferenceservice.repository.mongodb.ConferenceRepository;
import com.kanz.conferenceservice.repository.mongodb.PersonRepository;
import com.kanz.conferenceservice.ustils.UtilsClass;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.cassandra.repository.MapId;
import org.springframework.data.cassandra.repository.support.BasicMapId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class VoteServiceImpl implements VoteService {

    private VoterRepository voterRepository;
    private PersonRepository personRepository;
    private ConferenceRepository conferenceRepository;
    private BulletinRepository bulletinRepository;
    private ApplicationEventPublisher applicationEventPublisher;
    private ObjectMapper objectMapper;
    private MongoTemplate mongoTemplate;

    private final String MESSAGE = "message";
    @Autowired
    public VoteServiceImpl(VoterRepository voterRepository,
                           PersonRepository personRepository,
                           ConferenceRepository conferenceRepository,
                           BulletinRepository bulletinRepository,
                           ApplicationEventPublisher applicationEventPublisher,
                           ObjectMapper objectMapper,
                           MongoTemplate mongoTemplate){
        this.voterRepository = voterRepository;
        this.personRepository = personRepository;
        this.conferenceRepository = conferenceRepository;
        this.bulletinRepository = bulletinRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.objectMapper = objectMapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ResponseEntity<?> newVote(Bulletin bulletin, String username) {
        Map<String,String> reason = new HashMap<>();
        log.debug("Received new vote {} from {}", bulletin,username);
        if(!UtilsClass.isStringNotNullOrEmpty(username) || !bulletin.isValid()){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(bulletin.getEventId());

        if(conference==null){
            reason.put(MESSAGE,"no conference registered with this id");
            return new ResponseEntity<>(reason,HttpStatus.NOT_FOUND);
        }

        if(!conference.getUsername().equals(username)){
            reason.put(MESSAGE,"user not authorized");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }

        bulletin.initializeResults();
        bulletinRepository.save(bulletin);

        String announcementString = null;

        bulletin.setResults(null);
        try {
            announcementString = objectMapper.writeValueAsString(bulletin);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ActivityEvent activityEvent =
                new ActivityEvent(this,
                        announcementString,
                        bulletin.getEventId(),ActivityEvent.Type.VOTE);

        if(conference.isEventPublicState()){
            activityEvent.setToAll(true);
        }
        else{
            activityEvent.setUsernames(Collections.singletonList(conference.getUsername()));
        }



        log.debug("sending the activity to the application event handler");
        applicationEventPublisher.publishEvent(activityEvent);
        log.debug("Application sent to the application event handler");
        return ResponseEntity.ok().build();


    }

    @Override
    public ResponseEntity<Bulletin> vote(Choice choice, String username) {
        log.debug("Received a vote for {} from {}",choice,username);

        if(!choice.isValid() || !UtilsClass.isStringNotNullOrEmpty(username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Bulletin bulletin = bulletinRepository.findOne(choice.getBulletinId());

        if(bulletin==null){
            log.debug("Bulletin doesn't exists");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(bulletin.getResults().get(choice.getChoice())==null){
            log.debug("There is no such a choice");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        String eventId = bulletin.getEventId();
        Conference conference = conferenceRepository.findOne(eventId);

        if(conference==null){
            log.debug("The bulletin refers a conference which do not exists");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        if(!conference.isEventPublicState() && !conference.getUsername().equals(username)){
            Person person = personRepository.findByEventIdAndUsername(eventId,username);

            if(person==null || person.getState()!= Person.STATE.ATTEND){
                log.debug("user not authorized");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        MapId id = BasicMapId.id("bulletinId",choice.getBulletinId())
                .with("username",username);
        Voter voter = voterRepository.findOne(id);

        String bulletinId = bulletin.getId();

        if(voter!=null && voter.isValid() ){
            log.debug("Voted already");
            bulletin = bulletinRepository.findOne(bulletinId);
            bulletin.setUserVoted(true);
            return ResponseEntity.ok(bulletin);
        }


        if(isAlreadingPending(username,bulletinId)){
            log.debug("vote is under process");
            bulletin = bulletinRepository.findOne(bulletinId);
            bulletin.setUserVoted(true);
            return  ResponseEntity.ok(bulletin);
        }


        Query query = new Query(Criteria.where("_id").is(bulletin.getId()));
        Update update = new Update().inc("results."+choice.getChoice(),1);

        boolean ok = mongoTemplate.updateFirst(query,update,Bulletin.class).isUpdateOfExisting();

        if(ok) {
            bulletin = bulletinRepository.findOne(bulletin.getId());
            voter = new Voter(bulletin.getId(),username,choice.getChoice());
            log.debug("new voter {}",voter);
            voterRepository.save(voter);
            log.debug("New bulletin {}",bulletin);
            boolean removed = removeFromPending(username,bulletinId);
            log.debug("transaction removed? {} ", removed);


            Map<String,String> content = new HashMap<>();
            content.put("bulletinId",bulletinId);
            String contentString = null;

            try {
                contentString = objectMapper.writeValueAsString(content);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            ActivityEvent activityEvent =
                    new ActivityEvent(this,
                            contentString,
                            eventId,ActivityEvent.Type.NEW_VOTE);

            if(conference.isEventPublicState()){
                activityEvent.setToAll(true);
            }
            else{
                activityEvent.setUsernames(Collections.singletonList(conference.getUsername()));
            }
            log.debug("About to publish the activity event {}",activityEvent);
            applicationEventPublisher.publishEvent(activityEvent);


            return ResponseEntity.ok(bulletin);
        }

        boolean removed = removeFromPending(username,bulletinId);
        log.debug("transaction out side removed? {} ", removed);

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);



    }

    @Override
    public ResponseEntity<List<Bulletin>> bulletinsGet(String eventId, String bulletinId, String username) {
        log.debug("received request for bulletins of conference {} from bulletinId by {} ",eventId,bulletinId,username);

        if(!UtilsClass.isStringNotNullOrEmpty(eventId,username)|| bulletinId==null){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);
        if(conference==null){
            log.debug("The bulletin refers a conference which do not exists");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(!conference.isEventPublicState() && !conference.getUsername().equals(username)) {
            Person person = personRepository.findByEventIdAndUsername(eventId, username);

            if (person == null || person.getState() != Person.STATE.ATTEND) {
                log.debug("User not authorize");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        List<Bulletin> bulletins;

        if(!bulletinId.isEmpty()) {
            Query query = new Query(Criteria.where("eventId").is(eventId).and("_id").lt(new ObjectId(bulletinId)))
                    .with(new Sort(Sort.Direction.DESC,"_id")).limit(3);

            bulletins = mongoTemplate.find(query,Bulletin.class);
        }
        else
            bulletins = bulletinRepository.findByEventIdOrderByIdDesc(eventId);

        if(bulletins==null)
            bulletins = Collections.emptyList();

        // This is just for listing purpose so the results are not shown.
        bulletins.forEach(bulletin->{

            MapId id = BasicMapId.id("bulletinId",bulletin.getId()).with("username",username);
            Voter voter = voterRepository.findOne(id);

            if(voter!=null && voter.isValid()){
                bulletin.setUserVoted(true);
                bulletin.setMyChoice(voter.getChoice());
            }
            else{
                bulletin.optionsToList();
                bulletin.setResults(null);
            }


        });

        log.debug("bulletins to be sent {}",bulletins);
        return ResponseEntity.ok(bulletins);
    }

    @Override
    public ResponseEntity<Bulletin> bulletinGet(String bulletinId, String username) {
        log.debug("received request for bulletin {} for user {}",bulletinId,username);

        if(!UtilsClass.isStringNotNullOrEmpty(bulletinId,username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Bulletin bulletin = bulletinRepository.findOne(bulletinId);

        if(bulletin==null){
            log.debug("No bulletin registered with this id");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Conference conference = conferenceRepository.findOne(bulletin.getEventId());

        if(conference==null){
            log.debug("No conference registered with this id");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(!conference.isEventPublicState() && !conference.getUsername().equals(username)){
            Person person = personRepository.findByEventIdAndUsername(conference.getId(),username);

            if(person==null || person.getState()!= Person.STATE.ATTEND){
                log.debug("Not authorized");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }


        MapId id = BasicMapId.id("bulletinId",bulletinId).with("username",username);
        Voter voter = voterRepository.findOne(id);

        if(voter!=null && voter.isValid()){
            bulletin.setUserVoted(true);
            bulletin.setMyChoice(voter.getChoice());
        }

        // To make sure the organizer always can see the results no matter what.
        bulletin.optionsToList();
//        else if(!username.equals(conference.getUsername())){
//            bulletin.optionsToList();
//            log.debug("bulletin is {}",bulletin);
//            bulletin.setResults(null);
//        }

        log.debug("Bulletin to be set {}",bulletin);
        return ResponseEntity.ok(bulletin);

    }

    private boolean isAlreadingPending(String username, String bulletinId){
        Query query = new Query(Criteria.where("_id").is(bulletinId).and("pendingVotes").ne(username));
        Update update = new Update().push("pendingVotes",username);
        return !mongoTemplate.updateFirst(query,update,Bulletin.class).isUpdateOfExisting();
    }

    private boolean removeFromPending(String username,String bulletinId){
        Query query = new Query(Criteria.where("_id").is(bulletinId));
        Update update = new Update().pull("pendingVotes",username);
        return mongoTemplate.updateFirst(query,update,Bulletin.class).isUpdateOfExisting();
    }
}
