package com.finebi.base.data;


import com.finebi.base.data.xml.item.XmlListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew_asa on 2017/10/3.
 */
public class Tables {

    @XmlListItem(clazz = Table.class)
    List<Table> tables = new ArrayList<Table>();

    String tags;


    List<String> lists;

    public Tables() {

    }

    public Tables(List<Table> tables) {

        this.tables = tables;
    }

    public List<Table> getTables() {

        return tables;
    }

    public void setTables(List<Table> tables) {

        this.tables = tables;
    }

    public void addTable(Table table) {

        tables.add(table);
    }


}
