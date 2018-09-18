package com.yoyo.conferenceservice.model.cassandra;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.util.UUID;


@Getter
@NoArgsConstructor(force=true)
@Table
@ToString
public class Announcement {

    @PrimaryKeyColumn(ordinal=0,type= PrimaryKeyType.PARTITIONED)
    private final String eventId;
    @PrimaryKeyColumn(ordinal=1, type=PrimaryKeyType.CLUSTERED,ordering = Ordering.DESCENDING)
    private  UUID uuidTime;
    private final String text;

    @Transient
    private Long time;

    @JsonIgnore
    public boolean isValid(){
        return eventId!=null &&
                !eventId.isEmpty() &&
                text!=null &&
                !text.isEmpty();
    }

    @JsonIgnore
    public synchronized void setUUIDtime(){
        if(uuidTime ==null)
            uuidTime = UUIDs.timeBased();
        time  = UUIDs.unixTimestamp(uuidTime);
    }


}
