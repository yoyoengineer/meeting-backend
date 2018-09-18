package com.yoyo.conferenceservice.model.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.yoyo.conferenceservice.ustils.UtilsClass;
import jnr.ffi.annotations.In;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
public class FileModel {
    @Id private String id;
    private String fileName;
    @Indexed private String fileId;
    private String contentType;
    private Long size;
    private Long time;
    private String owner;
    @Indexed private String eventId;

    @JsonIgnore
    public boolean isValid(){
        return UtilsClass.isStringNotNullOrEmpty(fileName,eventId)
                && size!= null && size > 0;
    }
}
