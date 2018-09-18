package com.binary.profileservice.controller;


import com.binary.profileservice.model.FriendRequest;
import com.binary.profileservice.model.ModificationParcel;
import com.binary.profileservice.model.neo4j.*;
import com.binary.profileservice.repository.*;
import com.binary.profileservice.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@Slf4j
public class PersonController {


    private PersonService personService;

    @Autowired
    public PersonController(PersonService personService){
        this.personService = personService;

    }


    @PostMapping("/profile/befriend/{newFriend:.+}")
    public ResponseEntity<?> beFriend(@PathVariable("newFriend")String newFriend, Principal user){
      return personService.beFriend(user.getName(),newFriend);
    }

    /** To be implemented **/
    ///////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////
    @PostMapping("/profile/friendrequest/{to:.+}")
    public ResponseEntity<?> beFriend(@PathVariable("to")String to){
        return null;
    }

    @GetMapping("/profile/friendrequests")


    public ResponseEntity<List<FriendRequest>> myFriendRequests(Principal user) {
        return null;
    }
    //////////////////////////////////////////////
    ///////////////////////////////////////////////


    @PostMapping("/profile/update")
    public ResponseEntity<?> updateField(@RequestBody ModificationParcel modificationParcel, Principal user){
        return personService.edit(modificationParcel,user.getName());
    }



    @PostMapping("/profile/likes/{eventId}")
    public ResponseEntity<?> like(@PathVariable("eventId")String eventId, Principal user){
        log.debug("Likes a pictures");
        return personService.likes(user.getName(),eventId);
    }

    @GetMapping("/profile/liked/{eventId}")
    public ResponseEntity<Map<String,Object>> liked(@PathVariable("eventId")String eventId, Principal user){
        return personService.liked(user.getName(),eventId);
    }
    @GetMapping("profile/user/{username}")
    public ResponseEntity<Person> getUser(@PathVariable String username, Principal user){
        return personService.profile(username,user.getName());
    }

    @GetMapping("profile/user")
    public ResponseEntity<Person> getUser(Principal user){
        return getUser(user.getName(),user);
    }




    @GetMapping("/profile/myfriends")
    public ResponseEntity<List<Map<String,Object>>> myFriends(Principal user){
        return personService.myFriends(user.getName());
    }

    @GetMapping("/profile/myname")
    public ResponseEntity<Map<String,String>> myName(Principal user){
        return personService.myname(user.getName());
    }

    @PostMapping("/profile/picture/upload")
    public ResponseEntity<?> uploadPicture(@RequestParam("file")MultipartFile file,Principal user){
            return personService.uploadPicture(file,user.getName());
    }

    @GetMapping("/profile/picture/{photoId}")
    public ResponseEntity<InputStreamResource> downloadPhoto(@PathVariable("photoId")String photoId){
        return personService.downloadPicture(photoId);
    }

    @GetMapping("/profile/mypicture/{username}")
    public ResponseEntity<InputStreamResource> myPicture(@PathVariable("username")String username){
        return personService.myPicture(username);
    }

    @GetMapping("/profile/mypicture")
    public ResponseEntity<InputStreamResource> myPicture(Principal user){
        return myPicture(user.getName());
    }

    // RECOMMENDATIONS

    @GetMapping("profile/fof")
    public ResponseEntity<List<Map<String,Object>>> getFriendsByFof(Principal user){
        return personService.recommendFriendFromMutualFriends(user.getName());
    }

    @GetMapping("profile/fco")
    public ResponseEntity<List<Map<String,Object>>> getFriendsByOrg(Principal user) {

        return personService.recommendFriendFromConferenceOrganizer(user.getName());
    }

    @GetMapping("/profile/fsht")
    public ResponseEntity<List<Map<String,Object>>> getFromSameHomeTown(Principal user){
        return personService.recommendFriendBySameHomeTown(user.getName());
    }

    @GetMapping("/profile/fsct")
    public ResponseEntity<List<Map<String,Object>>> getFromCurrentHomeTown(Principal user){
        return personService.recommendFriendBySameCurrentTown(user.getName());
    }


    @GetMapping("/profile/fcat")
    public ResponseEntity<List<Map<String,Object>>> getFromConfAttendedTogether(Principal user){
        return personService.recommendFriendFromConferencesAttendedTogether(user.getName());
    }



}

