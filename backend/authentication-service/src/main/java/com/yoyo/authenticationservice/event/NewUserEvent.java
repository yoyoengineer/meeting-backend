package com.yoyo.authenticationservice.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;
import java.util.HashMap;
import java.util.Map;

@ToString
@Getter
@Setter
public class NewUserEvent extends ApplicationEvent{
    private Map<String,String> username;

    public NewUserEvent(Object source, String username){
        super(source);
        this.username = new HashMap<>();
        this.username.put("username",username);
    }

    @JsonIgnore
    public boolean isValid(){
        return username!=null && !username.isEmpty();
    }
}
