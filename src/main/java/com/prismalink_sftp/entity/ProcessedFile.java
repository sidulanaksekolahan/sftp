package com.prismalink_sftp.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "processed_file")
public class ProcessedFile {

    @Id
    private String fileName;

    @Column(name = "folder_name")
    private String folderName;

    @CreationTimestamp
    @Column(name = "downloaded_at", updatable = false)
    private OffsetDateTime downloadedAt;

    public ProcessedFile() {

    }

    public ProcessedFile(String fileName, String folderName) {
        this.fileName = fileName;
        this.folderName = folderName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public OffsetDateTime getDownloadedAt() {
        return downloadedAt;
    }

    public void setDownloadedAt(OffsetDateTime downloadedAt) {
        this.downloadedAt = downloadedAt;
    }
}
