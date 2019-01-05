package com.fr.swift.api.info;

import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.base.json.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-07
 */
public class CreateTableRequestInfo extends TableRequestInfo {
    @JsonProperty(value = "columns")
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

    @Override
    public ApiInvocation accept(ApiRequestParserVisitor visitor) {
        return visitor.visit(this);
    }
}
