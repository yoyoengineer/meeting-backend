package com.yoyo.chatservice.model.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document
@AllArgsConstructor
@ToString
@Getter
public class History {

    public enum Type{FILE,TEXT}

    @Id
    private String id;

    @JsonIgnore
    private String username;
    private String fromUser;
    private String name;
    private String toUser;
    private Long time;
    private String text;
    private Type type;
    private Map<String,String> extra;


    public History(){
     type = Type.TEXT;
    }

    public History withFromUser(String fromUser){
        this.fromUser = fromUser;
        return this;
    }

    public History withName(String name){
        this.name = name;
        return this;
    }

    public History withToUser(String toUser){
        this.toUser = toUser;
        return this;
    }

    public History withTime(Long time){
        this.time = time;
        return this;
    }


    public History withText(String content){
        this.text = content;
        return this;
    }

    public History withExtra(Map<String,String> extra){
        this.extra = extra;
        return this;
    }

    public History withType(Type type){
        this.type = type;
        return this;
    }
   public History withUsername(String username){
        this.username = username;
        return this;
   }

   public History setId(String id){
        this.id = id;
        return this;
   }
}
