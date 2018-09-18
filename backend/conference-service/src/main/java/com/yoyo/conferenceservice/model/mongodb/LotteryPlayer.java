package com.yoyo.conferenceservice.model.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@NoArgsConstructor
@ToString
@CompoundIndexes({
        @CompoundIndex(def = "{eventId:1,username:1}",unique = true)
})
public class LotteryPlayer {
    public enum Status{
        WIN,
        LOSE,
    }
    @Id
    private String id;
    private String username;
    private String eventId;



    private String name;
    private String prize;



    @JsonIgnore
    private Status status;

}
