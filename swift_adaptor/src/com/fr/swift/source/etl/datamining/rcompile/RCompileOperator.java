package com.fr.swift.source.etl.datamining.rcompile;

import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.transformer.ColumnTypeAdaptor;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.*;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;
import org.rosuda.REngine.Rserve.RConnection;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Handsome on 2018/3/29 0029 15:59
 */
public class RCompileOperator extends AbstractOperator {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RCompileOperator.class);

    @CoreField
    private static List dataList;
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
        if(null != this.commands) {
            List list = RExecute.process(conn, commands, tableName);
            dataList = null == list ? dataList : list;
        }
    }

    public List getDataList() {
        return dataList;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        columnList = new ArrayList<SwiftMetaDataColumn>();
        if (null == commands) {
            SwiftMetaData metaData = metaDatas[0];
            int count = 0;
            ColumnKey[] columnKeys = null;
            try {
                count = metaData.getColumnCount();
                columnKeys = new ColumnKey[count];
                for(int i = 0; i < count; i ++) {
                    String name = metaData.getColumnName(i + 1);
                    int type = metaData.getColumnType(i + 1);
                    int scale = metaData.getScale(i + 1);
                    columnKeys[i] = new ColumnKey(name);
                    columnList.add(new MetaDataColumn(name, name, type, ColumnTypeUtils.MAX_LONG_COLUMN_SIZE,
                            scale, name));
                }
            } catch(Exception e) {
                LOGGER.error("falied to get metaData's column", e);
            }
            init(columnKeys);
        } else {
            SwiftMetaData metaData = metaDatas[0];
            try {
                for(int i = 0; i < metaData.getColumnCount(); i ++) {
                    String name = metaData.getColumnName(i + 1);
                    int type = metaData.getColumnType(i + 1);
                    int scale = metaData.getScale(i + 1);
                    columnList.add(new MetaDataColumn(name, name, type, ColumnTypeUtils.MAX_LONG_COLUMN_SIZE,
                            scale, name));
                }
            } catch(Exception e) {
                LOGGER.error("falied to get metaData's column", e);
            }
            String[] columns = (String[]) dataList.get(0);
            int[] columnTypes = (int[]) dataList.get(1);
            for(int i = 0; i < columns.length; i ++) {
                if(!findColumnName(columns[0], metaData)) {
                    int scale = 0;
                    if(columnTypes[i] == Types.DOUBLE) {
                        scale = 15;
                    }
                    columnList.add(new MetaDataColumn(columns[0], columns[0], columnTypes[i],
                            ColumnTypeUtils.MAX_LONG_COLUMN_SIZE, scale, MD5Utils.getMD5String(new String[]{(columns[0])})));
                }
            }
        }
        return columnList;
    }

    private boolean findColumnName(String columnName, SwiftMetaData metaData) {
        try {
            for(int i = 0; i < metaData.getColumnCount(); i ++) {
                String name = metaData.getColumnName(i + 1);
                if(StringUtils.equals(columnName, name)) {
                    return true;
                }
            }
        } catch(SwiftMetaDataException e) {
            LOGGER.error("failed to find field: " + columnName);
        }
        return false;
    }

    private void init(ColumnKey[] columnKeys) {
        RExecute.processAssignment(conn, segment, columnKeys, tableName);
        dataList = RExecute.getPreviousDataList(conn);
        /*if(null == commands) {
            RExecute.processAssignment(conn, segment, columnKeys, tableName);
            dataList = RExecute.getPreviousDataList(conn);
        } else {
            List list = RExecute.process(conn, commands, tableName);
            dataList = null == list ? dataList : list;
        }*/
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
