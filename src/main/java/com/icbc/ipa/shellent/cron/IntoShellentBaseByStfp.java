package com.icbc.ipa.shellent.cron;

import com.icbc.ipa.shellent.service.ShellentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IntoShellentBaseByStfp {
    private ShellentService shellentService;
    @Autowired
    public IntoShellentBaseByStfp(ShellentService shellentService){
        this.shellentService=shellentService;
    }

    @Scheduled(cron = "${entdata.into-base-sftp}")
    public void intoBaseBySftp(){
        shellentService.intoDatabase("/data/20200814");
    }
}
