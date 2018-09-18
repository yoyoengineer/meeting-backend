package com.yoyo.authenticationservice.service;

import com.yoyo.authenticationservice.event.NewUserEvent;
import com.yoyo.authenticationservice.model.User;
import com.yoyo.authenticationservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {


    private UserRepository userRepository;
    private ApplicationEventPublisher applicationEventPublisher;

    public UserService(UserRepository userRepository,
                       ApplicationEventPublisher applicationEventPublisher){
        this.userRepository = userRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("username {}",username);
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()){
            User u = user.get();
            return
                    new org.springframework.security.core.userdetails.User(u.getUsername(),u.getPassword(), AuthorityUtils.createAuthorityList("USER"));
        }
        throw new UsernameNotFoundException("Username doesn't exist");
    }

    public ResponseEntity<?> register(User user){
        log.debug("received request for {}",user);
        Map<String,String>  reason = new HashMap<>();

        if(user==null || !user.isValid()){
            log.debug("no valid input");
            reason.put("message","no valid input");
            return new ResponseEntity<>(reason, HttpStatus.BAD_REQUEST);
        }

        try {
            userRepository.insert(user);
            applicationEventPublisher.publishEvent(new NewUserEvent(this,user.getUsername()));
            reason.put("message","Done");
            return ResponseEntity.ok(reason);
        }
        catch(Exception e){
            log.debug("user conflicts");
            reason.put("message","Conflicts");
            return new ResponseEntity<>(reason,HttpStatus.CONFLICT);
        }

    }
}
