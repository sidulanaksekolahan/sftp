package com.prismalink_sftp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendZipFile(
            File attachment) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true);

            helper.setTo(
                    "alva.gani@prismalink.co.id");

//            helper.setCc(
//                    new String[]{
//                            "muhammadirfanit99@gmail.com"
//                    });

            helper.setSubject(
                    "PTEN File Download Notification");

            helper.setText(
                    "File PTEN terbaru terlampir.");

            helper.addAttachment(
                    attachment.getName(),
                    attachment);

            mailSender.send(message);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}
