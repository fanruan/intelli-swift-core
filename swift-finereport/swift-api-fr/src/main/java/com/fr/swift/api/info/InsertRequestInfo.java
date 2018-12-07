package com.fr.swift.api.info;

import com.fr.swift.api.json.annotation.ApiJsonProperty;
import com.fr.swift.source.Row;

import java.util.List;

/**
 * @author yee
 * @date 2018-12-07
 */
public class InsertRequestInfo extends TableRequestInfo {
    @ApiJsonProperty("selectField")
    private List<String> selectFields;
    @ApiJsonProperty(value = "data", require = true)
    private List<Row> data;

    public InsertRequestInfo() {
        super(ApiRequestType.INSERT);
    }

    public List<String> getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(List<String> selectFields) {
        this.selectFields = selectFields;
    }

    public List<Row> getData() {
        return data;
    }

    public void setData(List<Row> data) {
        this.data = data;
    }
}
