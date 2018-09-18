package com.yoyo.conferenceservice.controller;

import com.kanz.conferenceservice.model.mongodb.FileModel;
import com.kanz.conferenceservice.service.activities.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
public class FileController {

    private FileService fileService;

    public FileController(FileService fileService){
        this.fileService = fileService;
    }

    @PostMapping("/event/file/{eventId}")
    public ResponseEntity<?> uploadFile(@PathVariable("eventId")String eventId,
                                        @RequestParam("file")MultipartFile multipartFile,
                                        Principal user){
        log.debug("In controller received request for file upload");
        return fileService.uploadFile(multipartFile, eventId, user.getName());

    }

    @GetMapping("/event/file/download/{eventId}/{fileId}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("eventId")String eventId,
                                                       @PathVariable("fileId")String fileId,
                                                       Principal user){
        return fileService.downloadFile(eventId,fileId,user.getName());
    }



    @GetMapping("/event/mobile/photo/{eventId}")
    public ResponseEntity<List<FileModel>> getPhotos(@PathVariable("eventId")String eventId,Principal user){

        return fileService.photos(eventId,user.getName());
    }

    @GetMapping("/event/mobile/files/{eventId}")
    public ResponseEntity<List<FileModel>> getOther(@PathVariable("eventId")String eventId,Principal user){

        return fileService.others(eventId,user.getName());
    }




    @PostMapping("event/file/delete/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable("fileId")String fileId,
                                        Principal user){
        return fileService.deleteFile(fileId,user.getName());
    }

    @GetMapping("/event/files/{eventId}")
    public ResponseEntity<List<FileModel>> getFiles(@PathVariable("eventId")String eventId,Principal user){

        return fileService.fileListGet(eventId,user.getName());
    }
}
