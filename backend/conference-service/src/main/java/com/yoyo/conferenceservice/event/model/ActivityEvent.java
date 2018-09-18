package com.yoyo.conferenceservice.event.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.yoyo.conferenceservice.ustils.UtilsClass;
import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
public class ActivityEvent extends ApplicationEvent{
   public enum Type{
       COMMENT,
       VOTE,
       FILE,
       ANNOUNCEMENT,
       INVITATION,
       UPDATE,
       NEW_VOTE,
       LOTTERY,
       LOTTERY_WIN,
       ATTENDANCE,
       BULLET,
       AGENDA
   }


   private String eventId;
   private List<String> usernames;
   private boolean toAll;
   private String content;
   private Type type;

   public ActivityEvent(Object source,String content,String eventId,Type type){
       super(source);
       this.content = content;
       this.eventId = eventId;
       this.type = type;
   }

   @JsonIgnore
    public boolean isValid(){
       return UtilsClass.isStringNotNullOrEmpty(eventId,content)
               && type !=null;
   }

   public void setUsernames(List<String> usernames){
       if(this.usernames==null)
           this.usernames = new ArrayList<>(usernames);
       else
           this.usernames.addAll(usernames);
   }

}
