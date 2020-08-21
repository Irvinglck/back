package com.icbc.ipa.shellent;

import com.icbc.ipa.shellent.config.PropertiesValue;
import com.icbc.ipa.shellent.dao.EntinfoDao;
import com.icbc.ipa.shellent.dao.TraceLogDao;
import com.icbc.ipa.shellent.enums.BatchZipFileLog;
import com.icbc.ipa.shellent.model.Entinfo;
import com.icbc.ipa.shellent.model.TraceLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootTest
class ShellentSftpDataApplicationTests {

    @Autowired
    private EntinfoDao entinfoDao;
    @Autowired
    private TraceLogDao traceLogDao;
    @Autowired
    private PropertiesValue propertiesValue;

    @Test
    void contextLoads() {
        Entinfo lck = new Entinfo().setEntId("lck").setCreditCode("sdlfkj")
                .setEmptyShellContent("sdfkjs").setEntName("skdfjs").setEntStatus("slkdfjjksad")
                .setRegNo("slkdfjl").setCreateTime(new Date()).setUpdateTime(new Date());
        List<Entinfo> list=new ArrayList<>();
        list.add(lck);
        list.add(lck);
        list.add(lck);
        Entinfo lck1 = new Entinfo().setEntId("lckdddddddddddddddddddddddddddddddddddddddddddddd").setCreditCode("sdlfkj")
                .setEmptyShellContent("sdfkjs").setEntName("skdfjs").setEntStatus("slkdfjjksad")
                .setRegNo("slkdfjl").setCreateTime(new Date()).setUpdateTime(new Date());
        list.add(lck1);
        int i = entinfoDao.addBatch(list);
        System.out.println(i);
    }

    @Test
    void test1(){
        String str="shellent_20200602_SHELLCOMPANY_00.zip";
        String batchZip=str.replaceAll("\\.zip","");
        traceLogDao.addOne(new TraceLog()
                .setLogLevel(BatchZipFileLog.LOGLEVEL.getValue())
                .setKeywords(BatchZipFileLog.KEYWORD.getValue()+batchZip)
                .setDetails("批次"+batchZip+"ZIP文件入库成功,入库条数"+43)
                .setLoginTime(new Date()));
    }
    @Test
    void test2(){
        List<String> zipFiles = Arrays.asList("shellent_20200602_SHELLCOMPANY_00.zip", "shellent_20200602_SHELLCOMPANY_01.zip", "shellent_20200602_SHELLCOMPANY_02.zip", "shellent_20200602_SHELLCOMPANY_03.zip");
        String remote="/data/20200602";
        List<String> strings = undoneZipFiles(remote, zipFiles);
        strings.forEach(System.out::println);
    }

    @Test
    void test3(){
        String batchZip="shellent_20200602_SHELLCOMPANY_00";
        traceLogDao.addOne(new TraceLog()
                .setLogLevel(BatchZipFileLog.LOGLEVEL.getValue())
                .setCategory(batchZip)
                .setKeywords(BatchZipFileLog.KEYWORD.getValue()+batchZip.split("_")[1])
                .setDetails("批次"+batchZip+"ZIP文件入库成功,入库条数"+88)
                .setLoginTime(new Date()));
    }

    @Test
    void test4(){
        Integer aNull = entinfoDao.truncateTable();
        System.out.println(aNull);
    }

    private List<String> undoneZipFiles(String remoteShellPath,List<String> zipFiles) {
        String dateStr=remoteShellPath.split("/")[2];
        List<String> keyWordsList = traceLogDao.findByKeyWords(dateStr);
        AtomicInteger i= new AtomicInteger();
        return zipFiles.stream().filter(item -> {
            //已经成功入库的批次不在重复入库
            if ((i.get()<keyWordsList.size())&&(item.replaceAll("\\.zip","").equals(keyWordsList.get(i.get()).substring(10)))) {
                i.getAndIncrement();
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

}
