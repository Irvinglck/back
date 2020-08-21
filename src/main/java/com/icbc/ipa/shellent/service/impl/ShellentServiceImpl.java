package com.icbc.ipa.shellent.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icbc.ipa.shellent.config.PropertiesValue;
import com.icbc.ipa.shellent.dao.EntinfoDao;
import com.icbc.ipa.shellent.dao.TraceLogDao;
import com.icbc.ipa.shellent.enums.BatchZipFileLog;
import com.icbc.ipa.shellent.model.Entinfo;
import com.icbc.ipa.shellent.model.TraceLog;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class ShellentServiceImpl implements ShellentService {
    private SftpService sftpService;
    private EntinfoDao entinfoDao;
    private TraceLogDao traceLogDao;
    private PropertiesValue propertiesValue;


    @Autowired
    public ShellentServiceImpl(SftpService sftpService, EntinfoDao entinfoDao, TraceLogDao traceLogDao, PropertiesValue propertiesValue) {
        this.sftpService = sftpService;
        this.entinfoDao = entinfoDao;
        this.traceLogDao = traceLogDao;
        this.propertiesValue = propertiesValue;
    }

    @Override
    public void intoDatabaseByStream(String remoteShellPath) {//data/20200602
        List<String> dirs = sftpService.lsRemoteDir(remoteShellPath);
        String maxDir = dirs.stream().max(String::compareTo).get();
        remoteShellPath = remoteShellPath + "/" + maxDir;

        // TODO 暂时写死 remoteShellPath
        remoteShellPath = "/data/20200602";

        List<String> files = sftpService.lsRemoteDir(remoteShellPath);
        log.info("路径->{}探壳目录文件{}", remoteShellPath, files.toString());
        if (CollectionUtils.isEmpty(files)) {
            log.info(remoteShellPath + " >>> 当前路径下没有可执行的探壳文件");
            return;
        }
        List<String> zipFiles = files.stream().filter(file -> file.contains(".zip"))
                .distinct().sorted().collect(Collectors.toList());
        //断点下载继续解析
        zipFiles = undoneZipFiles(remoteShellPath, zipFiles);
        log.info("当前月份所有批次压缩ZIP文件{}", zipFiles);
        String finalRemoteShellPath = remoteShellPath;
        zipFiles.forEach(item -> {
            String batchRemotePathZip = finalRemoteShellPath + "/" + item;
            log.info("开始处理批次{}的ZIP", batchRemotePathZip);
            //ok文件验证zip文件,通过流的方式
            boolean veriResult = veriyZipOkByStream(batchRemotePathZip);
            if (!veriResult) {
                log.info("{}批次文件OK文件ZIP文件md5验证失败", batchRemotePathZip);
                return;
            }
            //解析验证成功的zip
            List<String> dataLists = analysisZipByStream(batchRemotePathZip);
            if (CollectionUtils.isEmpty(dataLists)) {
                log.info("{}批次ZIP文件解析为空", batchRemotePathZip);
                return;
            }
            //解密zip文件并批量入库
            log.info("{}批次的ZIP数据开始入库", batchRemotePathZip);
            Integer count = decZipMapsIntoSql(dataLists, batchRemotePathZip);
            log.info("{}批次的ZIP数据结束入库,{}入库条数", batchRemotePathZip, count);
            if (count == 0) {
                log.info("{}批次ZIP文件总计零条入库", batchRemotePathZip);
                return;
            }
            //批次入库成功的ZIP文件,log表记录一条数据
            resultInsertLog(count, item);
        });

    }

    @Override
    public void mergeShellentData() {
        List<String> dirs = sftpService.lsRemoteDir(propertiesValue.getRemotePath());
        String maxDir = dirs.stream().max(String::compareTo).get();
        if(entinfoDao.isNull()==null){
            log.info("{}临时表暂无同步数据",maxDir);
            return;
        }


    }

    private List<String> undoneZipFiles(String remoteShellPath, List<String> zipFiles) {
        String[] remoteDirLevel = remoteShellPath.split("/");
        String dateStr = remoteDirLevel[remoteDirLevel.length - 1];
//        String dateStr = remoteShellPath.split("/")[2];
        List<String> categoryList = traceLogDao.findByKeyWords(BatchZipFileLog.KEYWORD.getValue() + dateStr);
        categoryList = categoryList.stream().map(item -> item + ".zip").collect(Collectors.toList());
        zipFiles.removeAll(categoryList);
        return zipFiles;
    }

    private void resultInsertLog(Integer count, String batchRemotePathZip) {
        String batchZip = batchRemotePathZip.replaceAll("\\.zip", "");
//        CHINADAAS_shellent_20200602_SHELLCOMPANY_00
        traceLogDao.addOne(new TraceLog()
                .setLogLevel(BatchZipFileLog.LOGLEVEL.getValue())
                .setCategory(batchZip)
                .setKeywords(BatchZipFileLog.KEYWORD.getValue() + batchZip.split("_")[1])
                .setDetails("批次" + batchZip + "ZIP文件入库成功,入库条数" + count)
                .setLoginTime(new Date()));
    }

    @Override
    public void intoDatabase(String sshelent) {
        List<String> files = sftpService.lsRemoteDir(sshelent);
        log.info("路径:->{}探壳目录文件:->{}", sshelent, files.toString());
        if (StringUtils.isEmpty(files)) {
            log.info(sshelent + "当前路径下没有可执行的探壳文件");
            return;
        }
        List<String> zipFiles = files.stream().filter(file -> file.contains(".zip")).distinct().collect(Collectors.toList());
        String sftpFilePath = sshelent + "/" + zipFiles.get(0);
        log.info("sftp下载路径:->{}", sftpFilePath);
        InputStream zipInputStream = sftpService.getInputStream(sftpFilePath);
//        当前批次zip文件下载到本地
        String zipAbsoluteLocalPath = downZiptoLocal(zipInputStream, sftpFilePath);
//        ok文件验证zip文件
        boolean zipResult = veriyMd5Zip(zipAbsoluteLocalPath, sftpFilePath);
        if (!zipResult) {
            log.info("{}批次文件OK文件ZIP文件md5验证失败", sftpFilePath);
            return;
        }
//        解析zip压缩文件
        List<String> zipDataLists = analysisLocalZip(zipAbsoluteLocalPath);
        if (CollectionUtils.isEmpty(zipDataLists)) {
            log.info("{}批次ZIP文件解析为空", sftpFilePath);
            return;
        }

    }

    private List<String> analysisZipByStream(String batchRemotePathZip) {
        InputStream zipInputStream = sftpService.getInputStream(batchRemotePathZip);
        if (zipInputStream == null) {
            log.info("SFTP获取{}批次ZIP流异常", batchRemotePathZip);
            return null;
        }
        ZipInputStream zipStream = null;
        BufferedReader bufferedReader = null;
        List<String> dataList = new ArrayList<>();
        try {
            zipStream = new ZipInputStream(new BufferedInputStream(zipInputStream));
            int count = 0;
            while ((zipStream.getNextEntry()) != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(zipStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    dataList.add(line);
                    count++;
                }
            }
            log.info("{}批次的总记录数为{}", batchRemotePathZip, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                zipInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataList;

    }

    private boolean  veriyZipOkByStream(String batchRemotePathZip) {
        boolean flag = false;
        InputStream zipInputStream = null;
        InputStream okInputStream = null;
        try {
            zipInputStream = sftpService.getInputStream(batchRemotePathZip);
            okInputStream = sftpService.getInputStream(batchRemotePathZip.replaceAll("\\.zip", ".ok"));
            String okFile = readOkKey(okInputStream);
            String zipFile = DigestUtils.md5Hex(zipInputStream);
            if (okFile.equals(zipFile)) {
                flag = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (okInputStream != null) {
                try {
                    okInputStream.close();
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
        return flag;

    }

    private Integer decZipMapsIntoSql(List<String> zipDataLists, String batchRemotePathZip) {
        List<Entinfo> entList = new ArrayList<>();
        int count = 0;
        zipDataLists.forEach(item -> {
            String[] line = item.split("\u0001");
            if (line.length != 2) {
                log.info("{}该条记录不能根据指定{}分隔符号分割", item, "\u0001");
                return;
            }
            //解密
            String jsonStr = StringsUtil.desCbcDecrypt(line[1]);
            if (StringUtils.isEmpty(jsonStr)) {
                log.info("entId->{}解密异常", line[0]);
                return;
            }
            JSONObject entInfo = (JSONObject) JSON.parse(jsonStr);
            if (entInfo == null) {
                log.info("entId->{}基础信息json解析为空", line[0]);
                return;
            }
            entInfo = (JSONObject) entInfo.get("BASEINFO");
            Entinfo ent = new Entinfo()
                    .setEntId(line[0])
                    .setEntName(entInfo.getString("ENTNAME"))
                    .setEntStatus(entInfo.getString("ENTSTATUS"))
                    .setRegNo(entInfo.getString("REGNO"))
                    .setCreditCode(entInfo.getString("CREDITCODE"))
                    .setEmptyShellContent(line[1])
                    .setCreateTime(new Date());
            entList.add(ent);

        });
        //300条批量插入
        int bathSize = propertiesValue.getBatchSize();
        int index = entList.size() / bathSize;
        for (int i = 0; i <= index; i++) {
            try {
                List<Entinfo> subZipList = entList.stream().skip(i * bathSize).limit(bathSize).collect(Collectors.toList());
                count += entinfoDao.addBatch(subZipList);
            } catch (Exception e) {
                log.info("{}批次的第{}次入库失败", batchRemotePathZip, i);
                e.printStackTrace();
            }
        }
        return count;
    }

    //解析zip文件
    private List<String> analysisLocalZip(String file) {
        List<String> resultList = new ArrayList<>();
        try {
            ZipFile zf = new ZipFile(file);
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            ZipInputStream zin = new ZipInputStream(in);
            ZipEntry ze;
            int count = 0;
            while ((ze = zin.getNextEntry()) != null) {
                if (!ze.isDirectory()) {
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
            log.info("总的记录数-->{}", count);
            zin.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    //ok文件验证zip文件
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

}
