package com.prismalink_sftp.service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.prismalink_sftp.entity.ProcessedFile;
import com.prismalink_sftp.entity.ProcessedFolder;
import com.prismalink_sftp.repository.ProcessedFileRepository;
import com.prismalink_sftp.repository.ProcessedFolderRepository;
import com.prismalink_sftp.utils.ZipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

@Service
public class PtenServiceImpl implements PtenService {

    private final static Logger LOGGER = LoggerFactory.getLogger(PtenServiceImpl.class);

    private static final String REMOTE_DIR =
            "/home/prisma_link/PTEN_OUT";

    private static final String LOCAL_DIR =
            "/pten/PTEN_OUT";

    @Value("${pten.sftp.username}")
    private String ptenSftpUsername;

    @Value("${pten.sftp.password}")
    private String ptenSftpPassword;

    @Value("${pten.sftp.host}")
    private String ptenSftpHost;

    @Value("${pten.sftp.port}")
    private String ptenSftpPort;

    private final ProcessedFolderRepository folderRepository;

    private final ProcessedFileRepository fileRepository;

    private final EmailService emailService;

    private final ZipUtil zipUtil;

    @Autowired
    public PtenServiceImpl(ProcessedFolderRepository folderRepository, ProcessedFileRepository fileRepository,
                           EmailService emailService, ZipUtil zipUtil) {
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
        this.emailService = emailService;
        this.zipUtil = zipUtil;
    }

    @Override
    public void processNewFolders() {

        ChannelSftp channel = login();

        try {

            List<String> folders =
                    listDirectories(
                            channel,
                            REMOTE_DIR);

            for (String folder : folders) {
                LOGGER.info("Processing PTEN folder: {}", folder);

                processFolder(
                        channel,
                        folder);

                if (!folderRepository.existsById(folder)) {

                    folderRepository.save(
                            new ProcessedFolder(folder));
                }
            }

        } finally {

            disconnect(channel);
        }
    }

    private ChannelSftp login() {

        try {

            JSch jsch = new JSch();

            Session session =
                    jsch.getSession(
                            ptenSftpUsername,
                            ptenSftpHost,
                            Integer.parseInt(ptenSftpPort));

            session.setPassword(ptenSftpPassword);

            session.setConfig(
                    "StrictHostKeyChecking",
                    "no");

            session.connect();

            ChannelSftp channel =
                    (ChannelSftp)
                            session.openChannel("sftp");

            channel.connect();

            LOGGER.info("Connected to SFTP server");

            return channel;

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    private void disconnect(
            ChannelSftp channel) {

        try {

            Session session =
                    channel.getSession();

            channel.disconnect();

            session.disconnect();

        } catch (Exception ignored) {
        }
    }

    private void processFolder(
            ChannelSftp channel,
            String folderName) {

        String dateFolder =
                REMOTE_DIR +
                        "/" +
                        folderName;

        List<String> batchFolders =
                listDirectories(
                        channel,
                        dateFolder);

        for (String batchFolder : batchFolders) {

//            LOGGER.info("batchFolder: {}", batchFolder);

            processBatchFolder(
                    channel,
                    folderName,
                    batchFolder);
        }
    }

    private void processBatchFolder(
            ChannelSftp channel,
            String dateFolder,
            String batchFolder) {

        String remoteBatchFolder =
                REMOTE_DIR +
                        "/" +
                        dateFolder +
                        "/" +
                        batchFolder;

        List<String> files =
                listFiles(
                        channel,
                        remoteBatchFolder);
//        LOGGER.info("files: {}", files);

        for (String fileName : files) {

//            LOGGER.info("fileName: {}", fileName);

            String uniqueKey =
                    dateFolder +
                            "/" +
                            batchFolder +
                            "/" +
                            fileName;
//            LOGGER.info("uniqueKey: {}", uniqueKey);
//            LOGGER.info("!fileRepository.existsById(uniqueKey): {}", !fileRepository.existsById(uniqueKey));

            if (!fileRepository.existsById(
                    uniqueKey)) {

                downloadFile(
                        channel,
                        dateFolder,
                        batchFolder,
                        fileName);

                fileRepository.save(
                        new ProcessedFile(
                                uniqueKey,
                                dateFolder));
            }
        }
    }

    private List<String> listDirectories(
            ChannelSftp channel,
            String path) {

        try {

            Vector<ChannelSftp.LsEntry> entries =
                    channel.ls(path);

            return entries.stream()
                    .filter(e ->
                            e.getAttrs().isDir())
                    .map(ChannelSftp.LsEntry::getFilename)
                    .filter(name ->
                            !name.equals("."))
                    .filter(name ->
                            !name.equals(".."))
                    .collect(Collectors.toList());

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    private List<String> listFiles(
            ChannelSftp channel,
            String path) {

        try {

            Vector<ChannelSftp.LsEntry> entries =
                    channel.ls(path);

            return entries.stream()
                    .filter(e ->
                            !e.getAttrs().isDir())
                    .map(ChannelSftp.LsEntry::getFilename)
                    .collect(Collectors.toList());

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    private void downloadFile(
            ChannelSftp channel,
            String dateFolder,
            String batchFolder,
            String fileName) {

        try {

            Path localDir =
                    Paths.get(
                            LOCAL_DIR,
                            dateFolder,
                            batchFolder);

//            LOGGER.info("localDir absolute: {}",
//                    localDir.toAbsolutePath());

            Files.createDirectories(
                    localDir);

            String remoteFile =
                    REMOTE_DIR
                            + "/"
                            + dateFolder
                            + "/"
                            + batchFolder
                            + "/"
                            + fileName;
            LOGGER.info("Downloading file: {}", remoteFile);

            String localFile =
                    localDir
                            + "/"
                            + fileName;
            LOGGER.info("localFile: {}", localFile);

//            LOGGER.info("localFile absolute: {}",
//                    Paths.get(localFile).toAbsolutePath());

//            // ############## testing
//            Path testFile =
//                    Paths.get(localDir.toString(),
//                            "test.txt");
//
//            Files.writeString(
//                    testFile,
//                    "HELLO");
//
//            LOGGER.info(
//                    "test exists={}",
//                    Files.exists(testFile));
//            // ############## testing

            channel.get(
                    remoteFile,
                    localFile);

            Path downloadedFile =
                    Paths.get(localFile);

            // send email
            File zipFile =
                    zipUtil.zipFile(
                            downloadedFile);

            emailService.sendZipFile(
                    zipFile);

            LOGGER.info(
                    "exists={}",
                    Files.exists(downloadedFile));

            LOGGER.info(
                    "size={}",
                    Files.size(downloadedFile));

            LOGGER.info(
                    "user={}",
                    System.getProperty("user.name"));

            LOGGER.info(
                    "Downloaded {}",
                    remoteFile);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}
