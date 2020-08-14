package com.icbc.ipa.shellent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShellentSftpDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShellentSftpDataApplication.class, args);
    }

}
