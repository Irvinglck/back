package com.icbc.ipa.shellent.cron;

import com.icbc.ipa.shellent.config.PropertiesValue;
import com.icbc.ipa.shellent.service.ShellentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class IntoShellentBaseByStfp implements CommandLineRunner {
    private ShellentService shellentService;
    private PropertiesValue propertiesValue;

    @Autowired
    public IntoShellentBaseByStfp(ShellentService shellentService, PropertiesValue propertiesValue) {
        this.shellentService = shellentService;
        this.propertiesValue = propertiesValue;
    }

    //sftp入库
//    @Scheduled(cron = "${entdata.into-base-sftp}")
    void intoBaseBySftp() {
        shellentService.intoDatabaseByStream(propertiesValue.getRemotePath());
    }

    //临时表meger到主表
    @Scheduled(cron = "${entdata.merge-shellent-data}")
    void mergeShellentData() {
        shellentService.mergeShellentData();
    }

    @Override
    public void run(String... args) {
//        intoBaseBySftp();
    }
}
