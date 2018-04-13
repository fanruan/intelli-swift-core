package com.fr.swift.source.etl.datamining.rcompile;

import com.fr.swift.adaptor.transformer.ColumnTypeAdaptor;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.*;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;
import org.rosuda.REngine.Rserve.RConnection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Handsome on 2018/3/29 0029 15:59
 */
public class RCompileOperator extends AbstractOperator {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RCompileOperator.class);

    @CoreField
    private List dataList;
    @CoreField
    private List<SwiftMetaDataColumn> columnList;
    @CoreField
    private String commands;
    @CoreField
    private String tableName;
    @CoreField
    private RConnection conn;
    @CoreField
    private Segment[] segment;

    public RCompileOperator(String commands, RConnection conn, String tableName, Segment[] segment) {
        this.commands = commands;
        this.conn = conn;
        this.tableName = tableName;
        this.segment = segment;
    }

    public List getDataList() {
        return dataList;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        columnList = new ArrayList<SwiftMetaDataColumn>();
        SwiftMetaData metaData = metaDatas[0];
        int count = 0;
        ColumnKey[] columnKeys = null;
        try {
            count = metaData.getColumnCount();
            columnKeys = new ColumnKey[count];
            for(int i = 0; i < count; i ++) {
                String name = metaData.getColumnName(i + 1);
                int type = metaData.getColumnType(i + 1);
                columnKeys[i] = new ColumnKey(name);
                columnList.add(new MetaDataColumn(name, name, type, ColumnTypeUtils.MAX_LONG_COLUMN_SIZE,
                        0, name));
            }
        } catch(Exception e) {
            LOGGER.error("falied to get metaData's column", e);
        }
        init(columnKeys);
        return columnList;
    }

    private void init(ColumnKey[] columnKeys) {
        RExecute.processAssignment(conn, segment, columnKeys, tableName);
        if(null == commands) {
            dataList = RExecute.getPreviousDataList(conn);
        } else {
            dataList = RExecute.process(conn, commands, tableName);
        }
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
