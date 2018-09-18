package com.yoyo.conferenceservice.model.cassandra;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.yoyo.conferenceservice.ustils.UtilsClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

@Table
@Getter
@NoArgsConstructor(force=true)
@ToString
@RequiredArgsConstructor
public class Voter {

    @PrimaryKeyColumn(ordinal=0,type= PrimaryKeyType.PARTITIONED)
    private final String bulletinId;
    @PrimaryKeyColumn(ordinal=1,type=PrimaryKeyType.CLUSTERED)
    private final String username;
    private final String choice;

    @JsonIgnore
    public boolean isValid(){
        return UtilsClass.isStringNotNullOrEmpty(bulletinId,username,choice);
    }
}
