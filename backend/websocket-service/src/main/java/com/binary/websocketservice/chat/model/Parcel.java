package com.binary.websocketservice.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor(force=true)
@ToString
public class Parcel {
    private final List<String> toUsers;
    private final History history;
}
