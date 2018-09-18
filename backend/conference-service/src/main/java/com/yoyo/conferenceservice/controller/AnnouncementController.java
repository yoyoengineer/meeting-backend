package com.yoyo.conferenceservice.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.kanz.conferenceservice.model.cassandra.Announcement;
import com.kanz.conferenceservice.service.activities.AnnouncementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
public class AnnouncementController {

    private AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService){
        this.announcementService = announcementService;
    }

    @PostMapping("/event/announcement")
    public ResponseEntity<?> newAnnouncement(@RequestBody Announcement announcement, Principal user){
        String owner = user.getName();
        return announcementService.newAnnouncement(announcement,owner);
    }


    @GetMapping("/event/announcements/{eventId}")
    public ResponseEntity<List<Announcement>> myAnnouncements(@PathVariable("eventId") String eventId,
                                                              Principal user){
        String username = user.getName();

        return announcementService.getAnnouncements(eventId,username, UUIDs.timeBased());
    }


    @GetMapping("/event/announcements/{eventId}/{UUIDtime}")
    public ResponseEntity<List<Announcement>> myAnnouncements(@PathVariable("eventId")String eventId, @PathVariable("UUIDtime")UUID UUIDtime, Principal user){
        String username = user.getName();

        return announcementService.getAnnouncements(eventId,username,UUIDtime);
    }

    @PostMapping("/event/announcement/delete/{eventId}/{UUIDtime}")
    public ResponseEntity deleteAnnouncement(@PathVariable("eventId")String eventId, @PathVariable("UUIDtime") UUID UUIDtime, Principal user){
        String owner = user.getName();
        return announcementService.deleteAnnouncement(eventId,UUIDtime,owner);
    }
}
