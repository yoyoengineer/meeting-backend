package com.yesbinary.service.impl;

import com.yesbinary.service.IFileService;
import com.yesbinary.utils.FtpUtil;
import com.yesbinary.utils.IDUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yoy on 2018/2/26.
 */
@RefreshScope
@Service
public class FileService implements IFileService {

    @Value("${FTP_ADDRESS}")
    private String FTP_ADDRESS;
    @Value("${FTP_PORT}")
    private Integer FTP_PORT;
    @Value("${FTP_USERNAME}")
    private String FTP_USERNAME;
    @Value("${FTP_PASSWORD}")
    private String FTP_PASSWORD;
    @Value("${FTP_BASE_PATH}")
    private String FTP_BASE_PATH;
    @Value("${FILE_BASE_URL}")
    private String FILE_BASE_URL;

    @Override
    public Map uploadFile(MultipartFile uploadFile,String domainPath) {
        Map resultMap = new HashMap<>();
        try {
            //生成一个新的文件名
            //取原始文件名
            String oldName = uploadFile.getOriginalFilename();
            //生成新文件名
            //UUID.randomUUID();
            String newName = IDUtils.genImageName();
            newName = newName + oldName.substring(oldName.lastIndexOf("."));
            //图片上传
            String filePath = new DateTime().toString("/yyyy/MM/dd");
            filePath += "/"+domainPath;
            boolean result = FtpUtil.uploadFile(FTP_ADDRESS, FTP_PORT, FTP_USERNAME, FTP_PASSWORD,
                    FTP_BASE_PATH, filePath, newName, uploadFile.getInputStream());
            //返回结果
            if(!result) {
                resultMap.put("error", 1);
                resultMap.put("message", "文件上传失败");
                return resultMap;
            }
            resultMap.put("error", 0);
            resultMap.put("url", FILE_BASE_URL + filePath + "/" + newName);
            return resultMap;

        } catch (Exception e) {
            resultMap.put("error", 1);
            resultMap.put("message", "文件上传发生异常");
            return resultMap;
        }
    }


}
