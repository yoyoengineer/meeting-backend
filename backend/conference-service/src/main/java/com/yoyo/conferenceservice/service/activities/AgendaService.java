package com.yoyo.conferenceservice.service.activities;

import com.kanz.conferenceservice.model.mongodb.Agenda;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface AgendaService {
    ResponseEntity<List<Agenda>> getAgenda(String eventId, String username, UUID uuid);

    ResponseEntity<?> newAgenda(Agenda agenda, String owner);
}
