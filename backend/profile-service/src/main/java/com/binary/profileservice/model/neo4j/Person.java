package com.binary.profileservice.model.neo4j;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.neo4j.ogm.annotation.*;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.HashSet;
import java.util.Set;

@QueryResult
@NodeEntity(label="Person")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"conferencesOrganized","conferencesLiked","conferencesAttended","friends"})
@JsonIgnoreProperties({"id","school","company","conferencesAttended","conferencesLiked","conferencesOrganized","home", "current", "friends","conferencesParticipatedIn"})
public class Person {
    @GraphId private Long id;

    @Index(primary=true)
    private String username;
    private String name;
    private String aboutMe;
    private String birthday;
    private String photo;
    private int lastModified;

    @Relationship(type="STUDIED_AT")
    private School school;

    @Relationship(type="WORKS_AT")
    private Company company;

    @Relationship(type="ATTENDED")
    private Set<Conference> conferencesAttended = new HashSet<>();

    @Relationship(type="PARTICIPATES")
    private Set<Conference> conferencesParticipatedIn = new HashSet<>();

    @Relationship(type="LIKES")
    private Set<Conference> conferencesLiked = new HashSet<>();

    @Relationship(type="ORGANIZED")
    private Set<Conference> conferencesOrganized = new HashSet<>();

    @Relationship(type="HOME_TOWN")
    private City home;

    @Relationship(type="CURRENT_TOWN")
    private City current;

    @Relationship(type="FRIEND_WITH",direction = Relationship.UNDIRECTED)
    private Set<Person> friends = new HashSet<>();

    @Transient
    private String companyName;

    @Transient
    private String schoolName;

    @Transient
    private String homeTown;

    @Transient
    private String currentTown;

    @Transient
    private Integer nbrOfMutualFriends;

    @Transient
    private boolean areFriend;


    @JsonIgnore
    public void setConferencesAttended(Set<Conference> conferenceAttended){
        this.conferencesAttended.addAll(conferenceAttended);
    }

    @JsonIgnore
    public void setConferencesLiked(Set<Conference> conferencesLiked){
        this.conferencesLiked.addAll(conferencesLiked);
    }

    @JsonIgnore
    public void setConferencesOrganized(Set<Conference> conferencesOrganized){
        if(conferencesOrganized==null)
            return;
        this.conferencesOrganized.addAll(conferencesOrganized);
    }

    @JsonIgnore
    public void beFriend(Person friend){
        if(friend==null)
            return;

        this.friends.add(friend);
        friend.friends.add(this);
    }


    @JsonIgnore
    public boolean isValid(){
        return username!=null && !username.isEmpty();
    }

}
