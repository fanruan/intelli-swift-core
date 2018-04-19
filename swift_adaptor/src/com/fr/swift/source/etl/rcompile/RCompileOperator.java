package com.fr.swift.source.etl.rcompile;

import com.finebi.conf.algorithm.DMColMetaData;
import com.finebi.conf.algorithm.DMDataModel;
import com.finebi.conf.algorithm.DMRowMetaData;
import com.finebi.conf.algorithm.DMType;
import com.finebi.conf.algorithm.rcompile.RExecute;
import com.fr.stable.StringUtils;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;
import org.rosuda.REngine.Rserve.RConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/29 0029 15:59
 */
public class RCompileOperator extends AbstractOperator {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RCompileOperator.class);

    @CoreField
    private static DMDataModel dataModel;
    @CoreField
    private List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
    @CoreField
    private String commands;
    @CoreField
    private String uuid;
    @CoreField
    private RConnection conn;
    @CoreField
    private Segment[] segments;

    public RCompileOperator(String commands, RConnection conn, String uuid, Segment[] segments) {
        this.commands = commands;
        this.conn = conn;
        this.uuid = uuid;
        this.segments = segments;
    }

    public DMDataModel getDataModel() {
        return dataModel;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {

        try {
            init(metaDatas);
            if (null != this.commands) {
                dataModel = RExecute.process(conn, commands, uuid);
            } else {
                SwiftMetaData metaData = metaDatas[0];
                int count = 0;
                ColumnKey[] columnKeys = null;
                try {
                    count = metaData.getColumnCount();
                    columnKeys = new ColumnKey[count];
                    for (int i = 0; i < count; i++) {
                        String name = metaData.getColumnName(i + 1);
                        int type = metaData.getColumnType(i + 1);
                        int scale = metaData.getScale(i + 1);
                        columnKeys[i] = new ColumnKey(name);
                        columnList.add(new MetaDataColumn(name, name, type, ColumnTypeUtils.MAX_LONG_COLUMN_SIZE,
                                scale, name));
                    }
                } catch (Exception e) {
                    LOGGER.error("falied to get metaData's column", e);
                }
                dataModel = RExecute.getPreviousDataModel(conn);
            }
            DMRowMetaData rowMetaData = dataModel.getRowMetaData();
            for (DMColMetaData colMetaData : rowMetaData.getColMetas()) {
                columnList.add(new MetaDataColumn(colMetaData.getColName(), colMetaData.getColType().toSwiftInt()));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return columnList;
    }

    private boolean findColumnName(String columnName, SwiftMetaData metaData) {
        try {
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                String name = metaData.getColumnName(i + 1);
                if (StringUtils.equals(columnName, name)) {
                    return true;
                }
            }
        } catch (SwiftMetaDataException e) {
            LOGGER.error("failed to find field: " + columnName);
        }
        return false;
    }

    private void init(SwiftMetaData[] metaData) throws Exception {
        DMDataModel dataModel = new DMDataModel();
        DMRowMetaData inputMetaData = new DMRowMetaData();
        SwiftMetaData baseMetaData = metaData[0];
        List<List<Object>> inputData = new ArrayList<List<Object>>();
        for (Segment segment : segments) {
            for (int i = 0; i < segment.getRowCount(); i++) {
                List<Object> row = new ArrayList<Object>();
                for (int j = 0; j < baseMetaData.getColumnCount(); j++) {
                    SwiftMetaDataColumn column = baseMetaData.getColumn(j + 1);
                    if (i == 0) {
                        inputMetaData.addColMeta(new DMColMetaData(column.getName(), DMType.fromSwiftInt(column.getType())));
                    }
                    Object cellValue = getCellValueFromSegment(segment, column.getName(), i);
                    row.add(cellValue);
                }
                inputData.add(row);
            }
        }
        dataModel.setRowMetaData(inputMetaData);
        dataModel.setData(inputData);
        RExecute.processAssignment(conn, dataModel);
    }

    private Object getCellValueFromSegment(Segment segment, String columnName, int rowIndex) {
        Column column = segment.getColumn(new ColumnKey(columnName));
        DictionaryEncodedColumn dicColumn = column.getDictionaryEncodedColumn();
        return dicColumn.getValue(dicColumn.getIndexByRow(rowIndex));
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.R_COMPILE;
    }

    @Override
    public List<String> getNewAddedName() {
        return null;
    }
}
