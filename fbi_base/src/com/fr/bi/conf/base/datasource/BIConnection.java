package com.fr.bi.conf.base.datasource;

import com.fr.fs.control.UserControl;

import java.io.Serializable;

/**
 * Created by 小灰灰 on 2016/3/18.
 */
public class BIConnection implements Serializable {
    private static final long serialVersionUID = 7790477263598447784L;
    private String name;
    private String schema;
    private long createBy = UserControl.getInstance().getSuperManagerID();  //默认admin
    private long initTime;

    public BIConnection(String name, String schema) {
        this.name = name;
        this.schema = schema;
    }

    public BIConnection(String name, String schema, long createBy, long initTime) {
        this.name = name;
        this.schema = schema;
        this.createBy = createBy;
        this.initTime = initTime;
    }

    public String getConnectionName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(long createBy) {
        this.createBy = createBy;
    }

    public long getInitTime() {
        return initTime;
    }

    public void setInitTime(long initTime) {
        this.initTime = initTime;
    }
}
