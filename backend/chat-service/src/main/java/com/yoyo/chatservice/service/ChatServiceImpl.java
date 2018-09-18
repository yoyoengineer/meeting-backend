package com.yoyo.chatservice.service;

import com.yoyo.chatservice.client.RestClient;
import com.yoyo.chatservice.model.mongo.History;
import com.yoyo.chatservice.model.cassandra.InstantMessage;
import com.yoyo.chatservice.model.Parcel;
import com.yoyo.chatservice.model.ParcelForFileMessage;
import com.yoyo.chatservice.repository.cassandra.ChatRepository;
import com.yoyo.chatservice.repository.mongo.HistoryRepository;
import com.yoyo.chatservice.ustils.UtilsClass;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.cassandra.repository.MapId;
import org.springframework.data.cassandra.repository.support.BasicMapId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


@Service
@Slf4j
public class ChatServiceImpl implements ChatService {


    private RestClient restClient;
    private ChatRepository chatRepository;
    private GridFsTemplate gridFsTemplate;
    private HistoryRepository historyRepository;
    private StringRedisTemplate stringRedisTemplate;

    public ChatServiceImpl(RestClient restClient,
                           ChatRepository chatRepository,
                           GridFsTemplate gridFsTemplate,
                           HistoryRepository historyRepository,
                           StringRedisTemplate stringRedisTemplate){

        this.restClient = restClient;
        this.chatRepository = chatRepository;
        this.gridFsTemplate = gridFsTemplate;
        this.historyRepository = historyRepository;
        this.stringRedisTemplate = stringRedisTemplate;

    }

