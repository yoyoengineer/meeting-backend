package com.binary.websocketservice.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@NoArgsConstructor(force=true)
public class InstantMessage {

    public static enum Type{TEXT, FILE}

    private final String fromUser;
    private final String toUser;
    private final String text;
    private final Type type;
    private final long time;
    private final Map<String, String> extra;

}
