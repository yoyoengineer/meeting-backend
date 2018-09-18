package com.binary.profileservice.service;

import com.binary.profileservice.model.ModificationParcel;
import com.binary.profileservice.model.neo4j.Person;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface PersonService {

    ResponseEntity<?> beFriend(String username1, String username2);
    ResponseEntity<?> newUser(Person person);
    ResponseEntity<?> edit(ModificationParcel modificationParcel, String username);
    ResponseEntity<?> attend(String username,String eventId);
    ResponseEntity<?> participate(String username,String eventId);
    ResponseEntity<?> likes(String username,String eventId);
    ResponseEntity<Map<String,Object>> liked(String username, String eventId);
    ResponseEntity<Person> profile(String username, String me);
    ResponseEntity<?> uploadPicture(MultipartFile file, String username);
    ResponseEntity<InputStreamResource> downloadPicture(String photoId);
    ResponseEntity<Map<String,String>> myname(String username);
    ResponseEntity<InputStreamResource> myPicture(String username);

    ResponseEntity<List<Map<String,Object>>> myFriends(String username);
    ResponseEntity<List<Map<String,Object>>> recommendFriendFromMutualFriends(String username);
    ResponseEntity<List<Map<String,Object>>> recommendFriendFromConferenceOrganizer(String username);
    ResponseEntity<List<Map<String,Object>>> recommendFriendBySameHomeTown(String username);
    ResponseEntity<List<Map<String,Object>>> recommendFriendBySameCurrentTown(String username);
    ResponseEntity<List<Map<String,Object>>> recommendFriendFromConferencesAttendedTogether(String username);


}
