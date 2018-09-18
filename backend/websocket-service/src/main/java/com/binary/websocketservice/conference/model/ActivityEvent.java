package com.binary.websocketservice.conference.model;

import com.binary.websocketservice.utils.UtilsClass;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import java.util.List;

@ToString
@Getter
@NoArgsConstructor(force=true)
public class ActivityEvent{
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
       LIKE,
       BULLET,
       AGENDA
   }

   private final String eventId;
   private final List<String> usernames;
   private final boolean toAll;
   private final String content;
   private final Type type;

   @JsonIgnore
    public boolean isValid(){
       return UtilsClass.isStringNotNullOrEmpty(eventId,content)
               && type!=null;
   }

}
