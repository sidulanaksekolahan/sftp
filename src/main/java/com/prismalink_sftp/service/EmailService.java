package com.prismalink_sftp.service;

import java.io.File;

public interface EmailService {

    void sendZipFile(File attachment);
}
