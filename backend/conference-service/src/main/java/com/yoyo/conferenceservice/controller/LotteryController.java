package com.yoyo.conferenceservice.controller;

import com.kanz.conferenceservice.model.mongodb.Lottery;
import com.kanz.conferenceservice.model.mongodb.LotteryPlayer;
import com.kanz.conferenceservice.service.activities.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
public class LotteryController {

    private LotteryService lotteryService;

    @Autowired
    public LotteryController(LotteryService lotteryService){
        this.lotteryService = lotteryService;
    }

    @PostMapping("/event/lottery")
    public ResponseEntity<Map<String,String>> newLottery(@RequestBody Lottery lottery, Principal user){
        return lotteryService.newLottery(lottery,user.getName());
    }

    @GetMapping("/event/lottery/{eventId}")
    public ResponseEntity<List<LotteryPlayer>> results(@PathVariable("eventId")String eventId, Principal user){
        return lotteryService.results(eventId,user.getName());
    }

    @PostMapping("/event/lottery/play/{eventId}")
    public ResponseEntity<Map<String,String>> play(@PathVariable("eventId")String eventId,Principal user){
        return lotteryService.play(eventId,user.getName());
    }

    @PostMapping("/event/lottery/stop/{eventId}")
    public ResponseEntity<?> stopLottery(@PathVariable("eventId")String eventId,Principal user){
        return lotteryService.stopLottery(eventId,user.getName());
    }

    @GetMapping("/event/lottery/qrcode/{eventId}")
    public ResponseEntity<InputStreamResource> lotteryQRCode(@PathVariable("eventId")String eventId, Principal user){
        return lotteryService.lotteryQRCode(eventId,user.getName());
    }
}
