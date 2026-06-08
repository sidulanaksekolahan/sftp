package com.prismalink_sftp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final String UPLOAD_DIR = "/home/nofrets.poai/pten/PTEN_IN/";

    @Override
    public String upload(MultipartFile file) {
        try {

            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = file.getOriginalFilename();

            Path destination =
                    uploadPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return destination.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String upload(MultipartFile file, String folderName) {
        try {

            Path uploadPath = Paths.get(UPLOAD_DIR + folderName);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = file.getOriginalFilename();

            Path destination =
                    uploadPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return destination.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
