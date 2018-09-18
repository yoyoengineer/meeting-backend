package com.yoyo.conferenceservice.repository.mongodb;


import com.kanz.conferenceservice.model.mongodb.LotteryPlayer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LotteryPlayerRepository extends MongoRepository<LotteryPlayer,String>{

    List<LotteryPlayer> findByEventId(String eventId);
}
