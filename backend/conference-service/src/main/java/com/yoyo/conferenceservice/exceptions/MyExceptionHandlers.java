package com.yoyo.conferenceservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class MyExceptionHandlers {

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<?> fileException(MultipartException e){
//        e.printStackTrace();
        return new ResponseEntity<>(e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
    }
}
