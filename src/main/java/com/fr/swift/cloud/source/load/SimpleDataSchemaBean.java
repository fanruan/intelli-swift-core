package com.fr.swift.cloud.source.load;

import java.util.Map;

/**
 * Created by lyon on 2019/3/7.
 */
public class SimpleDataSchemaBean {

    private String tableName;
    private Map<String, String> fields;
    private String type;
    private String function;

    public SimpleDataSchemaBean(String tableName, Map<String, String> fields, String type) {
        this.tableName = tableName;
        this.fields = fields;
        this.type = type;
    }

    public SimpleDataSchemaBean(String tableName, Map<String, String> fields, String type, String function) {
        this.tableName = tableName;
        this.fields = fields;
        this.type = type;
        this.function = function;
    }
}
