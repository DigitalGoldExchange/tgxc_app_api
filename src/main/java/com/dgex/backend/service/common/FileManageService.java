package com.dgex.backend.service.common;

import com.dgex.backend.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


@Slf4j
@Service
public class FileManageService {

    @Value("${upload.url}")
    private String uploadDir;
    private Path fileStorageLocation;

    public String storeFile(MultipartFile file){
        this.fileStorageLocation = Paths.get("./uploads").toAbsolutePath().normalize();
        try{
            Files.createDirectories(this.fileStorageLocation);
        }catch (Exception e){
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",e);
        }

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyyMMdd_HHmmss", Locale.KOREA );
        Date currentTime = new Date ();
        String mTime = mSimpleDateFormat.format ( currentTime );

        UUID uuid = UUID.randomUUID();
        String[] fileOriginName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename()).split("\\.");

//        String fileName = mTime + "_" + org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
        String fileName = mTime + "_" + uuid.toString()+"."+fileOriginName[fileOriginName.length-1];

        try{
            if(fileName.contains("..")){
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        }catch (IOException e){
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
        }
    }
}
