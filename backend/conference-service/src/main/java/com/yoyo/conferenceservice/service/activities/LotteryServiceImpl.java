package com.yoyo.conferenceservice.service.activities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanz.conferenceservice.event.model.ActivityEvent;
import com.kanz.conferenceservice.model.mongodb.Conference;
import com.kanz.conferenceservice.model.mongodb.Lottery;
import com.kanz.conferenceservice.model.mongodb.LotteryPlayer;
import com.kanz.conferenceservice.model.mongodb.Person;
import com.kanz.conferenceservice.repository.mongodb.ConferenceRepository;
import com.kanz.conferenceservice.repository.mongodb.LotteryPlayerRepository;
import com.kanz.conferenceservice.repository.mongodb.LotteryRepository;
import com.kanz.conferenceservice.repository.mongodb.PersonRepository;
import com.kanz.conferenceservice.service.QRCodeGenerator;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.yoyo.conferenceservice.repository.mongodb.ConferenceRepository;
import com.yoyo.conferenceservice.repository.mongodb.LotteryPlayerRepository;
import com.yoyo.conferenceservice.repository.mongodb.LotteryRepository;
import com.yoyo.conferenceservice.repository.mongodb.PersonRepository;
import com.yoyo.conferenceservice.service.QRCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class LotteryServiceImpl implements LotteryService{

    private LotteryRepository lotteryRepository;
    private ConferenceRepository conferenceRepository;
    private final String MESSAGE = "message";
    private QRCodeGenerator qrCodeGenerator;
    private GridFsTemplate gridFsTemplate;
    private PersonRepository personRepository;
    private LotteryPlayerRepository lotteryPlayerRepository;
    private MongoTemplate mongoTemplate;
    private ObjectMapper objectMapper;
    private ApplicationEventPublisher applicationEventPublisher;
    private StringRedisTemplate stringRedisTemplate;
    private Random random;


    @Autowired
    public LotteryServiceImpl(LotteryRepository lotteryRepository,
                              ConferenceRepository conferenceRepository,
                              GridFsTemplate gridFsTemplate,
                              QRCodeGenerator qrCodeGenerator,
                              StringRedisTemplate stringRedisTemplate,
                              PersonRepository personRepository,
                              ApplicationEventPublisher applicationEventPublisher,
                              LotteryPlayerRepository lotteryPlayerRepository,
                              MongoTemplate mongoTemplate,
                              ObjectMapper objectMapper){
        this.lotteryRepository = lotteryRepository;
        this.conferenceRepository = conferenceRepository;
        this.gridFsTemplate = gridFsTemplate;
        this.qrCodeGenerator = qrCodeGenerator;
        this.personRepository = personRepository;
        this.lotteryPlayerRepository = lotteryPlayerRepository;
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
        this.applicationEventPublisher = applicationEventPublisher;
        this.stringRedisTemplate = stringRedisTemplate;
        this.random = new Random();
    }


    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> newLottery(Lottery newLottery, String username) {
        log.debug("Request received lottery {} for {}",newLottery,username);
        Map<String,String> map = new HashMap<>();

        if(newLottery!=null && newLottery.isValid()){

            String eventId = newLottery.getEventId();
            Conference conference = conferenceRepository.findOne(eventId);
            if(conference==null) {
                log.debug("conference does not exists");
                map.put(MESSAGE,"conference does not exists");
                return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
            }

            if(!conference.getUsername().equals(username)){
                map.put(MESSAGE,"user not authorized");
                return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
            }

            Lottery lottery = lotteryRepository.findByEventId(eventId);

            if(lottery!=null){
                map.put(MESSAGE,"There is another game running");
                return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
            }

            try {

                // Remove old record.....

                Query query = new Query(Criteria.where("eventId").is(eventId));
                mongoTemplate.remove(query,LotteryPlayer.class);


                lottery = lotteryRepository.save(newLottery);

                ByteArrayOutputStream ouputStream = qrCodeGenerator.generateQRcode(eventId);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(ouputStream.toByteArray());
                DBObject metaData = new BasicDBObject();
                metaData.put("lotteryId", lottery.getId());
                GridFSFile file = gridFsTemplate.store(inputStream,
                        UtilsClass.generateRandomTitle(new Random(), 10)+".png",
                        "image/png",
                        metaData);


                lottery.setQrCodeId(file.getId().toString());
                lotteryRepository.save(lottery);

                log.debug("Done");
                map.put(MESSAGE,"Done");

                String contentString = null;

                try {
                    contentString = objectMapper.writeValueAsString(lottery);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                ActivityEvent activityEvent =
                        new ActivityEvent(this,
                                contentString,
                                eventId,ActivityEvent.Type.LOTTERY);

                if(conference.isEventPublicState()){

                    activityEvent.setToAll(true);
                }
                else{
                    activityEvent.setUsernames(Collections.singletonList(conference.getUsername()));
                }

                log.debug("About to publish the activity event {}",activityEvent);
                applicationEventPublisher.publishEvent(activityEvent);
                return ResponseEntity.ok(map);

            } catch (IOException e) {
                e.printStackTrace();
                if(lottery!=null && lottery.getId()!=null &&!lottery.getId().isEmpty()){
                    lotteryRepository.delete(lottery);
                }

                map.put(MESSAGE,"Failed");
                return new ResponseEntity<>(map,HttpStatus.INTERNAL_SERVER_ERROR);
            }


        }

        map.put(MESSAGE,"Something is missing");
        return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<LotteryPlayer>> results(String eventId, String username) {
        log.debug("Request result for eventId {} by {}",eventId,username);

        Conference conference = conferenceRepository.findOne(eventId);
        if(conference==null) {
            log.debug("conference does not exists");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        if(!conference.isEventPublicState() && !conference.getUsername().equals(username)){
            Person person = personRepository.findByEventIdAndUsername(eventId,username);

            if(person==null || person.getState()!= Person.STATE.ATTEND){
                log.debug("user not authorized");;
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        Query query = new Query(Criteria.where("eventId").is(eventId).and("status").is(LotteryPlayer.Status.WIN));
        List<LotteryPlayer> players = mongoTemplate.find(query,LotteryPlayer.class);

        if(players==null){
            players=Collections.emptyList();
        }

        return ResponseEntity.ok(players);



    }

    @Override
    public ResponseEntity<Map<String, String>> play(String eventId, String username) {

        log.debug("Request received play lottery in eventId {} by {}", eventId, username);
        Map<String, String> reason = new HashMap<>();

        if (!UtilsClass.isStringNotNullOrEmpty(eventId, username)) {
            log.debug("Something is missing");
            reason.put(MESSAGE, "Something is missing");
            return new ResponseEntity<>(reason, HttpStatus.BAD_REQUEST);
        }

        Conference conference = conferenceRepository.findOne(eventId);

        if (conference == null) {
            log.debug("conference does not exists");
            reason.put(MESSAGE, "conference does not exists");
            return new ResponseEntity<>(reason, HttpStatus.NOT_FOUND);
        }

        Lottery lottery = lotteryRepository.findByEventId(eventId);

        if (lottery == null) {
            reason.put(MESSAGE, "No game available");
            return new ResponseEntity<>(reason, HttpStatus.BAD_REQUEST);
        }

        if (!conference.isEventPublicState() && !conference.getUsername().equals(username)) {
            Person person = personRepository.findByEventIdAndUsername(eventId, username);

            if (person == null || person.getState() != Person.STATE.ATTEND) {
                log.debug("user not authorized");
                reason.put(MESSAGE, "user not authorized");
                return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
            }
        }

        LotteryPlayer lotteryPlayer = new LotteryPlayer();
        lotteryPlayer.setEventId(eventId);
        lotteryPlayer.setUsername(username);


        String name = stringRedisTemplate.opsForValue().get(lotteryPlayer.getUsername());
        log.debug("{} 's name is {}", lotteryPlayer.getUsername(), name);


        if (name == null)
            name = "";
        lotteryPlayer.setName(name);


        try {
            lotteryPlayerRepository.insert(lotteryPlayer);
        } catch (Exception e) {
            log.debug("played already");
            reason.put(MESSAGE, "PLAYED ALREADY");
            return new ResponseEntity<>(reason, HttpStatus.BAD_REQUEST);
        }


        int lotteryItemSize = lottery.getItems().size();
        int randomResult = random.nextInt(lotteryItemSize);
        Map<String, Integer> lotteryItems = lottery.getItems();
        List<String> itemNames = new ArrayList<>();
        for (Map.Entry<String, Integer> entity : lotteryItems.entrySet()) {
            itemNames.add(entity.getKey());
        }

        System.out.println(randomResult);
        String itemName = null;

        if (randomResult < itemNames.size()) {
            itemName = itemNames.get(randomResult);


            Query query = new Query(Criteria.where("_id").is(lottery.getId()).and("items." + itemName).gt(0));
            Update update = new Update().inc("items." + itemName, -1);
            boolean updated = mongoTemplate.updateFirst(query, update, Lottery.class).isUpdateOfExisting();

            if (updated) {
                lotteryPlayer.setStatus(LotteryPlayer.Status.WIN);
                lotteryPlayer.setPrize(itemName);
                lotteryPlayerRepository.save(lotteryPlayer);
                log.debug("Win: {}", itemName);
                reason.put(MESSAGE, "WIN " + itemName);


                String contentString = null;

                try {
                    contentString = objectMapper.writeValueAsString(lotteryPlayer);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                ActivityEvent activityEvent =
                        new ActivityEvent(this,
                                contentString,
                                eventId, ActivityEvent.Type.LOTTERY_WIN);

                if (conference.isEventPublicState()) {

                    activityEvent.setToAll(true);
                } else {
                    activityEvent.setUsernames(Collections.singletonList(conference.getUsername()));
                }

                log.debug("About to publish the activity event {}", activityEvent);
                applicationEventPublisher.publishEvent(activityEvent);
            }

            else{
                log.debug("Lose");
                reason.put(MESSAGE, "Lose");


            }

            return ResponseEntity.ok(reason);
        }


        log.debug("Lose");
        reason.put(MESSAGE, "Lose");

        return ResponseEntity.ok(reason);

    }

    @Override
    public ResponseEntity<?> stopLottery(String eventId, String username) {
        Map<String,String> reason = new HashMap<>();

        if(!UtilsClass.isStringNotNullOrEmpty(eventId,username)){
            log.debug("Something is missing");
            reason.put(MESSAGE,"Something is missing");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }


        Conference conference = conferenceRepository.findOne(eventId);
        if(conference==null) {
            log.debug("conference does not exists");
            reason.put(MESSAGE,"conference does not exists");
            return new ResponseEntity<>(reason,HttpStatus.NOT_FOUND);
        }

        if(!conference.getUsername().equals(username)){
            reason.put(MESSAGE,"user not authorized");
            return new ResponseEntity<>(reason,HttpStatus.BAD_REQUEST);
        }

        Lottery lottery = lotteryRepository.findByEventId(eventId);

        if(lottery!=null){
            lotteryRepository.delete(lottery);
            String qrCodeId = lottery.getQrCodeId();
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(qrCodeId)));

        }
        reason.put(MESSAGE,"Done");
        return ResponseEntity.ok(reason);
    }

    @Override
    public ResponseEntity<InputStreamResource> lotteryQRCode(String eventId, String username) {

        if(!UtilsClass.isStringNotNullOrEmpty(eventId,username)){
            log.debug("Something is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Lottery lottery = lotteryRepository.findByEventId(eventId);

        if(lottery==null){
            log.debug("Lottery not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String qrCodeId = lottery.getQrCodeId();
        GridFSDBFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(qrCodeId)));
        if(file==null){
            log.debug("File not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        log.debug("Sending the file");
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(file.getContentType()))
                .contentLength(file.getLength())
                .body(new InputStreamResource(file.getInputStream()));
    }

}
