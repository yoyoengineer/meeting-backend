package com.binary.websocketservice.conference.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
@RequiredArgsConstructor
@NoArgsConstructor(force=true)
@ToString
public class NotificationParcel {

    enum Type{
        FRIEND("FRIEND"),INVITATION("INVITATION"),CHAT("CHAT");

        private String name;
        Type(String name){
            this.name = name;
        }
        @Override
        public String toString() {
            return this.name;
        }
    }

    private final List<String> receivers;
    private final Type type;
    private final String content;

    public boolean isValid(){

        return receivers!=null &&
                type!=null &&
                content!=null;
    }

    public Map<String,String> getNotification(){
        Map<String,String> notification = new HashMap<>();
        String t;
        if(type==null)
            t="";

        else
            t=type.toString();
        notification.put("type",t);
        notification.put("content",content);

        return notification;
    }
}
