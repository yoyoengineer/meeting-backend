package com.yoyo.authenticationservice.repository;

import com.yoyo.authenticationservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findByUsernameOrEmail(String parameter);
    Optional<User> findByUsername(String email);
}
