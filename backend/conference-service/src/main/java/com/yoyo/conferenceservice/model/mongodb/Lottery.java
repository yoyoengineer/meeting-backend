package com.yoyo.conferenceservice.model.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.yoyo.conferenceservice.ustils.UtilsClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Lottery {
    public enum State{
        RUNNING,
        STOPPED
    }

    @Id
    private String id;

    @Indexed
    private String eventId;

    private Map<String,Integer> items;
    private String qrCodeId;

    @JsonIgnore
    private State state;

    @JsonIgnore
    public boolean isValid(){
       boolean  valid =  UtilsClass.isStringNotNullOrEmpty(eventId)
                && items!=null && !items.isEmpty();

       if(valid){
           for(Map.Entry entry: items.entrySet()){
                if(entry.getValue()==null){
                    valid = false;
                }

           }
       }

       return valid;
    }
}
