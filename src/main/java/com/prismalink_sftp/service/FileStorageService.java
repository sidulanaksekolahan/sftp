package com.prismalink_sftp.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String upload(MultipartFile file);

    String upload(MultipartFile file, String folderName);
}
