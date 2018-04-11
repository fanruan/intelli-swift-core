package com.fr.swift.source.etl.datamining.rcompile;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;
import org.rosuda.REngine.Rserve.RConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Handsome on 2018/3/29 0029 15:59
 */
public class RCompileOperator extends AbstractOperator {

    private String[] columns;
    private int[] columnTypes;
    private static List dataList;
    private List<SwiftMetaDataColumn> columnList;
    private String commands;
    private boolean needExecute;
    private RConnection conn;
    private String tableName;
    private Segment[] segments;
    private boolean cancelPreviousStep;
    private boolean init;

    public RCompileOperator(String commands, boolean needExecute, RConnection conn,
                            String tableName, Segment[] segments, int[] columnTypes,
                            String[] columns, boolean cancelPreviousStep, boolean init) {
        if(!init) {
            this.columns = (String[]) dataList.get(0);
            this.columnTypes = (int[]) dataList.get(1);
        } else {
            this.columns = columns;
            this.columnTypes = columnTypes;
        }
        this.conn = conn;
        this.init = init;
        this.cancelPreviousStep = cancelPreviousStep;
        this.commands = commands;
        this.needExecute = needExecute;
        this.tableName = tableName;
        this.segments = segments;
        init(commands, init, tableName, segments, needExecute);
    }

    private void init(String commands, boolean init, String tableName,
                      Segment[] segments, boolean needExecute) {
        if(!init) {
            if(needExecute) {
                List list = RExecute.process(conn, commands, tableName);
                if(null != list) {
                    dataList = list;
                    this.columns = (String[]) dataList.get(0);
                    this.columnTypes = (int[]) dataList.get(1);
                }
            } else {
                if(cancelPreviousStep) {
                    List list = RExecute.cancelPreviousStep(conn, tableName);
                    if(null != list) {
                        dataList = list;
                        this.columns = (String[]) dataList.get(0);
                        this.columnTypes = (int[]) dataList.get(1);
                    }
                }
            }
        } else {
            ColumnKey[] columnKeys = new ColumnKey[columns.length];
            for(int i = 0; i < columns.length; i++) {
                columnKeys[i] = new ColumnKey(columns[i]);
            }
            RExecute.processAssignment(conn, segments, columnKeys, tableName);
            dataList = RExecute.getPreviousDataList(conn);
        }
    }


    public String[] getColumns() {
        return columns;
    }

    public int[] getColumnTypes() {
        return columnTypes;
    }

    public List getDataList() {
        return dataList;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        columnList = new ArrayList<SwiftMetaDataColumn>();
        if(!init) {
            columns = (String[]) dataList.get(0);
            columnTypes = (int[]) dataList.get(1);
            for(int i = 0; i < columns.length; i++) {
                columnList.add(new MetaDataColumn(columns[i], columns[i], columnTypes[i],
                        ColumnTypeUtils.MAX_LONG_COLUMN_SIZE, 0, fetchObjectCore().getValue()));
            }
        } else {
            for(int i = 0; i < columns.length; i++) {
                columnList.add(new MetaDataColumn(columns[i], columns[i], columnTypes[i],
                        ColumnTypeUtils.MAX_LONG_COLUMN_SIZE, 0, fetchObjectCore().getValue()));
            }
        }
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.R_COMPILE;
    }

    @Override
    public List<String> getNewAddedName() {
        List<String> list = new ArrayList<String>(columnList.size());
        Iterator<SwiftMetaDataColumn> iterator = columnList.iterator();
        while(iterator.hasNext()) {
            SwiftMetaDataColumn column = iterator.next();
            list.add(column.getName());
        }
        return list;
    }
}
