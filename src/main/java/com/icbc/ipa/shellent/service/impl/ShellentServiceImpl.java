package com.icbc.ipa.shellent.service.impl;

import com.icbc.ipa.shellent.service.SftpService;
import com.icbc.ipa.shellent.service.ShellentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class ShellentServiceImpl implements ShellentService {
    private static final Pattern pattern = Pattern.compile("^202\\d[0-1]\\d[0-3]\\d$");
    private SftpService sftpService;

    @Autowired
    public ShellentServiceImpl(SftpService sftpService) {
        this.sftpService = sftpService;
    }

    @Override
    public void intoDatabase(String sshelent) throws IOException {
        List<String> files = sftpService.lsRemoteDir(sshelent);
        log.info("路径:->{}探壳目录文件:->{}", sshelent, files.toString());
        if (StringUtils.isEmpty(files)) {
            log.info(sshelent + "当前路径下没有可执行的探壳文件");
            return;
        }
//        files = files.stream()
//                .filter(dateStr -> pattern.matcher(dateStr).find())
//                .collect(Collectors.toList());
//        String yesterday = getYesterdayTime();
//        String yesterdayDate = null;
//        for (String date : files) {
//            if (yesterday.equals(date)) {
//                yesterdayDate = date;
//            }
//        }
        //获取文件sftp文件流
        InputStream inputStream = null;

        List<String> zipFiles = files.stream().filter(file -> file.contains(".zip")).distinct().collect(Collectors.toList());

        String sftpFilePath = sshelent + "/" + zipFiles.get(0);
        log.info("sftp下载路径:->{}", sftpFilePath);
        inputStream = sftpService.getInputStream(sftpFilePath);
        String md5 = DigestUtils.md5Hex(inputStream);
        String ok = sftpFilePath.replaceAll("\\.zip", "\\.ok");
        inputStream = sftpService.getInputStream(ok);
        String s = "beb11b6d34bece559f5a614ae04b7dff";
        boolean equals = md5.equals(s);
        System.out.println(equals);
        System.out.println(s);
        inputStream.close();

    }

    private String getYesterdayTime() {
        LocalDateTime nowTime = LocalDateTime.now();
        nowTime = nowTime.minusDays(0);
        return nowTime.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
}
