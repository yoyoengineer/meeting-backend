package com.yoyo.conferenceservice.model.mongodb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@ToString
@NoArgsConstructor(force=true)
@RequiredArgsConstructor
@Document
@CompoundIndexes(
        {
            @CompoundIndex(def = "{eventId:1,username:1}")
        }
)
public class Person {
    public enum STATE {PARTICIPATE,ATTEND}
    @Id
    private String id;
    private final String eventId;
    private final String username;
    private final STATE state;
}
