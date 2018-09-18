package com.binary.profileservice.service;

import com.binary.profileservice.model.neo4j.City;
import com.binary.profileservice.model.neo4j.Conference;
import com.binary.profileservice.model.neo4j.Person;
import com.binary.profileservice.repository.ConferenceRepository;
import com.binary.profileservice.repository.PersonRepository;
import com.binary.profileservice.ustils.UtilsClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ConferenceServiceImpl implements ConferenceService {

    private ConferenceRepository conferenceRepository;
    private PersonRepository personRepository;
    private final String MESSAGE = "message";

    @Autowired
    public ConferenceServiceImpl(ConferenceRepository conferenceRepository,
                                 PersonRepository personRepository){
        this.conferenceRepository = conferenceRepository;
        this.personRepository = personRepository;
    }


    @Override
    @Transactional
    public ResponseEntity<?> newConference(Conference conference) {
        Map<String,String> reason = new HashMap<>();

        log.debug("received new conference request {}",conference);
        if(!conference.isValid()){
            reason.put(MESSAGE,"Something is missing");
            return new ResponseEntity<>(reason, HttpStatus.BAD_REQUEST);

        }


        String username = conference.getUsername();
        Person person = personRepository.findByUsername(username,0);
        log.debug("person found is {}",person);
        if(person==null){
            reason.put(MESSAGE,"Something is missing");
            return new ResponseEntity<>(reason, HttpStatus.BAD_REQUEST);
        }

        City city = new City();
        city.setName(conference.getTown());
        log.debug("city found is {}",city);

        conference.organizedBy(person);
        conference.setCity(city);
        conferenceRepository.save(conference);

        reason.put(MESSAGE,"Done!");
        return ResponseEntity.ok(reason);
    }

    @Override
    public ResponseEntity<List<Map<String,Object>>> myConferences(String username, Integer page) {
        log.debug("Received request for my conferences by {}",username);

        if(!UtilsClass.isStringNotNullOrEmpty(username)|| page==null){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Map<String,Object>> myConferences = conferenceRepository.findMyConferences(username,page);
        log.debug("Conferences to be sent back {}",myConferences);
        return ResponseEntity.ok(myConferences);
    }

    @Override
    public ResponseEntity<List<Map<String,Object>>> conferencesAttended(String username, Integer page) {
        log.debug("Received request for my attended conferences by {}",username);

        if(!UtilsClass.isStringNotNullOrEmpty(username)|| page==null){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Map<String,Object>> myAttendedConferences = conferenceRepository.findAttendedConferences(username,page);
        log.debug("Conferences attended {}",myAttendedConferences);
        return ResponseEntity.ok(myAttendedConferences);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> conferenceParticipate(String username) {
        log.debug("Request for participating by {}",username);
        if(!UtilsClass.isStringNotNullOrEmpty(username)){

            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Map<String,Object>> conferences = conferenceRepository.conferenceParticipate(username);
        log.debug("Conferences participated in {}",conferences);

        return ResponseEntity.ok(conferences);
    }

    @Override
    @Transactional
    public ResponseEntity<?> edit(String eventId, String fieldName, String fieldValue) {
        Map<String,String> reason = new HashMap<>();
        Conference conference;

        if(!UtilsClass.isStringNotNullOrEmpty(eventId,fieldName,fieldValue)){
            log.debug("Something is missing");
            reason.put(MESSAGE,"Something is missing");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }


        switch(fieldName) {
            case "topic":
                conference = conferenceRepository.findByEventId(eventId,0);
                if(conference==null)
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                conference.setTopic(fieldValue);
                conferenceRepository.save(conference);
                break;

            case "description":
                conference = conferenceRepository.findByEventId(eventId,0);
                if(conference==null)
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                conference.setDescription(fieldValue);
                conferenceRepository.save(conference);
                break;

            case "time":
                conference = conferenceRepository.findByEventId(eventId,0);
                if(conference==null)
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);

                 try{
                    long time = Long.parseLong(fieldValue);
                     conference.setTime(time);
                     log.debug("The conference info is {} ",conference);
                     conferenceRepository.save(conference);
                 }
                 catch(NumberFormatException e){
                         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                 }


                break;

            case "town":
                conferenceRepository.deleteCity(eventId);
                City city = new City();
                city.setName(fieldValue);
                conference = conferenceRepository.findByEventId(eventId,0);
                if(conference==null)
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                conference.setCity(city);
                conferenceRepository.save(conference);
                break;

        }
        reason.put(MESSAGE,"Done!");
        return ResponseEntity.ok(reason);
    }


    @Override
    public ResponseEntity<List<Map<String, Object>>> conferenceLikedPeopleList(String eventId) {
        log.debug("received request for list of people who liked eventId {}",eventId);

        if(!UtilsClass.isStringNotNullOrEmpty(eventId)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Map<String,Object>> listOfPeople = conferenceRepository.conferenceLikedBy(eventId);

        log.debug("List of people who liked the conference {}",listOfPeople);
        return ResponseEntity.ok(listOfPeople);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> conferenceAttendedPeopleList(String eventId) {
        log.debug("received request for list of people who liked eventId {}",eventId);

        if(!UtilsClass.isStringNotNullOrEmpty(eventId)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Map<String,Object>> listOfPeople = conferenceRepository.conferenceAttendedBy(eventId);

        log.debug("List of people who attended the conference {} ", listOfPeople);
        return ResponseEntity.ok(listOfPeople);
    }

    @Override
    public ResponseEntity<Integer> numberOfPeopleWhoLiked(String eventId) {
        log.debug("received request for number of people who liked eventId {}",eventId);

        if(!UtilsClass.isStringNotNullOrEmpty(eventId)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Integer nbrOfPeopleWhoLiked = conferenceRepository.numberOfPeopleLiked(eventId);
        return ResponseEntity.ok(nbrOfPeopleWhoLiked);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> recommendFromMostAttendedOrganizer(String username) {
        log.debug("received request for recommendation for most attended conference for {} ", username);

        if(!UtilsClass.isStringNotNullOrEmpty(username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Map<String,Object>> recommended = conferenceRepository.recommendFromMostAttendedOrganizer(username);
        log.debug("Recommendation from most attended organizer {}",recommended);
        return ResponseEntity.ok(recommended);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> recommendFromMostPopular(String username) {
        log.debug("received request for recommendation for most popular for {} ", username);

        if(!UtilsClass.isStringNotNullOrEmpty(username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Map<String,Object>> recommended = conferenceRepository.recommendFromMostPopular(username);

        log.debug("Recommend from most popular organizer {}",recommended);
        return ResponseEntity.ok(recommended);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> userConferences(String username) {
        log.debug("received request for user conferences for most popular for {} ", username);

        if(!UtilsClass.isStringNotNullOrEmpty(username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Map<String,Object>> conferences = conferenceRepository.findMyConferences(username,0);
        log.debug("My conferences {} ", conferences);
        return ResponseEntity.ok(conferences);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> latest(String username) {
        log.debug("received request for recommendation for most popular for {} ", username);

        if(!UtilsClass.isStringNotNullOrEmpty(username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Map<String,Object>> recommended = conferenceRepository.recommendLatest(username);

        log.debug("Latest conferences {}",recommended);
        return ResponseEntity.ok(recommended);
    }


}
