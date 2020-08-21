package com.icbc.ipa.shellent.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class Entinfo {

    private static final  long serialVersionUID=1L;

    /**
     * 流水号
     */
    private Integer Id;

    private String EntId;//企业id

    private String EntName;//企业名称

    private String CreditCode;

    private String RegNo;

    private  String EntStatus;

    private String EmptyShellContent;

    private Date CreateTime;

    private Date UpdateTime;

}
