package com.finebi.cube.tools;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.source.ITableSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wuk on 16/5/17.
 */
public class BINationDataFactory {
    public static ITableSource createTablePerson(){
        BINationDataSource source=new BINationDataSource();
        List<DBField> columns=new ArrayList<DBField>();
        columns.add(new DBField("persons","id", DBConstant.CLASS.LONG,255));
        columns.add(new DBField("persons","name", DBConstant.CLASS.STRING,255));
        columns.add(new DBField("persons","nationId", DBConstant.CLASS.LONG,255));

        List<Long> id = new ArrayList<Long>();
        id.add(1L);
        id.add(2L);
        id.add(3L);
        List<String> name = new ArrayList<String>();
        name.add("nameA");
        name.add("nameB");
        name.add("nameA");

        List<Long> nationId = new ArrayList<Long>();
        nationId.add(1l);
        nationId.add(2l);
        nationId.add(3l);

        Map<Integer, List> content = new HashMap<Integer, List>();
        content.put(0,id);
        content.put(1,name);
        content.put(2,nationId);

        source.setFieldList(columns);
        source.setRowCount(id.size());
        source.setContents(content);
        source.setSourceID("persons");
        return source;
    }

    public static ITableSource createTableNation(){
        BINationDataSource source=new BINationDataSource();
        List<DBField> columns=new ArrayList<DBField>();
        columns.add(new DBField("nations","id", DBConstant.CLASS.LONG,255));
        columns.add(new DBField("nations","name", DBConstant.CLASS.STRING,10));

        List<Long> id = new ArrayList<Long>();
        id.add(1L);
        id.add(2L);
        List<String> name = new ArrayList<String>();
        name.add("China");
        name.add("US");
        source.setRowCount(id.size());

        Map<Integer, List> content = new HashMap<Integer, List>();
        content.put(0,id);
        content.put(1,name);

        source.setFieldList(columns);
        source.setContents(content);
        source.setSourceID("nations");
        return source;
    }
}
