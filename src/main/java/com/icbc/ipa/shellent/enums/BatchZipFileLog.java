package com.icbc.ipa.shellent.enums;

import lombok.Getter;

public enum BatchZipFileLog {
    LOGLEVEL("Information"),
    KEYWORD("BatShellent_");

    BatchZipFileLog(String value) {
        this.value=value;

    }
    @Getter
    private String value;
}


