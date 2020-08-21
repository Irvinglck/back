package com.icbc.ipa.shellent.dao;

import com.icbc.ipa.shellent.model.TraceLog;

import java.util.List;

public interface TraceLogDao {

    int addOne(TraceLog traceLog);

    TraceLog findOne(TraceLog traceLog);

    List<String> findByKeyWords(String Keywords);

}
