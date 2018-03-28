package com.yesbinary.web;

import com.yesbinary.service.IFileService;
import com.yesbinary.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

//import com.yesbinary.util.FtpUtil;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;

/**
 * Created by Yoy on 2018/2/24.
 */
@Controller
@RequestMapping("/file")
public class UploadController {
    //Save the uploaded file to this folder
//    private static String UPLOADED_FOLDER = "H://temp//";
    @Autowired
    private IFileService fileService;

//    @Value("${from}")
//    private String from;
//
//    @RequestMapping("/from")
//    public String from() {
//        return this.from;
//    }

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload") // //new annotation since 4.3
    @ResponseBody
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        System.out.println("1111111");
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }
//
//        try {
////            // Get the file and save it somewhere
////            byte[] bytes = file.getBytes();
////            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
////            Files.write(path, bytes);
//
//            String fileName = file.getOriginalFilename();
//            InputStream inputStream = file.getInputStream();
//            FtpUtil.uploadFile("192.168.1.8",21,"user1","ftpuser","/CONT1","2018/2/25",fileName,inputStream);
//            redirectAttributes.addFlashAttribute("message",
//                    "You successfully uploaded '" + file.getOriginalFilename() + "'");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Map result = fileService.uploadFile(file,"2018/2/25");
        return JsonUtils.objectToJson(result);
//        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }
}
