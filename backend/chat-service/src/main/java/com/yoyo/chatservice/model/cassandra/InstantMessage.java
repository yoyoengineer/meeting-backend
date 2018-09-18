package com.yoyo.chatservice.model.cassandra;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.util.Map;
import java.util.UUID;

@Getter
@ToString
@Table("messages")
public class InstantMessage {

    public  enum Type{TEXT, FILE}

    @JsonIgnore
    @PrimaryKeyColumn(name="historyId", ordinal=0, type = PrimaryKeyType.PARTITIONED)
    private String historyId;

    @JsonIgnore
    @PrimaryKeyColumn(ordinal=1, type=PrimaryKeyType.CLUSTERED,ordering = Ordering.DESCENDING)
    private UUID uuidTime;

    private Long time;
    private String text;
    private String fromUser;
    private String toUser;
    private Type type;
    private Map<String, String> extra;



    public InstantMessage(){
        this.type = Type.TEXT;
    }

    public InstantMessage withExtra(Map<String,String> extra){
        this.type=Type.FILE;
        this.extra = extra;
        return this;
    }

    public InstantMessage withHistoryId(String historyId){
        this.historyId = historyId;
        return this;
    }


    public InstantMessage withType(Type type){
        this.type = type;
        return this;
    }
    public InstantMessage withText(String text){
        this.text = text;
        return this;
    }

    public InstantMessage withFromUser(String fromUser){
        this.fromUser = fromUser;
        return this;
    }

    public InstantMessage withToUser(String toUser){
        this.toUser = toUser;
        return this;
    }

    @JsonIgnore
    public  InstantMessage withUUIDtime(){
        if(uuidTime ==null)
            uuidTime = UUIDs.timeBased();
        time  = UUIDs.unixTimestamp(uuidTime);
        return this;
    }

    // ADD A MESSAGE TO CHECK THE VALIDITY OF THE MESSAGE

}
