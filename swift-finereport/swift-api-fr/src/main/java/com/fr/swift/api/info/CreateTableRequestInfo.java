package com.fr.swift.api.info;

import com.fr.swift.api.json.annotation.ApiJsonProperty;
import com.fr.swift.api.rpc.bean.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-07
 */
public class CreateTableRequestInfo extends TableRequestInfo {
    @ApiJsonProperty(value = "columns", require = true)
    private List<Column> columns = new ArrayList<Column>();

    public CreateTableRequestInfo() {
        super(ApiRequestType.CREATE_TABLE);
    }


    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

}
