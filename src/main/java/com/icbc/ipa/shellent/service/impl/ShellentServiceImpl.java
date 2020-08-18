package com.icbc.ipa.shellent.service.impl;

import com.icbc.ipa.shellent.service.SftpService;
import com.icbc.ipa.shellent.service.ShellentService;
import com.icbc.ipa.shellent.util.StringsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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


        List<String> zipFiles = files.stream().filter(file -> file.contains(".zip")).distinct().collect(Collectors.toList());

        String sftpFilePath = sshelent + "/" + zipFiles.get(0);
        log.info("sftp下载路径:->{}", sftpFilePath);
        InputStream zipInputStream = sftpService.getInputStream(sftpFilePath);
        //当前批次zip文件下载到本地
        String zipAbsoluteLocalPath = downZiptoLocal(zipInputStream, sftpFilePath);
        boolean zipResult = veriyMd5Zip(zipAbsoluteLocalPath, sftpFilePath);
        if (!zipResult) {
            log.info("{}批次文件OK文件ZIP文件md5验证失败", sftpFilePath);
            return;
        }
        //zip解压成csv
        List<String> zipDataLists = analysisLocalZip(zipAbsoluteLocalPath);
        if(CollectionUtils.isEmpty(zipDataLists)){
            log.info("{}批次ZIP文件解析为空", sftpFilePath);
            return;
        }
        //解密文件入库
        decZipMapsIntoSql(zipDataLists);

    }

    private void decZipMapsIntoSql(List<String> zipDataLists) {
    }

    /**
     * zip解压
     * @param srcFile        zip源文件
     * @param destDirPath     解压后的目标文件夹
     * @throws RuntimeException 解压失败会抛出运行时异常
     */
    //解压zip文件
    public  String unZip(File srcFile, String destDirPath) throws RuntimeException {
        String result=null;
        long start = System.currentTimeMillis();
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        // 开始解压
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcFile);
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                System.out.println("解压" + entry.getName());
                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = destDirPath + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(destDirPath + "/" + entry.getName());
                    // 保证这个文件的父文件夹必须要存在
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    // 将压缩文件内容写入到这个文件中
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    // 关流顺序，先打开的后关闭
                    fos.close();
                    is.close();
                    result=destDirPath + "/" + entry.getName();
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("解压完成，耗时：" + (end - start) + " ms");

        } catch (Exception e) {
            throw new RuntimeException("unzip error from ZipUtils", e);
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private List<String> analysisLocalZip(String file) throws IOException {
        List<String> resultList=new ArrayList<>();
        ZipFile zf = new ZipFile(file);
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry ze;
        int count=0;
        while ((ze = zin.getNextEntry()) != null) {
            if (ze.isDirectory()) {
            } else {
                log.info("file - " + ze.getName() + " : " + ze.getSize() + " bytes");
                long size = ze.getSize();
                if (size > 0) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(zf.getInputStream(ze)));
                    String line;
                    while ((line = br.readLine()) != null) {
                        resultList.add(line);
                        count++;
                    }
                    br.close();
                }
            }
        }
        log.info("总的记录数-->{}",count);
        zin.closeEntry();
        return resultList;
    }


    private boolean veriyMd5Zip(String zipLocalPath, String sftpFilePath) {
        FileInputStream fis = null;
        try {

            String okPath = sftpFilePath.replaceAll("\\.zip", "\\.ok");
            String okFileContetnt = readOkKey(sftpService.getInputStream(okPath));
            if (StringUtils.isEmpty(okFileContetnt)) {
                log.info("无法获取sftp{}批次文件流", okPath);
                return false;
            }
            fis = new FileInputStream(new File(zipLocalPath));
            if (!okFileContetnt.equals(DigestUtils.md5Hex(fis))) {
                log.info("{}批次ZIP文件验证失败", okPath);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private String downZiptoLocal(InputStream zipInputStream, String zipFileName) {
        String result = null;
        FileOutputStream out = null;
        try {
            int index;
            byte[] bytes = new byte[2048];
            File file = new File("D:\\lcoalcsv\\test.zip");
            out = new FileOutputStream(file);
            while ((index = zipInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, index);
                out.flush();
            }
            result = file.getAbsolutePath();
        } catch (IOException e) {
            log.info("sftp写入本地文件失败,批次-->{}", zipFileName);
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("{}批次文件写入本地", zipFileName);
        return result;
    }

    private String readOkKey(InputStream inputStream) {
        String result = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            result = br.readLine();
        } catch (IOException e) {
            log.error("读取ok文件失败");
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private String getYesterdayTime() {
        LocalDateTime nowTime = LocalDateTime.now();
        nowTime = nowTime.minusDays(0);
        return nowTime.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
}
