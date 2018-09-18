package com.yoyo.conferenceservice.service;

import com.kanz.conferenceservice.ustils.QRCodeUtil;
import com.mongodb.gridfs.GridFSDBFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

@Service
public class QRCodeGenerator {

    public static final String APP_LOGO_ID = "5b8bd02728b01a7cfc49b03a";

    private GridFsTemplate gridFsTemplate;

    @Autowired
    public QRCodeGenerator(GridFsTemplate gridFsTemplate){
        this.gridFsTemplate = gridFsTemplate;
    }


    public ByteArrayOutputStream generateQRcode(String dataEncodedInQRcode) throws IOException{

        Query query = new Query(Criteria.where("_id").is(APP_LOGO_ID));
        GridFSDBFile logoFile = gridFsTemplate.findOne(query);

        BufferedImage logo = ImageIO.read(logoFile.getInputStream());
        BufferedImage qrcode = QRCodeUtil.createQRCodeWithLogo(dataEncodedInQRcode,logo);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(qrcode,"png",  os);

        return os;

    }



}
