package com.prismalink_sftp.repository;

import com.prismalink_sftp.entity.DownloadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DownloadedFileRepository extends JpaRepository<DownloadedFile, Long> {
}
