package com.finebi.base.data;


import com.finebi.base.data.xml.item.XmlItem;

/**
 * Created by andrew_asa on 2017/10/11.
 */
    public class TableWidget extends AbstractBiWidget {

    @XmlItem
    String tableName;

    public TableWidget() {

    }

    public String getTableName() {

        return tableName;
    }

    public void setTableName(String tableName) {

        this.tableName = tableName;
    }
}