    @Override
    @Transactional
    public ResponseEntity<?> sendPlainTextMessage(History history) {

        log.debug("Received send message {}",history);

        if(history == null ||
                !UtilsClass.isStringNotNullOrEmpty(history.getText(),
                        history.getToUser(),
                        history.getFromUser()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        // Put some checking here to see if the history is a valid one.like you did with conference-service.

        Set<String> toUsers = new HashSet<>();
        toUsers.add(history.getFromUser());
        toUsers.add(history.getToUser());
        // For each one of them save a copy of the history with their respective username

        String name = stringRedisTemplate.opsForValue().get(history.getToUser());
        history.withUsername(history.getFromUser()).withName(name);
        history.setId(history.getFromUser()+history.getToUser());
       history = historyRepository.save(history
               .withTime(new Date().getTime()));
       log.debug("After saving the history {}",history);
       InstantMessage message = new InstantMessage().withUUIDtime()
                                    .withHistoryId(history.getId())
                                    .withText(history.getText())
                                    .withFromUser(history.getFromUser())
                                    .withToUser(history.getToUser())
                                    .withType(InstantMessage.Type.TEXT);
       log.debug("Message before saving {}",message);
       chatRepository.save(message);


       name = stringRedisTemplate.opsForValue().get(history.getFromUser());
       history = history.withUsername(history.getToUser()).withName(name);
       history.setId(history.getToUser()+history.getFromUser());
       history = historyRepository.save(history);
       message.withHistoryId(history.getId());
       chatRepository.save(message);

        // Put everything in a parcel and then send to the websocket service
        Parcel parcel = new Parcel(toUsers,history);
        log.debug("sending parcel {} to the websocket", parcel);

        try {
            restClient.post(chatURL, parcel);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }


    @Override
    public List<InstantMessage> findMessageForBeforeTime(String username, UUID time) {

        List<InstantMessage> messages = chatRepository.findTop10ByUsernameLessThanTime(username,time);
        log.debug("List of message to be sent {} ", messages);

        return messages;
//        return chatRepository.findTop10ByUsernameLessThanTime(username, time);

    }

    @Override
    public ResponseEntity<?> uploadFile(MultipartFile file, Map<String, String> usersInvolved) {
        long fileSize = file.getSize();
        String fileContentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        Set<String> toUsers = new HashSet<>();
        toUsers.add(usersInvolved.get(FROM_USER));
        toUsers.add(usersInvolved.get(TO_USER));

        List<Parcel> parcels = new ArrayList<>();

        try {


            // Add some meta data to be saved with the file
            DBObject metaData = new BasicDBObject();
            metaData.put(FROM_USER,usersInvolved.get(FROM_USER));
            metaData.put(TO_USER,usersInvolved.get(TO_USER));




          for(String toUser: toUsers){
              // Save it with grids
              String fileId = gridFsTemplate
                      .store(file.getInputStream(),fileName,file.getContentType(),metaData)
                      .getId().toString();
              // Put in details about the uploaded file to be saved as an instant history
              Map<String,String> extra = new HashMap<>();
              extra.put(FILE_NAME,fileName);
              extra.put(CONTENT_TYPE,fileContentType);
              extra.put(SIZE,Long.toString(fileSize));
              extra.put(FILE_ID,fileId);


              // Create an instant history object
              History history = new History()
                      .withFromUser(usersInvolved.get(FROM_USER))
                      .withToUser(usersInvolved.get(TO_USER))
                      .withType(History.Type.FILE)
                      .withTime(new Date().getTime())
                      .withExtra(extra).withUsername(toUser);

              String name = "";
              String id = "";
              if(toUser.equals(usersInvolved.get(FROM_USER))) {
                 name =  stringRedisTemplate.opsForValue().get(history.getToUser());
                  id = history.getFromUser() + history.getToUser();
              }
              else{
                 name =  stringRedisTemplate.opsForValue().get(history.getFromUser());
                  id = history.getToUser() + history.getFromUser();
              }

              history.withName(name).setId(id);
              history = historyRepository.save(history);

              InstantMessage message = new InstantMessage().withUUIDtime()
                      .withHistoryId(history.getId())
                      .withFromUser(history.getFromUser())
                      .withToUser(history.getToUser())
                      .withType(InstantMessage.Type.FILE)
                      .withExtra(extra);

              chatRepository.save(message);

              // Create a parcel for every involved user.
              parcels.add(new Parcel(new HashSet<>(Collections.singleton(toUser)),history));
          }


          ParcelForFileMessage parcelForFileMessage = new ParcelForFileMessage(parcels);
         restClient.post(chatFile,parcelForFileMessage);

         return ResponseEntity.ok().build();


        } catch (IOException e) {
            log.debug("Couldn't upload file");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<InputStreamResource> getFile(String fileId, String username) {
        // username or principal should be used later to check whehther the use is authenticated to access the file
        GridFSDBFile file = gridFsTemplate.findOne(getIdQuery(fileId));

        if(file==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(file.getContentType()))
                .contentLength(file.getLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=" + file.getFilename())
                .body(new InputStreamResource(file.getInputStream()));
    }

    @Override
    public ResponseEntity<?> deleteMessage(String username, long time) {
        // Create the id object to use against the database
        MapId mapId = BasicMapId.id("username",username).with("time", time);
        InstantMessage message = chatRepository.findOne(mapId);

        if(message!=null){

            String fileId=null;
            // If it were a file upload history find the fileId
            if(message.getType()==InstantMessage.Type.FILE){
                fileId=message.getExtra().get(FILE_ID);
            }

            log.debug("Deleting the history {} from cassandra",message);
            chatRepository.delete(mapId); // Delete the history

           if(fileId!=null && !fileId.isEmpty()) {
               log.debug("Deleting the file with id {} from mongodb", fileId);
               gridFsTemplate.delete(getIdQuery(fileId));
           }
        }

        return ResponseEntity.ok().build();
    }



    private Query getIdQuery(String fileId){
        return new Query(Criteria.where(ID).is(fileId));
    }


    @Override
    public ResponseEntity<List<History>> myHistory(String username) {
        log.debug("Received request for my chat history by {}",username);

        List<History> myHistories   = Collections.emptyList();

        if(username!=null && !username.isEmpty()){
            myHistories = historyRepository.findByUsernameOrderByTimeDesc(username);

            if(myHistories==null)
                myHistories  = Collections.emptyList();
        }

        log.debug("History to be sent back {}",myHistories);
        return ResponseEntity.ok(myHistories);
    }
}
