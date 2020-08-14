package com.icbc.ipa.shellent.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Entinfo {

    private static final  long serialVersionUID=1L;

    /**
     * 流水号
     */
    private Long id;

    private String entinfoId;//企业id

    private String entName;//企业名称

    private String creditCode;

    private String regno;

    private String baseData;

    private String createTime;

    private String updateTime;

}
