package com.fr.swift.source.etl.join;

/**
 * Created by Handsome on 2017/12/8 0008 11:39
 */
public class JoinColumn {


    public JoinColumn(String name, boolean isLeft, String columnName) {
        this.name = name;
        this.isLeft = isLeft;
        this.columnName = columnName;
    }

    public JoinColumn() {

    }
	

    public String getName() {
        return name;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public String getColumnName() {
        return columnName;
    }

    //etl之后的字段名
    private String name;

    private boolean isLeft;

    //父表字段名
    private String columnName;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return null;
    }
}
