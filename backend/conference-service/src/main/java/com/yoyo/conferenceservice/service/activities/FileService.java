package com.yoyo.conferenceservice.service.activities;

import com.kanz.conferenceservice.model.mongodb.FileModel;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    
    ResponseEntity<?> uploadFile(MultipartFile multipartFile, String eventId, String username);
    ResponseEntity<InputStreamResource> downloadFile(String eventId, String fileId, String username);
    ResponseEntity<?> deleteFile(String fileId, String username);
    ResponseEntity<List<FileModel>> fileListGet(String eventId, String username);
    ResponseEntity<List<FileModel>> photos(String eventId, String username);
    ResponseEntity<List<FileModel>> others(String eventId, String username);
}
