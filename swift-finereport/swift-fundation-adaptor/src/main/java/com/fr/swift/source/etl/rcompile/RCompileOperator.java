package com.fr.swift.source.etl.rcompile;

import com.finebi.base.stable.StableManager;
import com.finebi.conf.algorithm.DMColMetaData;
import com.finebi.conf.algorithm.DMDataModel;
import com.finebi.conf.algorithm.DMRowMetaData;
import com.finebi.conf.algorithm.DMType;
import com.finebi.conf.algorithm.common.DMLogEntityImp;
import com.finebi.conf.algorithm.common.DMLogType;
import com.finebi.conf.algorithm.rcompile.RCacheElement;
import com.finebi.conf.algorithm.rcompile.RCacheStore;
import com.finebi.conf.algorithm.rcompile.RExecute;
import com.finebi.conf.internalimp.service.datamining.RConnectionFactory;
import com.finebi.conf.service.datamining.DMCommonLogService;
import com.finebi.conf.structure.datamining.DMLogEntity;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
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
    private DMDataModel dataModel;
    @CoreField
    private String commands;
    @CoreField
    private String uuid;
    @CoreField
    private RConnection conn;
    @CoreField
    private Segment[] segments;

    public RCompileOperator(String commands, String uuid, Segment[] segments) {
        this.commands = commands;
        this.uuid = uuid;
        this.segments = segments;
    }

    public DMDataModel getDataModel() {
        return dataModel;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {

        List<Object> cacheList = new ArrayList<Object>();
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        if (commands.trim().isEmpty()) {
            return columnList;
        }
        // 尝试从缓存中读取
        RCacheElement rCacheElement = RCacheStore.INSTANCE.get(uuid);
//        if (null != rCacheElement) {
//            cacheList = rCacheElement.<List>getObj();
//            dataModel = (DMDataModel) cacheList.get(1);
//            return (List<SwiftMetaDataColumn>) cacheList.get(0);
//        }
        try {
            // 读取不到计算
            try {
                conn = RConnectionFactory.getRConnection();
                if (null == conn) {
                    return columnList;
                }
            } catch (Exception e) {
                DMCommonLogService service = StableManager.getContext().getObject("DMCommonLogServiceImpl");
                DMLogEntity logEntity = DMLogEntityImp.create();
                logEntity.setUuid(uuid);
                logEntity.setErrorType(DMLogType.RCOMPILE.R_CONNECT_ERROR);
                logEntity.writeLog("Cant not connect R serve!");
                service.setLog(logEntity);
                return columnList;
            }
            // 计算R语言命令
            try {
                assign(metaDatas);
                eval(metaDatas, columnList);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                return columnList;
            }
            cacheList.add(columnList);
            cacheList.add(dataModel);
            // 保存到缓存中
            RCacheStore.INSTANCE.put(new RCacheElement(uuid, cacheList));
        } finally {
            if(null != conn) {
                conn.close();
            }
        }
        return columnList;
    }

    private void eval(SwiftMetaData[] swiftMetaData, List<SwiftMetaDataColumn> columnList) throws Exception {
        if (null != this.commands) {
            dataModel = RExecute.process(conn, commands, uuid);
        } else {
            SwiftMetaData metaData = swiftMetaData[0];
            int count = metaData.getColumnCount();
            try {
                for (int i = 0; i < count; i++) {
                    String name = metaData.getColumnName(i + 1);
                    int type = metaData.getColumnType(i + 1);
                    columnList.add(new MetaDataColumnBean(name, type));
                }
            } catch (Exception e) {
                LOGGER.error("falied to get metaData's column", e);
            }
            dataModel = RExecute.getPreviousDataModel(conn);
        }
        DMRowMetaData rowMetaData = dataModel.getRowMetaData();
        for (DMColMetaData colMetaData : rowMetaData.getColMetas()) {
            columnList.add(new MetaDataColumnBean(colMetaData.getColName(), colMetaData.getColType().toSwiftInt()));
        }
    }

    private void assign(SwiftMetaData[] metaData) throws Exception {
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