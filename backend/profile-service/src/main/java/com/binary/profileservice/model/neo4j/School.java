package com.binary.profileservice.model.neo4j;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
@Getter
@Setter
public class School {
    @GraphId
    private Long id;

    @Index(primary=true)
    private String name;


}
