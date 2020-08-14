package com.icbc.ipa.shellent.config;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.stream.Collectors;

@Configuration
public class SftpClient {

    private static final Logger log = LoggerFactory.getLogger(SftpClient.class);

    private Session session;
    private ChannelSftp channelSftp;
    private static ThreadLocal<SftpClient> sftpLocal = new ThreadLocal<>();

    private static String hosts;

    @Value("${chinadaas.sftp.hosts}")
    private void setHosts(String host1) {
        hosts = host1;
    }

    private static Integer port;

    @Value("${chinadaas.sftp.port}")
    private void setPort(Integer port1) {
        port = port1;
    }

    private static String username;

    @Value("${chinadaas.sftp.username}")
    private void setUsername(String username1) {
        username = username1;
    }

    private static String password;

    @Value("${chinadaas.sftp.password}")
    private void setPassword(String password1) {
        password = password1;
    }

    public SftpClient(String type) {
        init();
    }

    public SftpClient() {
    }

    /**
     * 获取SftpClient
     */
    private SftpClient getSftpClient() {
        SftpClient sftpClient = sftpLocal.get();
        if (null == sftpClient || !sftpClient.isConnected()) {
            sftpLocal.set(new SftpClient("init"));
            sftpClient = sftpLocal.get();
        }
        return sftpClient;
    }


    private boolean isConnected() {
        return null != channelSftp && channelSftp.isConnected();
    }

    public void init() {
        try {
            session = new JSch().getSession(username, hosts, port);

            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            // 2.通过session的sftp方式连接资源服务器下载变更监控文件
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放本地线程存储的sftp客户端
     */
    public static void release() {
        if (null != sftpLocal.get()) {
            sftpLocal.get().closeChannel();
            sftpLocal.set(null);
        }
    }

    /**
     * 关闭通道
     *
     * @throws Exception
     */
    public void closeChannel() {
        if (null != channelSftp) {
            try {
                channelSftp.disconnect();
            } catch (Exception e) {
                log.error("关闭SFTP通道发生异常:", e);
            }
        }
        if (null != session) {
            try {
                session.disconnect();
            } catch (Exception e) {
                log.error("SFTP关闭 session异常:", e);
            }
        }
    }

    public InputStream getStream(String path) {
        try {
            SftpClient sftpClient = getSftpClient();
            return sftpClient.channelSftp.get(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        channelSftp.quit();
        channelSftp.disconnect();
        session.disconnect();
    }

    public List<String> ls(String path) {
        try {
            SftpClient sftpClient = getSftpClient();
            Vector<ChannelSftp.LsEntry> list = sftpClient.channelSftp.ls(path);
            return list.stream()
                    .map(lsEntry -> lsEntry.getFilename())
                    .filter(name -> !(name.equals(".") || name.equals("..")))
                    .collect(Collectors.toList());
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean get(String source, String dst) {
        try {
            SftpClient sftpClient = getSftpClient();
            sftpClient.channelSftp.get(source, dst);
            return true;
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//    @PostConstruct
//    public ChannelSftp getSftpChannel() {
//        try {
//            session = new JSch().getSession(sftpProperties.getUsername(), sftpProperties.getHosts(), sftpProperties.getPort());
//            session.setPassword(sftpProperties.getPassword());
//            Properties p = new Properties();
//            p.put("StrictHostKeyChecking", "no");
//            session.setConfig(p);
//            session.connect();
//            // 2.通过session的sftp方式连接资源服务器下载变更监控文件
//            channelSftp = (ChannelSftp) session.openChannel("sftp");
//            channelSftp.connect();
//            return channelSftp;
//        } catch (JSchException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}
