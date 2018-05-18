package com.fr.swift.source.etl.datamining;

import com.finebi.base.stable.StableManager;
import com.finebi.conf.algorithm.DMAbstractAlgorithm;
import com.finebi.conf.algorithm.DMAlgorithmFactory;
import com.finebi.conf.algorithm.DMColMetaData;
import com.finebi.conf.algorithm.DMDataModel;
import com.finebi.conf.algorithm.DMModel;
import com.finebi.conf.algorithm.DMRowMetaData;
import com.finebi.conf.algorithm.DMType;
import com.finebi.conf.algorithm.EmptyAlgorithm;
import com.finebi.conf.algorithm.common.DMLogEntityImp;
import com.finebi.conf.algorithm.common.DMLogType;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.finebi.conf.service.datamining.DMCommonLogService;
import com.finebi.conf.structure.datamining.DMLogEntity;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2018/3/13 4:43
 */
public class DataMiningResultSet implements SwiftResultSet {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(DataMiningResultSet.class);
    private AlgorithmBean algorithmBean;
    private Segment[] basedSegment;
    private ListBasedRow listBasedRow;
    private SwiftMetaData selfMetaData;
    private SwiftMetaData baseMetaData;
    private int rowCursor = 0;
    private List<List<Object>> predictTableData = new ArrayList<List<Object>>();
    private boolean isFirst = true;

    DataMiningResultSet(AlgorithmBean algorithmBean, SwiftMetaData selfMetaData, SwiftMetaData baseMetaData, Segment[] basedSegment) {
        this.algorithmBean = algorithmBean;
        this.basedSegment = basedSegment;
        this.selfMetaData = selfMetaData;
        this.baseMetaData = baseMetaData;
    }

    private Object getCellValueFromSegment(Segment segment, String columnName, int rowIndex) {
        Column column = segment.getColumn(new ColumnKey(columnName));
        DictionaryEncodedColumn dicColumn = column.getDictionaryEncodedColumn();
        return dicColumn.getValue(dicColumn.getIndexByRow(rowIndex));
    }

    private void init() throws Exception {

        Segment segment = basedSegment[0];
        DMRowMetaData inputMetaData = new DMRowMetaData();

        List<List<Object>> inputData = new ArrayList<List<Object>>();
        // 初始化数据
        for (int i = 0; i < segment.getRowCount(); i++) {
            List<Object> row = new ArrayList<Object>();
            for (int j = 0; j < baseMetaData.getColumnCount(); j++) {
                SwiftMetaDataColumn column = baseMetaData.getColumn(j + 1);

                // 初始化metaData
                if (i == 0) {
                    inputMetaData.addColMeta(new DMColMetaData(column.getName(), DMType.fromSwiftInt(column.getType())));
                }

                Object cellValue = getCellValueFromSegment(segment, column.getName(), i);
                row.add(cellValue);
            }
            inputData.add(row);
        }


        DMDataModel inputDataModel = new DMDataModel(inputData, inputMetaData);

        DMAbstractAlgorithm algorithm = DMAlgorithmFactory.create(algorithmBean.getAlgorithmName());
        DMModel outputData;
        // 如果为Empty则返回上一个表的数据
        if (algorithm instanceof EmptyAlgorithm) {
            outputData = inputDataModel;
        } else {
            algorithm.init(algorithmBean, inputDataModel);
            outputData = algorithm.run();
        }
        predictTableData = outputData.getData();
    }

    @Override
    public void close() {
    }

    @Override
    public boolean next() {
        try {
            if (isFirst) {
                isFirst = false;
                try {
                    init();
                } catch (Exception e) {
                    // 逻辑：检查Log里面该uuid是否已经被设置了log，如果没有设置通用log返回给前端。
                    DMCommonLogService logService = StableManager.getContext().getObject("DMCommonLogServiceImpl");
                    String uuid = algorithmBean.getUuid();
                    DMLogEntity entity = logService.getLog(uuid);
//                    if (null == entity /*&& !StringUtils.isEmpty(uuid)*/) {
                    String log = e.getMessage() == null ? e.toString() : e.getMessage();
                    DMLogEntityImp newLog = new DMLogEntityImp(log, uuid, DMLogType.GLOBAL_CATCH_ERROR);
                    logService.setLog(newLog);
//                    }
                    LOGGER.error(e.getMessage(), e);
                }
            }
            List<Object> row;
            int rowCount = predictTableData.size();

            if (rowCursor < rowCount) {
                row = predictTableData.get(rowCursor);
                // 在swift引擎中得把Date数据转化为long
                List<Object> convert = AdapterUtils.convertSwiftData(row);
                rowCursor++;
                setRowValue(new ListBasedRow(convert));
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    private void setRowValue(ListBasedRow row) {
        this.listBasedRow = row;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return selfMetaData;
    }

    @Override
    public Row getRowData() {
        return getRowValue();
    }

    private ListBasedRow getRowValue() {
        return this.listBasedRow;
    }
}
