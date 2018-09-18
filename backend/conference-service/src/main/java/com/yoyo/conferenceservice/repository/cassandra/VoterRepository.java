package com.yoyo.conferenceservice.repository.cassandra;

import com.kanz.conferenceservice.model.cassandra.Voter;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface VoterRepository extends CassandraRepository<Voter> {
}
