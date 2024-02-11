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


        // create the directory if it doesn't exist
        Files.createDirectories(Paths.get(path));

        // copy the file or upload the file to the path.
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // Handle the exception according to your requirements
            throw new IOException("Failed to upload the file", e);
        }
        return fileName;
    }

    @Override
    public InputStream getResourceFile(String path, String fileName) throws FileNotFoundException,IOException {
    // path + File.separator + fileName;
        String filepath = Paths.get(path,fileName).toString();
        File file = new File(filepath);

        if(!file.exists()){
            throw new FileNotFoundException("File not Found:"+ filepath);
        }

        return Files.newInputStream(Paths.get(filepath));
    }
}
