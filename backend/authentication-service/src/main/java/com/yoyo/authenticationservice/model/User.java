package com.yoyo.authenticationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yoyo.authenticationservice.ustils.MyEmailValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
@Setter
public class User {

    @Id
    private  String  username;
    private  String  password;
    private  boolean enabled;
    private String email;

    @JsonIgnore
    @Transient
    private static MyEmailValidator myEmailValidator = new MyEmailValidator();

    @JsonIgnore
    public boolean isValid(){
        return username!=null && password!=null
//                && email!=null
                && !username.isEmpty()
                && !password.isEmpty();
//                && myEmailValidator.validate(email);
    }
}
