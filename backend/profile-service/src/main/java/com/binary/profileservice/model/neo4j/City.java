package com.binary.profileservice.model.neo4j;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label="City")
@Getter
@Setter
@NoArgsConstructor
public class City {

    @GraphId
    private Long id;

    @Index(primary=true)
    private String name;
}
