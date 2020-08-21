package com.icbc.ipa.shellent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.icbc.ipa.shellent.dao")
public class ShellentSftpDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShellentSftpDataApplication.class, args);
    }

}
