package com.yoyo.conferenceservice.controller;

import com.kanz.conferenceservice.model.mongodb.Person;
import com.kanz.conferenceservice.service.characters.AttendanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Slf4j
public class AttendanceController {


    private AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService){
        this.attendanceService = attendanceService;
    }

    @PostMapping("/event/participate/{eventId}")
    public ResponseEntity<?> participate(@PathVariable("eventId")String eventId,Principal user){
        String username = user.getName();
        log.debug("received request for participation to {} by {}",eventId,username);
        return  attendanceService.participate(eventId,username);
    }

    @GetMapping("/event/participants/count/{eventId}")
    public ResponseEntity<?> countParticipants(@PathVariable("eventId")String eventId){
        return attendanceService.countPersons(eventId, Person.STATE.PARTICIPATE);
    }

    @PostMapping("/event/disengage/{eventId}")
    public ResponseEntity<?> disengageParticipant(@PathVariable("eventId")String eventId,Principal user){
        String username = user.getName();
        return attendanceService.deleteParticipant(eventId,username);
    }

    @PostMapping("/event/disengage/{eventId}/{participant:.+}")
    public ResponseEntity<?> disengageParticipant(@PathVariable("eventId")String eventId,
                                                  @PathVariable("participant")String username,
                                                  Principal user){
        String owner = user.getName();
        return attendanceService.deleteParticipant(eventId,username,owner);
    }

    @GetMapping("/event/attendees/count/{eventId}")
    public ResponseEntity<?> countAttendess(@PathVariable("eventId")String eventId){
        return attendanceService.countPersons(eventId, Person.STATE.ATTEND);
    }


    // This comes from a user when scanning a qrcode when the conference is public
    @PostMapping("/event/attend/{eventId}")
    public ResponseEntity<?> attend(@PathVariable("eventId") String eventId, Principal user){
        String username = user.getName();

        return attendanceService.attend(eventId,username);
    }

    // This comes from the administrator when he scans qrcode from attendees when the conference is private
    @PostMapping("/event/attend/{eventId}/{username:.+}")
    public ResponseEntity<?> attend(@PathVariable("eventId")String eventId,@PathVariable("username")String username,Principal user){
        return attendanceService.attend(eventId,username,user.getName());
    }


    @GetMapping("/event/attendance/{eventId}/qrcode")
    public ResponseEntity<InputStreamResource> userQRCode(@PathVariable("eventId")String eventId, Principal user){
        return attendanceService.userQRCode(eventId, user.getName());
    }
}
