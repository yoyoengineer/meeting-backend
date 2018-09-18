package com.yoyo.conferenceservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.yoyo.conferenceservice.ustils.UtilsClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(force=true)
@RequiredArgsConstructor
public class Choice {
    private final String bulletinId;
    private final String choice;

    @JsonIgnore
    public boolean isValid(){
    return UtilsClass.isStringNotNullOrEmpty(bulletinId,choice);
    }
}
