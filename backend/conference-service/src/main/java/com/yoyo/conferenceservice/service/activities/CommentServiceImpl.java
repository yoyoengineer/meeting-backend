package com.yoyo.conferenceservice.service.activities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanz.conferenceservice.client.MyRestClient;
import com.kanz.conferenceservice.event.model.ActivityEvent;
import com.kanz.conferenceservice.model.mongodb.Comment;
import com.kanz.conferenceservice.model.mongodb.Conference;
import com.kanz.conferenceservice.model.mongodb.Person;
import com.kanz.conferenceservice.repository.mongodb.CommentRepository;
import com.kanz.conferenceservice.repository.mongodb.ConferenceRepository;
import com.kanz.conferenceservice.repository.mongodb.PersonRepository;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.yoyo.conferenceservice.repository.mongodb.CommentRepository;
import com.yoyo.conferenceservice.repository.mongodb.ConferenceRepository;
import com.yoyo.conferenceservice.repository.mongodb.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private ApplicationEventPublisher applicationEventPublisher;
    private CommentRepository commentRepository;
    private StringRedisTemplate stringRedisTemplate;
    private MyRestClient myRestClient;
    private ConferenceRepository conferenceRepository;
    private PersonRepository personRepository;
    private final String PROFILE_USERNAME = "profile";
    private final String PROFILE_PASSWORD = "password";
    private final String MESSAGE = "message";
    private ObjectMapper objectMapper;
    private MongoTemplate mongoTemplate;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              StringRedisTemplate stringRedisTemplate,
                              MyRestClient myRestClient,
                              ApplicationEventPublisher applicationEventPublisher,
                              ConferenceRepository conferenceRepository,
                              PersonRepository personRepository,
                              ObjectMapper objectMapper,
                              MongoTemplate mongoTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
        this.commentRepository = commentRepository;
        this.myRestClient = myRestClient;
        this.applicationEventPublisher = applicationEventPublisher;
        this.conferenceRepository = conferenceRepository;
        this.personRepository = personRepository;
        this.objectMapper = objectMapper;
        this.mongoTemplate = mongoTemplate;

    }

    @Override
    public ResponseEntity<?> comment(Comment comment) {
        Map<String,String> reason = new HashMap<>();
        log.debug("comment received {}",comment);

        if(!comment.isValid()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Conference conference = conferenceRepository.findOne(comment.getEventId());

        if(conference==null) {
            log.debug("No conference is registered with eventId {} ", comment.getEventId());
            reason.put(MESSAGE,"no conference registered to this id");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }

        if(!conference.isEventPublicState() && !conference.getUsername().equals(comment.getUsername())){
            Person person = personRepository.findByEventIdAndUsername(comment.getEventId(),comment.getUsername());
            if(person==null || person.getState()!= Person.STATE.ATTEND) {
                log.debug("user not authorized");
                reason.put(MESSAGE, "user not authorized");
                return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
            }
        }

        String name = stringRedisTemplate.opsForValue().get(comment.getUsername());
        log.debug("{} 's name is {}",comment.getUsername(),name);


        if(name==null)
            name="";
        comment.setName(name);
        comment.setTime();

        log.debug("comment to be saved {}",comment);
        commentRepository.save(comment);

        String commentString = null;

        try {
            commentString = objectMapper.writeValueAsString(comment);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ActivityEvent activityEvent =
                new ActivityEvent(this,
                        commentString,
                        comment.getEventId(),ActivityEvent.Type.COMMENT);

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
    public ResponseEntity<List<Comment>> commentsGet(String eventId, String id, String username) {
        Map<String,String> reason= new HashMap<>();
        log.debug("received request for comments of {} after {} by {}",eventId,id,username);
        if(!UtilsClass.isStringNotNullOrEmpty(eventId,username) || id==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);

        if(conference==null){
            log.debug("no conference registered to this id");
            reason.put(MESSAGE,"no conference registered to this id");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(!conference.isEventPublicState() && !conference.getUsername().equals(username)) {
            Person person = personRepository.findByEventIdAndUsername(eventId, username);

            if (person == null || person.getState() != Person.STATE.ATTEND) {
                log.debug("not authorized");
                reason.put(MESSAGE, "not authorized");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        List<Comment> comments;

        if(!id.isEmpty()) {
            Query query = new Query(Criteria.where("eventId").is(eventId).and("_id").lt(new ObjectId(id)))
                    .with(new Sort(Sort.Direction.DESC,"_id"));//.limit(4);

            comments = mongoTemplate.find(query,Comment.class);
        }
        else
            comments = commentRepository.findByEventIdOrderByIdDesc(eventId);

        if(comments==null)
            comments = Collections.emptyList();

        return ResponseEntity.ok(comments);
    }

    @Override
    public ResponseEntity<?> deleteComment(String id, String username) {
        Map<String,String> reason = new HashMap<>();
        log.debug("Received request for deletion of id {} by {}",id,username);

        if(!UtilsClass.isStringNotNullOrEmpty(id,username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Comment comment = commentRepository.findOne(id);
        if(comment==null){
            reason.put(MESSAGE,"no such comment");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Conference conference = conferenceRepository.findOne(comment.getEventId());

        if(conference==null){
            reason.put(MESSAGE,"no such conference");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(!comment.getUsername().equals(username)
                && !conference.getUsername().equals(username)){
            reason.put(MESSAGE,"not authorized");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }

        log.debug("deleted");
        commentRepository.delete(comment);

        return ResponseEntity.ok().build();
    }
}
