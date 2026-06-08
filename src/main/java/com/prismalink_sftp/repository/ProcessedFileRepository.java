package com.prismalink_sftp.repository;

import com.prismalink_sftp.entity.ProcessedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedFileRepository extends JpaRepository<ProcessedFile, String> {
}
