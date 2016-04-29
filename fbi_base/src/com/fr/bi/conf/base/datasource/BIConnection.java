package com.fr.bi.conf.base.datasource;

/**
 * Created by 小灰灰 on 2016/3/18.
 */
public class BIConnection{
    private String name;

    private String schema;

    public BIConnection(String name, String schema) {
        this.name = name;
        this.schema = schema;
    }

    public String getName() {

        return name;
    }

    public String getSchema() {
        return schema;
    }

}
