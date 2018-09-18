package com.yoyo.conferenceservice.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.kanz.conferenceservice.model.mongodb.Agenda;
import com.kanz.conferenceservice.service.activities.AgendaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
public class AgendaController {

    private AgendaService agendaService;

    public AgendaController(AgendaService agendaService){
        this.agendaService = agendaService;
    }

    @PostMapping("/event/agenda")
    public ResponseEntity<?> newAnnouncement(@RequestBody Agenda agenda, Principal user){
        String owner = user.getName();
        return agendaService.newAgenda(agenda,owner);
    }


    @GetMapping("/event/agenda/{eventId}")
    public ResponseEntity<List<Agenda>> myAgendas(@PathVariable("eventId") String eventId,
                                                        Principal user){
        String username = user.getName();

        return agendaService.getAgenda(eventId,username, UUIDs.timeBased());
    }

    @GetMapping("/event/agenda/{eventId}/{UUIDtime}")
    public ResponseEntity<List<Agenda>> myAgenda(@PathVariable("eventId")String eventId, @PathVariable("UUIDtime")UUID UUIDtime, Principal user){
        String username = user.getName();

        return agendaService.getAgenda(eventId,username,UUIDtime);
    }

    /////// You can add the delete function
}
