package com.yoyo.conferenceservice.repository.cassandra;

import com.kanz.conferenceservice.model.cassandra.Announcement;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnnouncementRepository  extends CassandraRepository<Announcement> {

    Optional<List<Announcement>> findByEventId(String eventId);

    @Query("SELECT * FROM announcement  where eventId = :eventId AND uuidtime < :uuidtime  ORDER BY uuidtime DESC LIMIT 10")
    List<Announcement> findByEventIdBeforeTime(@Param("eventId")String eventId, @Param("uuidtime") UUID time);
}
