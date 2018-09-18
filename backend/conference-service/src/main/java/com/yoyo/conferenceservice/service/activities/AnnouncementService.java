package com.yoyo.conferenceservice.service.activities;

import com.kanz.conferenceservice.model.cassandra.Announcement;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface AnnouncementService {

    ResponseEntity<?> newAnnouncement(Announcement announcement, String owner);
    ResponseEntity<List<Announcement>> getAnnouncements(String eventId, String username, UUID UUIDtime);
    ResponseEntity<?> deleteAnnouncement(String eventId, UUID UUIDtime, String owner);
}
