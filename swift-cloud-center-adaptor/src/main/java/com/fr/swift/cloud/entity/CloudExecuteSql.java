package com.fr.swift.cloud.entity;

import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Table;

/**
 * @author yee
 * @date 2018-12-21
 */
@Entity
@Table(name = "cloud_execute_sql")
public class CloudExecuteSql {
    @Column
    private String executeId;
    @Column(name = "dsname")
    private String dataSourceName;
    @Column
    private long sqlTime;
    @Column
    private long rows;
    @Column
    private long columns;

    public String getExecuteId() {
        return executeId;
    }

    public void setExecuteId(String executeId) {
        this.executeId = executeId;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public long getSqlTime() {
        return sqlTime;
    }

    public void setSqlTime(long sqlTime) {
        this.sqlTime = sqlTime;
    }

    public long getRows() {
        return rows;
    }

    public void setRows(long rows) {
        this.rows = rows;
    }

    public long getColumns() {
        return columns;
    }

    public void setColumns(long columns) {
        this.columns = columns;
    }
}
