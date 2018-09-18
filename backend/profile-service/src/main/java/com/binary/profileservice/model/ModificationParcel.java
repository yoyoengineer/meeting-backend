package com.binary.profileservice.model;

import com.binary.profileservice.ustils.UtilsClass;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Setter
@Getter
public class ModificationParcel {
    private  String fieldName;
    private  String fieldValue;

    @JsonIgnore
    public boolean isValid(){
        return UtilsClass.isStringNotNullOrEmpty(fieldName,fieldValue);
    }
}
