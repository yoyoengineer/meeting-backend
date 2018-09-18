package com.binary.profileservice.repository;

import com.binary.profileservice.model.neo4j.Company;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface CompanyRepository extends GraphRepository<Company>{
}
