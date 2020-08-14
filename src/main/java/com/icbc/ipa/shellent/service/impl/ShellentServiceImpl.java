package com.icbc.ipa.shellent.service.impl;

import com.icbc.ipa.shellent.service.SftpService;
import com.icbc.ipa.shellent.service.ShellentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShellentServiceImpl implements ShellentService {
    private static final Pattern pattern = Pattern.compile("^202\\d[0-1]\\d[0-3]\\d$");
    private SftpService sftpService;
    @Autowired
    public ShellentServiceImpl(SftpService sftpService){
        this.sftpService=sftpService;
    }
    @Override
    public void intoDatabase(String sshelent) {
        List<String> files = sftpService.lsRemoteDir(sshelent);
        if(StringUtils.isEmpty(files)){
            log.info(sshelent+"当前路径下没有可执行的探壳文件");
            return;
        }
        files = files.stream()
                .filter(dateStr -> pattern.matcher(dateStr).find())
                .collect(Collectors.toList());
        String yesterday = getYesterdayTime();
        String yesterdayDate = null;
        for (String date : files) {
            if (yesterday.equals(date)) {
                yesterdayDate = date;
            }
        }
        System.out.println(dates);
    }
    private String getYesterdayTime() {
        LocalDateTime nowTime = LocalDateTime.now();
        nowTime = nowTime.minusDays(0);
        return nowTime.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
}
