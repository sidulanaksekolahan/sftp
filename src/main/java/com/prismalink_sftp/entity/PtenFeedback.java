package com.prismalink_sftp.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "pten_feedback")
public class PtenFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "merchantName")
    private String merchantName;

    @Column(name = "statusMessage")
    private String statusMessage;

    @CreationTimestamp
    @Column(name = "created_at",  updatable = false)
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "downloaded_file_id")
    private DownloadedFile downloadedFile;

    public PtenFeedback() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DownloadedFile getDownloadedFile() {
        return downloadedFile;
    }

    public void setDownloadedFile(DownloadedFile downloadedFile) {
        this.downloadedFile = downloadedFile;
    }
}
