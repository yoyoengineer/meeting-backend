package com.yoyo.chatservice.model;

import com.yoyo.chatservice.model.mongo.History;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Parcel {
    private Set<String> toUsers;
    private History history;
}
