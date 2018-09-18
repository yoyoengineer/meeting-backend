package com.binary.profileservice.model.neo4j;

import com.binary.profileservice.ustils.UtilsClass;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.neo4j.ogm.annotation.*;
import org.springframework.data.neo4j.annotation.QueryResult;


@NodeEntity(label="Conference")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"organizer"})
@JsonIgnoreProperties({"city","id","organizer",""})
@QueryResult
public class Conference {

    @GraphId
    private Long id;

    @Index(primary=true)
    private String eventId;

    @Relationship(type="LOCATED_IN")
    private City city;

    @Relationship(type="ORGANIZED",direction = Relationship.INCOMING)
    private Person organizer;
    private String topic;
    private String description;
    private Long time;
    private boolean eventPublicState;

    @Transient
    private Integer likes;
    @Transient
    private String town;

    @Transient
    private String username;

    @JsonIgnore
    public void organizedBy(Person organizer){
        System.out.println("In conference class "+organizer);
        this.organizer = organizer;
        organizer.getConferencesOrganized().add(this);
    }

    @JsonIgnore
    public boolean isValid(){
        return UtilsClass.isStringNotNullOrEmpty(eventId,username);
    }



}
