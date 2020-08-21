package com.icbc.ipa.shellent.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
public class PropertiesValue {
    @Value("${chinadaas.shellent.remotePath}")
    private String remotePath;

    @Value("${chinadaas.shellent.batchSize}")
    private Integer batchSize;
}
