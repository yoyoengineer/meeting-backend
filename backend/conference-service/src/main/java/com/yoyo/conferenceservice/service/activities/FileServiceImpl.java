package com.yoyo.conferenceservice.service.activities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanz.conferenceservice.event.model.ActivityEvent;
import com.kanz.conferenceservice.model.mongodb.Conference;
import com.kanz.conferenceservice.model.mongodb.FileModel;
import com.kanz.conferenceservice.model.mongodb.Person;
import com.kanz.conferenceservice.repository.mongodb.ConferenceRepository;
import com.kanz.conferenceservice.repository.mongodb.FileModelRepository;
import com.kanz.conferenceservice.repository.mongodb.PersonRepository;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FileServiceImpl implements FileService{

    private FileModelRepository fileModelRepository;
    private GridFsTemplate gridFsTemplate;
    private ApplicationEventPublisher applicationEventPublisher;
    private ObjectMapper objectMapper;
    private ConferenceRepository conferenceRepository;
    private PersonRepository personRepository;
    private MongoTemplate mongoTemplate;
    private final String MESSAGE = "message";
    private final String EVENT_ID = "eventId";


    public FileServiceImpl(FileModelRepository fileModelRepository,
                           GridFsTemplate gridFsTemplate,
                           ApplicationEventPublisher applicationEventPublisher,
                           ObjectMapper objectMapper,
                           ConferenceRepository conferenceRepository,
                           MongoTemplate mongoTemplate,
                           PersonRepository personRepository){
        this.fileModelRepository = fileModelRepository;
        this.gridFsTemplate = gridFsTemplate;
        this.applicationEventPublisher = applicationEventPublisher;
        this.objectMapper = objectMapper;
        this.conferenceRepository = conferenceRepository;
        this.personRepository = personRepository;
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public ResponseEntity<?> uploadFile(MultipartFile multipartFile, String eventId, String username) {

        Map<String,String> reason = new HashMap<>();
        log.debug("New File upload for event {} by {}",eventId,username);
        if(multipartFile==null||!UtilsClass.isStringNotNullOrEmpty(eventId,username)){
            log.debug("Something is missing");
            reason.put(MESSAGE,"Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }



        FileModel fileModel = new FileModel();
        fileModel.setFileName(multipartFile.getOriginalFilename());
        fileModel.setContentType(multipartFile.getContentType());
        fileModel.setSize(multipartFile.getSize());
        fileModel.setEventId(eventId);
        fileModel.setOwner(username);

        if(!fileModel.isValid()){
            log.debug("It is not a valid file");
            reason.put(MESSAGE,"It is not a valid file");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(fileModel.getEventId());

        if(conference==null) {
            log.debug("No conference is registered with eventId {} ", fileModel.getEventId());
            reason.put(MESSAGE,"no conference registered to this id");
            return new ResponseEntity<>(reason,HttpStatus.NOT_FOUND);
        }

        if(!username.equals(conference.getUsername())){
            log.debug("user not authorized");
            reason.put(MESSAGE,"user not authorized");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }

        try {
            DBObject metaData = new BasicDBObject();
            metaData.put(EVENT_ID, eventId);
            GridFSFile file = gridFsTemplate.store(multipartFile.getInputStream(),
                    multipartFile.getOriginalFilename(),
                    multipartFile.getContentType(),
                    metaData);
            fileModel.setFileId(file.getId().toString());
            fileModel.setTime(file.getUploadDate().getTime());
            fileModelRepository.save(fileModel);

            log.debug("File saved");
            log.debug("About to send an event");
            String fileModelString = null;

            try {
                fileModelString = objectMapper.writeValueAsString(fileModel);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            ActivityEvent activityEvent =
                    new ActivityEvent(this,
                            fileModelString,
                            fileModel.getEventId(),ActivityEvent.Type.FILE);

            if(conference.isEventPublicState()){
                activityEvent.setToAll(true);
            }
            else{
                activityEvent.setUsernames(Collections.singletonList(username));
            }

            log.debug("About to publish the activity event {}",activityEvent);
            applicationEventPublisher.publishEvent(activityEvent);

            reason.put(MESSAGE,"saved");
            return ResponseEntity.ok(reason);
        }
        catch (IOException e) {
            log.debug("Couldn't upload file");
            reason.put(MESSAGE,"Couldn't upload the file");
            return new ResponseEntity<>(reason,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadFile(String eventId, String fileId, String username) {
        log.debug("received request for eventId{} and fileId {}",eventId,fileId);
        if(!UtilsClass.isStringNotNullOrEmpty(eventId,fileId,username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);

        if(conference==null){
            log.debug("conference does not exist");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(!conference.isEventPublicState() && !conference.getUsername().equals(username)){
            Person person = personRepository.findByEventIdAndUsername(eventId,username);

            if(person==null || person.getState()!= Person.STATE.ATTEND){
                log.debug("Person not authorized");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        GridFSDBFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));
        if(file==null){
            log.debug("File not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        DBObject metadata = file.getMetaData();

        if(metadata!=null) {
            String eventIdFromFile =
                    metadata.get(EVENT_ID)!=null?metadata.get(EVENT_ID).toString():null;
            if (eventIdFromFile!=null && !eventId.equals(eventIdFromFile)){
                log.debug("Not allowed to download this file");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }



        log.debug("Sending the file");
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(file.getContentType()))
                .contentLength(file.getLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=" + file.getFilename())
                .body(new InputStreamResource(file.getInputStream()));
    }

    @Override
    public ResponseEntity<?> deleteFile(String fileId, String username) {

        Map<String,String> reason = new HashMap<>();
        log.debug("received deletion request for {} by {}",fileId,username);

        if(!UtilsClass.isStringNotNullOrEmpty(fileId,username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        FileModel fileModel = fileModelRepository.findFirstByFileId(fileId);
        if(fileModel==null){
            log.debug("file not exists");
            reason.put(MESSAGE,"No such file");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }

        if(!fileModel.getOwner().equals(username)){
            log.debug("user not authorized");
            reason.put(MESSAGE,"user not authorized");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }

        gridFsTemplate.delete(new Query(Criteria.where("_id").is(fileId)));
        fileModelRepository.deleteByFileId(fileId);

        reason.put(MESSAGE,"deleted");
        return ResponseEntity.ok(reason);

    }

    @Override
    public ResponseEntity<List<FileModel>> fileListGet(String eventId, String username) {
        log.debug("received request for {} by {}",eventId,username);

        if(!UtilsClass.isStringNotNullOrEmpty(eventId,username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);

        if(conference==null){
            log.debug("conference does not exist");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(!conference.isEventPublicState() && !conference.getUsername().equals(username)){
            Person person = personRepository.findByEventIdAndUsername(eventId,username);
            if(person==null || person.getState()!= Person.STATE.ATTEND ){
                log.debug("user  not authorized");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        List<FileModel> fileModels = fileModelRepository.findByEventIdOrderByTimeDesc(eventId);
        if(fileModels==null)fileModels = Collections.emptyList();
        return ResponseEntity.ok(fileModels);
    }

    @Override
    public ResponseEntity<List<FileModel>> photos(String eventId, String username) {

        log.debug("received photo request for {} by {}",eventId,username);

        if(!UtilsClass.isStringNotNullOrEmpty(eventId,username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);

        if(conference==null){
            log.debug("conference does not exist");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(!conference.isEventPublicState() && !conference.getUsername().equals(username)){
            Person person = personRepository.findByEventIdAndUsername(eventId,username);
            if(person==null || person.getState()!= Person.STATE.ATTEND ){
                log.debug("user  not authorized");;
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        Query query = new Query(Criteria.where("eventId").is(eventId).and("contentType").regex("^image"));
        List<FileModel> fileModels = mongoTemplate.find(query,FileModel.class);
        if(fileModels==null)fileModels = Collections.emptyList();
        return ResponseEntity.ok(fileModels);
    }

    @Override
    public ResponseEntity<List<FileModel>> others(String eventId, String username) {

        log.debug("received photo request for {} by {}",eventId,username);

        if(!UtilsClass.isStringNotNullOrEmpty(eventId,username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);

        if(conference==null){
            log.debug("conference does not exist");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(!conference.isEventPublicState() && !conference.getUsername().equals(username)){
            Person person = personRepository.findByEventIdAndUsername(eventId,username);;
            if(person==null || person.getState()!= Person.STATE.ATTEND ){

                log.debug("user  not authorized");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        Query query = new Query(Criteria.where("eventId").is(eventId).norOperator(Criteria.where("contentType").regex("^image")));
        List<FileModel> fileModels = mongoTemplate.find(query,FileModel.class);
        if(fileModels==null)fileModels = Collections.emptyList();
        return ResponseEntity.ok(fileModels);

    }
}
