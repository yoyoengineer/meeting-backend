package com.yesbinary.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Created by Yoy on 2018/2/26.
 */
public interface IFileService {
    Map uploadFile(MultipartFile uploadFile, String domainPath);
}
