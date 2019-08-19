package com.fr.swift.config.bean;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author yee
 * @date 2018-12-20
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface FineIOConnectorConfig {
    String type();

    String basePath();
}
