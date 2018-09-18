package com.yoyo.chatservice.repository.cassandra;

import com.yoyo.chatservice.model.cassandra.InstantMessage;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.UUID;


public interface ChatRepository extends CassandraRepository<InstantMessage> {
    @Query("SELECT * FROM messages  where historyId = :historyId AND uuidtime < :uuidtime  ORDER BY uuidtime")
    List<InstantMessage> findTop10ByUsernameLessThanTime(@Param("historyId") String historyId, @Param("uuidtime") UUID uuidtime);
}
