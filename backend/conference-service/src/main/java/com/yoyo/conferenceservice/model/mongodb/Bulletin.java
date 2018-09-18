package com.yoyo.conferenceservice.model.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yoyo.conferenceservice.ustils.UtilsClass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@ToString
@Getter
@Setter
@Document
public class Bulletin {

    @Id private String id;
    private String question;
    @Transient  private List<String> options;
    private String eventId;
    private Map<String,Integer> results;
    @Transient private String myChoice;



    // To let the user interface tell the user that he voted already
    @Transient private boolean userVoted;






    @JsonIgnore private Set<String> pendingVotes;


    @JsonIgnore
    public boolean isValid(){
        return UtilsClass.isStringNotNullOrEmpty(eventId,question)
                && options!=null && !options.isEmpty();
    }

    @JsonIgnore
    public void initializeResults(){
        if(isValid()){
            results = new HashMap<>();
            options.forEach(option ->results.put(option,0));
        }
    }

    @JsonIgnore
    public void optionsToList(){
        if(results!=null){
            options = new ArrayList<>(results.keySet());
        }
    }


}
