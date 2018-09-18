package com.binary.profileservice.repository;

import com.binary.profileservice.model.neo4j.City;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface CityRepository extends GraphRepository<City> {
    City findByName(String name);
}
