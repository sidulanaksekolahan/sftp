package com.prismalink_sftp.controller;

import com.prismalink_sftp.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "PTEN",
        description = "PTEN SFTP Operations")
@RestController
@RequestMapping("/api/merchant-onboarding")
public class MerchantOnBoardingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantOnBoardingController.class);

    private final FileStorageService fileStorageService;

    @Autowired
    public MerchantOnBoardingController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Operation(
            summary = "Melihat daftar file dan direktori",
            description = "Menampilkan daftar file dan folder secara standar")
    @GetMapping("/ls")
    public String ls() throws Exception {

        ProcessBuilder processBuilder =
                new ProcessBuilder("ls", "-l", "/pten");

        Process process = processBuilder.start();

        return new String(
                process.getInputStream().readAllBytes());
    }

    @Operation(
            summary = "Menampilkan secara jelas hirarki folder dari direktori utama root",
            description = "Menampilkan jalur lengkap (lokasi folder) di mana Anda sedang berada saat ini di dalam sistem operasi Linux")
    @GetMapping("/pwd")
    public String pwd() throws Exception {

        final String command = "pwd";

        ProcessBuilder processBuilder =
                new ProcessBuilder(command);

        Process process = processBuilder.start();

        return new String(
                process.getInputStream().readAllBytes());
    }

    @Operation(
            summary = "Upload folder ke server PTEN",
            description = "Upload folder ke server PTEN dengan folder name yang ditentukan"
    )
    @GetMapping("/upload/{folderName}")
    public String upload(@PathVariable("folderName") String folderName) {

        try {
            ProcessBuilder processBuilder =
                    new ProcessBuilder("sh", "/pten/upload_pten_sftp.sh", "in", folderName);

            Process process = processBuilder.start();

            int exitCode = process.waitFor();

//            return new String(
//                    process.getInputStream().readAllBytes());

            return "Exit Code = " + exitCode;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

//    @Operation(
//            summary = "Upload file saja",
//            description = "Upload file yang akan dikirim ke PTEN"
//    )
//    @PostMapping("/upload")
//    public ResponseEntity<String> upload(@RequestParam("files") List<MultipartFile> files) {
//
//        for (MultipartFile file : files) {
//            fileStorageService.upload(file);
//        }
//
//        return ResponseEntity.ok("Successfully uploaded files");
//    }

    @Operation(
            summary = "Upload file kedalam folder PTEN_IN",
            description = "Upload file kedalam folder PTEN_IN dengan menentukan folder name"
    )
    @PostMapping("/uploadFile/{folderName}")
    public ResponseEntity<String> uploadFile(@RequestParam("files") List<MultipartFile> files, @PathVariable String folderName) {

        for (MultipartFile file : files) {
            fileStorageService.upload(file, folderName);
        }

        return ResponseEntity.ok("Successfully uploaded files");
    }

//    @Operation(
//            summary = "Membuat folder",
//            description = "Membuat folder. Contoh nama foldernya 20260608"
//    )
//    @GetMapping("/createFolder/{folderName}")
//    public String createFolder(@PathVariable("folderName") String folderName) throws Exception {
//
//        ProcessBuilder processBuilder =
//                new ProcessBuilder("mkdir", "-p", "/pten/PTEN_IN/" + folderName);
//
//        Process process = processBuilder.start();
//
//        int exitCode = process.waitFor();
//
////        return new String(
////                process.getInputStream().readAllBytes());
//
//        return "Exit Code = " + exitCode;
//    }
}
