package com.yoyo.conferenceservice.controller;

import com.kanz.conferenceservice.model.Choice;
import com.kanz.conferenceservice.model.mongodb.Bulletin;
import com.kanz.conferenceservice.service.activities.VoteService;
import com.yoyo.conferenceservice.model.mongodb.Bulletin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class VoteController {

    private VoteService voteService;

    public VoteController(VoteService voteService){
        this.voteService = voteService;
    }

    @PostMapping("/event/vote/create")
    public ResponseEntity<?> create(@RequestBody Bulletin bulletin, Principal user){
        String username = user.getName();
        return voteService.newVote(bulletin,username);
    }

    @PostMapping("/event/vote")
    public ResponseEntity<Bulletin> vote(@RequestBody Choice choice, Principal user){
        String username = user.getName();
        return voteService.vote(choice,username);
    }

    @GetMapping("/event/votes/{eventId}")
    public ResponseEntity<List<Bulletin>> getBulletins(@PathVariable("eventId")String eventId, Principal user){
        return getBulletins(eventId,"",user);
    }

    @GetMapping("/event/votes/{eventId}/{bulletinId}")
    public ResponseEntity<List<Bulletin>> getBulletins(@PathVariable("eventId")String eventId, @PathVariable("bulletinId")String bulletinId, Principal user){

        return voteService.bulletinsGet(eventId,bulletinId,user.getName());
    }

    @GetMapping("/event/bulletin/{bulletinId}")
    public ResponseEntity<Bulletin> getBulletin(@PathVariable("bulletinId")String bulletinId,Principal user){
        return voteService.bulletinGet(bulletinId,user.getName());
    }


    /////////////////////////////////////////////////////////////////
    ///////// HOW ABOUT STOP THE VOTE /////////////////////////////
    ///////////////////////////////////////////////////////////////
}
