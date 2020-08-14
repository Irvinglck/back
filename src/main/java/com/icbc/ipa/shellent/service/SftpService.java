package com.icbc.ipa.shellent.service;

import java.io.InputStream;
import java.util.List;

public interface SftpService {

    List<String> lsRemoteDir(String path);

    boolean get(String source, String dst);

    InputStream getInputStream(String path);
}
