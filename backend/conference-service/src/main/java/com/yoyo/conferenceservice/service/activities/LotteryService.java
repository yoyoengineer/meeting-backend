package com.yoyo.conferenceservice.service.activities;

import com.kanz.conferenceservice.model.mongodb.Lottery;
import com.kanz.conferenceservice.model.mongodb.LotteryPlayer;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface LotteryService {

    ResponseEntity<Map<String,String>> newLottery(Lottery lottery, String username);
    ResponseEntity<List<LotteryPlayer>> results(String eventId,String username);
    ResponseEntity<Map<String,String>> play(String eventId,String username);
    ResponseEntity<?> stopLottery(String eventId,String username);
    ResponseEntity<InputStreamResource> lotteryQRCode(String eventId, String username);
}
