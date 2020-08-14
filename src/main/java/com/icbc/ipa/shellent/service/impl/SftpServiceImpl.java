package com.icbc.ipa.shellent.service.impl;

import com.icbc.ipa.shellent.config.SftpClient;
import com.icbc.ipa.shellent.service.SftpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class SftpServiceImpl implements SftpService {
    @Autowired
    private SftpClient sftpClient;

    public List<String> lsRemoteDir(String path) {
        return sftpClient.ls(path);
    }

    @Override
    public boolean get(String source, String dst) {
        return sftpClient.get(source, dst);
    }

    @Override
    public InputStream getInputStream(String path) {
        return sftpClient.getStream(path);
    }
}
