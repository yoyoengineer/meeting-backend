package com.yoyo.conferenceservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor(force=true)
@Getter
public class ModificationParcel {
    private final String fieldName;
    private final Object fieldValue;
}
