package com.prismalink_sftp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DemoSftpApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoSftpApplication.class, args);
	}

}
