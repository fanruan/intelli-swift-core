package com.fr.swift.api.rpc;

import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.QueryBean;

import java.util.List;
import java.util.UUID;

/**
 * @author yee
 * @date 2018/7/24
 */
public class SimpleDetailQueryBean {

    private String table;
    private List<String> columns;

    public SimpleDetailQueryBean(String table, List<String> columns) {
        this.table = table;
        this.columns = columns;
    }

    public SimpleDetailQueryBean() {
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public QueryBean toQueryBean() throws Exception {
        return QueryBeanFactory.create(getQueryString());
    }

    public String getQueryString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{\n" +
                "  \"queryId\": \"").append(UUID.randomUUID()).append("\",\n" +
                "  \"queryType\": \"DETAIL\",\n" +
                "  \"querySegments\": null,\n");
        buffer.append("  \"tableName\": \"").append(table).append("\",\n" +
                "  \"filterInfoBean\": null,\n" +
                "  \"dimensionBeans\": [\n");
        for (String column : columns) {
            buffer.append("    {\n" +
                    "      \"column\": \"").append(column).append("\",\n" +
                    "      \"name\": \"").append(column).append("\",\n" +
                    "      \"groupBean\": {\n" +
                    "        \"type\": \"NONE\"\n" +
                    "      },\n" +
                    "      \"dimensionType\": \"DETAIL\"\n" +
                    "    },");
        }
        buffer.setLength(buffer.length() - 1);
        buffer.append("  ],\n" +
                "  \"sortBeans\": []\n" +
                "}");
        return buffer.toString();
    }
}
