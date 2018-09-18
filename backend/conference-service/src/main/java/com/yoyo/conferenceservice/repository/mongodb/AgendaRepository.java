package com.yoyo.conferenceservice.repository.mongodb;

import com.kanz.conferenceservice.model.mongodb.Agenda;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AgendaRepository extends MongoRepository<Agenda,String> {
    List<Agenda> findByEventIdOrderByIdDesc(String eventId);
}
