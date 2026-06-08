package com.prismalink_sftp.repository;

import com.prismalink_sftp.entity.ProcessedFolder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedFolderRepository extends JpaRepository<ProcessedFolder, String> {
}
