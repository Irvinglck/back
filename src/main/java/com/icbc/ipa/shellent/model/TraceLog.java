package com.icbc.ipa.shellent.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@Accessors(chain = true)
public class TraceLog {

    private int Id;

    private String LogLevel;

    private String Category;

    private String Keywords;

    private String Details;

    private Date LoginTime;
}
