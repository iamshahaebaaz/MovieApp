package com.movieflix.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        // get name of the file
        String fileName = file.getOriginalFilename();

        // get the file path
        String filePath = path + File.separator + fileName;

        //create a file object
        File f= new File(path);
        if(!f.exists()){
            f.mkdirs();
        }

        //copy the file or upload the file to the path.
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    @Override
    public InputStream getResourceFile(String path, String fileName) throws FileNotFoundException {
        String filepath = path + File.separator + fileName;
        return new FileInputStream(filepath);
    }
}
