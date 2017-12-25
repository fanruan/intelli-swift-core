package com.finebi.base.data;

import com.finebi.base.data.xml.item.XmlItem;
import com.finebi.base.data.xml.item.XmlObjectItem;

/**
 * Created by Kary on 2017/11/2.
 */
public class TableDemo {
    //    @XmlItem
//    BIWidget widget;
//
//    public BIWidget getWidget() {
//        return widget;
//    }
//
//    public void setWidget(BIWidget widget) {
//        this.widget = widget;
//    }
    @XmlObjectItem(clazz = TableWidget.class)
    TableWidget widget;

    @XmlItem
    String tableDemoName;

    public TableWidget getWidget() {
        return widget;
    }

    public void setTableDemoName(String tableDemoName) {
        this.tableDemoName = tableDemoName;
    }

    public String getTableDemoName() {
        return tableDemoName;
    }

    public void setWidget(TableWidget widget) {
        this.widget = widget;
    }
}
