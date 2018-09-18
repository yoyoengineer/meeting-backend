package com.binary.profileservice.repository;

import com.binary.profileservice.model.neo4j.School;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface SchoolRepository extends GraphRepository<School> {
}
