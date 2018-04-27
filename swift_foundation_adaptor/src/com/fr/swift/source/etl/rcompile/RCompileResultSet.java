package com.fr.swift.source.etl.rcompile;

import com.finebi.conf.algorithm.DMDataModel;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.datamining.AdapterUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/30 0030 09:54
 */
public class RCompileResultSet implements SwiftResultSet {

    private SwiftMetaData metaData;
    private int rowCursor, rowCount;
    private ListBasedRow row;
    private DMDataModel dataModel;

    public RCompileResultSet(DMDataModel dataModel, SwiftMetaData metaData) {
        this.metaData = metaData;
        this.dataModel = dataModel;
        if(null != dataModel) {
            rowCount = dataModel.getRowSize();
        }
        rowCursor = 0;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean next() {
        if (rowCursor < rowCount) {
            List<Object> list = new ArrayList<Object>();
            for (int i = 0, len = dataModel.getColSize(); i < len; i++) {
                list.add(dataModel.getValue(i, rowCursor));
            }
            // 在swift引擎中得把Date数据转化为long
            List<Object> convert = AdapterUtils.convertSwiftData(list);
            rowCursor++;
            row = new ListBasedRow(convert);
            return true;
        }
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return metaData;
    }

    @Override
    public Row getRowData() {
        return row;
    }

    private int getArrayLength(Object object) {
        if (object instanceof int[]) {
            return ((int[]) object).length;
        } else if (object instanceof double[]) {
            return ((double[]) object).length;
        } else if (object instanceof byte[]) {
            return ((byte[]) object).length;
        } else {
            return ((String[]) object).length;
        }
    }

}
