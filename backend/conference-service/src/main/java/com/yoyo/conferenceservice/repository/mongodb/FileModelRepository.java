package com.yoyo.conferenceservice.repository.mongodb;

import com.kanz.conferenceservice.model.mongodb.FileModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileModelRepository extends MongoRepository<FileModel,String>{
    List<FileModel> findByEventIdOrderByTimeDesc(String eventId);
    FileModel findFirstByFileId(String fileId);
    void deleteByFileId(String fileId);
}