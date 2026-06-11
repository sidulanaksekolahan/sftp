package com.prismalink_sftp.service;

import com.prismalink_sftp.entity.DownloadedFile;
import com.prismalink_sftp.entity.PtenFeedback;
import com.prismalink_sftp.repository.DownloadedFileRepository;
import com.prismalink_sftp.repository.PtenFeedbackRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackExcelServiceImpl implements FeedbackExcelService {

    private final DownloadedFileRepository downloadedFileRepository;

    private final PtenFeedbackRepository ptenFeedbackRepository;

    @Autowired
    public FeedbackExcelServiceImpl(PtenFeedbackRepository ptenFeedbackRepository, DownloadedFileRepository downloadedFileRepository) {
        this.ptenFeedbackRepository = ptenFeedbackRepository;
        this.downloadedFileRepository = downloadedFileRepository;
    }

    @Transactional
    @Override
    public void processExcel(String filePath) {

        try {
            File excelFile = new File(filePath);

            String fileName = excelFile.getName();

            boolean failedFile = fileName.startsWith("FAILED");

            Workbook workbook = WorkbookFactory.create(excelFile);

            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(3);

            int merchantColumn = findMerchantColumn(headerRow);

            if (merchantColumn == -1) {

                throw new RuntimeException("Merchant column not found");
            }

            int statusColumn = -1;

            if (failedFile) {

                statusColumn = findStatusColumn(headerRow);
            }

            DownloadedFile downloadedFile = new DownloadedFile();

            downloadedFile.setFileName(fileName);

            downloadedFile.setFileStatus(failedFile ? "FAILED" : "SUCCESS");

            downloadedFile = downloadedFileRepository.save(downloadedFile);

            List<PtenFeedback> feedbacks =
                    new ArrayList<>();

            int merchantCount = 0;

            for (int rowIndex = 4; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                Row row = sheet.getRow(rowIndex);

                if (row == null) {
                    continue;
                }

                Cell merchantCell = row.getCell(merchantColumn);

                if (merchantCell == null) {
                    continue;
                }

                String merchantName = merchantCell.toString().trim();

                if (merchantName.isEmpty()) {
                    continue;
                }

                PtenFeedback feedback = new PtenFeedback();

                feedback.setMerchantName(merchantName);

                feedback.setDownloadedFile(downloadedFile);

                /*
                 * Hanya file FAILED yang
                 * memiliki informasi STATUS
                 */
                if (failedFile && statusColumn != -1) {

                    Cell statusCell = row.getCell(statusColumn);

                    if (statusCell != null) {

                        feedback.setStatusMessage(statusCell.toString().trim());
                    }
                }

                feedbacks.add(feedback);

                merchantCount++;
            }

            ptenFeedbackRepository.saveAll(feedbacks);

            downloadedFile.setMerchantCount(merchantCount);

            downloadedFileRepository.save(downloadedFile);

            workbook.close();

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    private int findStatusColumn(Row headerRow) {

        for (Cell cell : headerRow) {

            String value = cell.toString().trim();

            if ("STATUS".equalsIgnoreCase(value)) {
                return cell.getColumnIndex();
            }
        }
        return -1;
    }

    private int findMerchantColumn(Row headerRow) {

        for (Cell cell : headerRow) {

            String value = cell.toString().trim();

            if ("Nama Merchant (max 50)".equalsIgnoreCase(value)) {

                return cell.getColumnIndex();
            }

            if ("NAMA_MERCHANT".equalsIgnoreCase(value)) {
                return cell.getColumnIndex();
            }

            if ("MERCHANT_NAME".equalsIgnoreCase(value)) {
                return cell.getColumnIndex();
            }

            if ("MERCHANT".equalsIgnoreCase(value)) {

                return cell.getColumnIndex();
            }
        }
        return -1;
    }
}
