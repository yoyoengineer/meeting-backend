package com.binary.profileservice.service;

import com.binary.profileservice.model.ModificationParcel;
import com.binary.profileservice.model.neo4j.City;
import com.binary.profileservice.model.neo4j.Company;
import com.binary.profileservice.model.neo4j.Person;
import com.binary.profileservice.model.neo4j.School;
import com.binary.profileservice.repository.PersonRepository;
import com.binary.profileservice.ustils.UtilsClass;
import com.mongodb.gridfs.GridFSDBFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class PersonServiceImpl implements PersonService {

    private PersonRepository personRepository;
    private final String MESSAGE = "message";
    private StringRedisTemplate stringRedisTemplate;
    private GridFsTemplate gridFsTemplate;


    @Autowired
    public PersonServiceImpl(PersonRepository personRepository,
                             StringRedisTemplate stringRedisTemplate,
                             GridFsTemplate gridFsTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
        this.personRepository = personRepository;
        this.gridFsTemplate = gridFsTemplate;
    }

    @Override
    @Transactional
    public ResponseEntity<?> beFriend(String username1, String username2) {
        Map<String,String> reason = new HashMap<>();

        log.debug("received friend acceptance from {} to {}",username1,username2);
        if(!UtilsClass.isStringNotNullOrEmpty(username1,username2)){
            log.debug("Something is missing");
            reason.put(MESSAGE,"Something is missing");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }

        Person person = personRepository.beFriend(username1,username2);

        if(person.getUsername() == null){
            log.debug("Not Found");
            reason.put(MESSAGE,"Not Found");
            return new ResponseEntity<>(reason,HttpStatus.NOT_FOUND);
        }

        /** Make sure to erase the friend request record from the database **/
        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<?> newUser(Person person) {
        Map<String,String> reason = new HashMap<>();
        log.debug("received person {}",person);

        if(person!=null && !person.isValid()){
            reason.put(MESSAGE,"Something is missing");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }
        person = personRepository.save(person);
        return person==null? new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR):ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<?> edit(ModificationParcel modificationParcel, String username) {
        Map<String,String> reason = new HashMap<>();

        if(!UtilsClass.isStringNotNullOrEmpty(username) || modificationParcel==null || !modificationParcel.isValid()){
            log.debug("Something is missing");
            reason.put(MESSAGE,"Something is missing");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }

        String fieldName = modificationParcel.getFieldName();
        String fieldValue = modificationParcel.getFieldValue();

        Person person;

        switch(fieldName) {
            case "name":
                person = personRepository.findByUsername(username, 0);
                if(person==null)
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                person.setName(fieldValue);
                personRepository.save(person);
                stringRedisTemplate.opsForValue().set(username,fieldValue);
                break;

            case "aboutMe":
                person = personRepository.findByUsername(username, 0);
                if(person==null)
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                person.setAboutMe(fieldValue);
                personRepository.save(person);
                break;

            case "birthday":
                person = personRepository.findByUsername(username, 0);
                if(person==null)
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                person.setBirthday(fieldValue);
                log.debug("The person info is {} ",person);
                personRepository.save(person);
                break;

            case "photo":
                person = personRepository.findByUsername(username, 0);
                if(person==null)
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                person.setPhoto(fieldValue);
                personRepository.save(person);
                break;

            case "schoolName":
                personRepository.deleteSchool(username);
                School school = new School();
                school.setName(fieldValue);
                person = personRepository.findByUsername(username, 0);
                if(person==null)
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                person.setSchool(school);
                personRepository.save(person);
                break;

            case "companyName":
                personRepository.deleteCompany(username);
                Company company = new Company();
                company.setName(fieldValue);
                person = personRepository.findByUsername(username, 0);
                if(person==null)
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                person.setCompany(company);
                personRepository.save(person);
                break;

            case "homeTown":
                personRepository.deleteHomeTown(username);
                City home = new City();
                home.setName(fieldValue);
                person = personRepository.findByUsername(username, 0);
                if(person==null)
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                person.setHome(home);
                personRepository.save(person);
                break;

            case "currentTown":
                personRepository.deleteCurrentTown(username);
                City current = new City();
                current.setName(fieldValue);
                person = personRepository.findByUsername(username, 0);
                if(person==null)
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                person.setCurrent(current);
                personRepository.save(person);
                break;

        }

        return ResponseEntity.ok().build();

    }


    @Override
    public ResponseEntity<?> uploadPicture(MultipartFile file, String username) {
        Map<String,String> reason = new HashMap<>();
        reason.put("message","ok");
        log.debug("uploading files");
        try {

            String fileId = gridFsTemplate
                    .store(file.getInputStream(),file.getOriginalFilename(),file.getContentType())
                    .getId().toString();
            ModificationParcel modificationParcel = new ModificationParcel();
            modificationParcel.setFieldName("photo");
            modificationParcel.setFieldValue(fileId);
            edit(modificationParcel,username);


            return ResponseEntity.ok(reason);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    public ResponseEntity<InputStreamResource> downloadPicture(String photoId) {

        log.debug("Received request for download picture {}",photoId);
        if(!UtilsClass.isStringNotNullOrEmpty(photoId)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Query query = new Query(Criteria.where("_id").is(photoId));
        GridFSDBFile file = gridFsTemplate.findOne(query);

        if(file==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(file.getContentType()))
                .contentLength(file.getLength())
                .body(new InputStreamResource(file.getInputStream()));

    }

    @Override
    @Transactional
    public ResponseEntity<?> attend(String username, String eventId) {
        Map<String,String> reason = new HashMap<>();
        log.debug("request received for event attendance from {} to {}",username,eventId);

        if(!UtilsClass.isStringNotNullOrEmpty(username,eventId)){
            reason.put(MESSAGE,"Something is missing");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }

        personRepository.attend(username,eventId);
        reason.put(MESSAGE,"Done!");
        return ResponseEntity.ok().body(reason);
    }

    @Override
    @Transactional
    public ResponseEntity<?> participate(String username, String eventId) {
        Map<String,String> reason = new HashMap<>();
        log.debug("request received for event participation from {} to {}",username,eventId);

        if(!UtilsClass.isStringNotNullOrEmpty(username,eventId)){
            reason.put(MESSAGE,"Something is missing");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }


        personRepository.participate(username,eventId);
        reason.put(MESSAGE,"Done!");
        return ResponseEntity.ok().body(reason);
    }

    @Override
    @Transactional
    public ResponseEntity<?> likes(String username, String eventId) {
        Map<String,String> reason = new HashMap<>();
        log.debug("Received a like for {} by {}",eventId,username);

        if(!UtilsClass.isStringNotNullOrEmpty(username,eventId)){
            log.debug("Something is missing");
            reason.put(MESSAGE,"Something is missing");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }
        personRepository.likes(username,eventId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Person> profile(String username, String me) {
        if(!UtilsClass.isStringNotNullOrEmpty(username,me)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Person person = personRepository.findByUsername(username,me);

        if(person==null){
            log.debug("User not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(username.equals(me))
            person.setAreFriend(true);

        return ResponseEntity.ok(person);
    }

    @Override
    public ResponseEntity<Map<String, Object>> liked(String username, String eventId) {
        Map<String,Object> response = new HashMap<>();
        response.put("liked",false);

        log.debug("Received a like for {} by {}",eventId,username);

        if(!UtilsClass.isStringNotNullOrEmpty(username,eventId)){
            log.debug("Something is missing");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
        List<Map<String,Object>> list = personRepository.liked(username,eventId);
        if(list!=null && !list.isEmpty())
            response = list.get(0);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, String>> myname(String username) {
        Map<String,String> map = new HashMap<>();
        map.put("name","");
        if(UtilsClass.isStringNotNullOrEmpty(username)){
            Person person = personRepository.findByUsername(username,0);

            if(person!=null && !person.getUsername().isEmpty()){
                map.put("name",person.getName());
            }
        }

        return ResponseEntity.ok(map);
    }

    @Override
    public ResponseEntity<InputStreamResource> myPicture(String username) {
        log.debug("Request to get the picture");
        if(!UtilsClass.isStringNotNullOrEmpty(username)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

        Person person = personRepository.findByUsername(username,0);
        log.debug("person found is {}", person);
        if(person!=null && person.getPhoto()!=null){
            log.debug("About to load the picture");
            return downloadPicture(person.getPhoto());
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> myFriends(String username) {

        log.debug("Received request myFriends by user {}",username);
        if(!UtilsClass.isStringNotNullOrEmpty(username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Map<String,Object>> friends = personRepository.myFriends(username);
        return ResponseEntity.ok(friends);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> recommendFriendFromMutualFriends(String username) {
        log.debug("Received request for mutual friends recommendation user {}",username);

        if(!UtilsClass.isStringNotNullOrEmpty(username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Map<String,Object>> recommendFriends = personRepository.recommendFriendFromMutualFriends(username);
        return ResponseEntity.ok(recommendFriends);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> recommendFriendFromConferenceOrganizer(String username) {
        log.debug("Received request for conference organizer friends recommendation user {}",username);

        if(!UtilsClass.isStringNotNullOrEmpty(username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Map<String,Object>> recommendedFriends = personRepository.recommendFriendFromConferenceOrganizer(username);
        return ResponseEntity.ok(recommendedFriends);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> recommendFriendBySameHomeTown(String username) {
        log.debug("Received request for same home town friends recommendation user {}",username);

        if(!UtilsClass.isStringNotNullOrEmpty(username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Map<String,Object>> recommendedFriends = personRepository.recommendFriendBySameHomeTown(username);
        return ResponseEntity.ok(recommendedFriends);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> recommendFriendBySameCurrentTown(String username) {
        log.debug("Received request for same current town friends recommendation user {}",username);

        if(!UtilsClass.isStringNotNullOrEmpty(username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Map<String,Object>> recommendedFriends = personRepository.recommendFriendBySameCurrentTown(username);
        return ResponseEntity.ok(recommendedFriends);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> recommendFriendFromConferencesAttendedTogether(String username) {
        log.debug("Received request for conferences attended together friends recommendation user {}",username);

        if(!UtilsClass.isStringNotNullOrEmpty(username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Map<String,Object>> recommendedFriends = personRepository.recommendFriendFromConferencesAttendedTogether(username);
        return ResponseEntity.ok(recommendedFriends);
    }
}
