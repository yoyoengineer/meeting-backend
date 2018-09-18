package com.binary.profileservice.event.model;

import com.binary.profileservice.ustils.UtilsClass;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NewUserEvent {
    private String username;

    @JsonIgnore
    public boolean isValid(){
        return UtilsClass.isStringNotNullOrEmpty(username);
    }
}
