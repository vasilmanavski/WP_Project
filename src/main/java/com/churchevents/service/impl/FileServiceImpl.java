package com.churchevents.service.impl;

import com.churchevents.model.exceptions.FileStorageException;
import com.churchevents.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService {

    @Value("${app.upload.dir:${user.home}}")
    public String uploadDir;

    public void uploadFile(MultipartFile file){
        try{
            Path copyLocation = Paths
                    .get(uploadDir + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            file.transferTo(copyLocation);

            Files.move(copyLocation, Paths.get("src/main/resources/templates/pdf/Newsletter.pdf"), StandardCopyOption.REPLACE_EXISTING);



        } catch (Exception e) {
            e.printStackTrace();
            throw new FileStorageException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");

        }
    }

    public void getPath(MultipartFile file){

    }

    @Override
    public void saveFile(MultipartFile file) {


//        try {
//            Files.copy(file.getInputStream(), root.resolve(file.getOriginalFilename()));
//        } catch (Exception e) {
//            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
//        }
    }
}
