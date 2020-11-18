package com.dgex.backend.service.common;

import com.dgex.backend.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
    private static final String algorithm = "AES";
    private static final String transformation = algorithm + "/ECB/PKCS5Padding";

    public void encrypt(MultipartFile source, File dest) throws Exception{
        crypt(Cipher.ENCRYPT_MODE, source, dest);
    }
//
//    public void decrypt(File source, File dest){
//        crypt(Cipher.DECRYPT_MODE, source, dest);
//    }

    public void crypt(int mode, MultipartFile source, File dest) throws Exception {
        Path targetLocation = this.fileStorageLocation;
        String test = "123456789abcdefg";
        SecretKeySpec key = new SecretKeySpec(test.getBytes(), algorithm);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(mode, key);
        InputStream input = null;
        OutputStream output = null;
        try{
            input = new BufferedInputStream(source.getInputStream());
            output = new BufferedOutputStream(new FileOutputStream(dest));
            byte[] buffer = new byte[1024];
            int read = -1;
            while((read = input.read(buffer)) != -1){
                output.write(cipher.update(buffer,0,read));
            }
            output.write(cipher.doFinal());
        }finally {
            if(output != null){
                try {
                    output.close();
                }catch (IOException ie){

                }
            }
            if(input != null){
                try {
                    input.close();
                }catch (IOException ie){

                }
            }
        }
    }


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

        String fileName = mTime + "_" + uuid.toString()+"."+fileOriginName[fileOriginName.length-1];

        try{
            if(fileName.contains("..")){
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
//            encrypt(file,new File(fileName));

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        }catch (IOException e){
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
        }
    }
}
