package com.yoyo.authenticationservice.event;


import com.yoyo.authenticationservice.event.messagedriven.NewUserEventDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class SimpleApplicationEventHandler {

   private NewUserEventDispatcher newUserEventDispatcher;

    @Autowired
    public SimpleApplicationEventHandler(NewUserEventDispatcher newUserEventDispatcher){
       this.newUserEventDispatcher = newUserEventDispatcher;
    }



    @EventListener
    public void handleNewUserEvent(NewUserEvent newUserEvent){
        if(newUserEvent.isValid()){
            log.debug("Sending conference in event {}",newUserEvent);
            newUserEventDispatcher.publishEvent(newUserEvent);
        }
    }

}
