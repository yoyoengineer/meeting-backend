package com.yoyo.chatservice.repository.mongo;

import com.yoyo.chatservice.model.mongo.History;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HistoryRepository extends MongoRepository<History,String> {


    List<History> findByUsernameOrderByTimeDesc(String username);
}
