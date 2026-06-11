package com.prismalink_sftp.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "downloaded_file")
public class DownloadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_status")
    private String fileStatus;

    @Column(name = "merchant_count")
    private Integer merchantCount;

    @CreationTimestamp
    @Column(name = "downloaded_at", updatable = false)
    private OffsetDateTime downloadedAt;

    @OneToMany(
            mappedBy = "downloadedFile",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PtenFeedback> feedbacks = new ArrayList<>();

    public DownloadedFile() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }

    public Integer getMerchantCount() {
        return merchantCount;
    }

    public void setMerchantCount(Integer merchantCount) {
        this.merchantCount = merchantCount;
    }

    public OffsetDateTime getDownloadedAt() {
        return downloadedAt;
    }

    public void setDownloadedAt(OffsetDateTime downloadedAt) {
        this.downloadedAt = downloadedAt;
    }

    public List<PtenFeedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<PtenFeedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public void addFeedback(
            PtenFeedback feedback) {

        feedbacks.add(feedback);

        feedback.setDownloadedFile(this);
    }
}
