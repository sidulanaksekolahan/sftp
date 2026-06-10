package com.prismalink_sftp.utils;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ZipUtil {

    public File zipFile(
            Path sourceFile)
            throws IOException {

        String zipName =
                sourceFile.toString()
                        + ".zip";

        File zipFile =
                new File(zipName);

        try (
                FileOutputStream fos =
                        new FileOutputStream(zipFile);

                ZipOutputStream zos =
                        new ZipOutputStream(fos);

                FileInputStream fis =
                        new FileInputStream(
                                sourceFile.toFile())
        ) {

            ZipEntry zipEntry =
                    new ZipEntry(
                            sourceFile
                                    .getFileName()
                                    .toString());

            zos.putNextEntry(
                    zipEntry);

            byte[] buffer =
                    new byte[1024];

            int length;

            while ((length =
                    fis.read(buffer)) > 0) {

                zos.write(
                        buffer,
                        0,
                        length);
            }

            zos.closeEntry();
        }

        return zipFile;
    }
}
