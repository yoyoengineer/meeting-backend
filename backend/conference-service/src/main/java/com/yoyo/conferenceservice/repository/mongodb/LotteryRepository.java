package com.yoyo.conferenceservice.repository.mongodb;

import com.kanz.conferenceservice.model.mongodb.Lottery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LotteryRepository extends MongoRepository<Lottery,String> {

    Lottery findByEventId(String eventId);
}
