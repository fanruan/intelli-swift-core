package com.fr.swift.jdbc.info;

import com.fr.swift.jdbc.json.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018/11/20
 */
public class Order {
    @JsonProperty("column")
    private String column;
    @JsonProperty("asc")
    private boolean asc;

    private Order(String column, boolean asc) {
        this.column = column;
        this.asc = asc;
    }

    public static Order asc(String column) {
        return new Order(column, true);
    }

    public static Order desc(String column) {
        return new Order(column, false);
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }
}
