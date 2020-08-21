package com.icbc.ipa.shellent.dao;

import com.icbc.ipa.shellent.model.Entinfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EntinfoDao {

    int addOne(Entinfo entinfo);

    int addBatch(@Param("entInfoList") List<Entinfo> entinfoList);

    Integer isNull();

    int update(Entinfo entinfo);

    int delete(Entinfo entinfo);

    Entinfo findOne(String EenId);

    int truncateTable();

}
