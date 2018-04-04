package com.fr.swift.source.etl.datamining;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.timeseries.HoltWintersBean;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2018/3/13 4:43
 */
public class DataMiningResultSet implements SwiftResultSet {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(DataMiningResultSet.class);
    private HoltWintersBean algorithmBean;
    private List<Segment> segmentList;
    private ListBasedRow listBasedRow;
    private SwiftMetaData selfMetaData;
    private SwiftMetaData baseMetaData;
    private int rowCursor = 0;
    private List<List<Object>> predictTableData = new ArrayList<List<Object>>();
    private boolean isFirst = true;

    DataMiningResultSet(AlgorithmBean algorithmBean, SwiftMetaData selfMetaData, SwiftMetaData baseMetaData, List<Segment> segmentList) {
        this.algorithmBean = (HoltWintersBean) algorithmBean;
        this.segmentList = segmentList;
        this.selfMetaData = selfMetaData;
        this.baseMetaData = baseMetaData;
    }

    private Object getCellValueFromSegment(Segment segment, String columnName, int rowIndex) {
        Column column = segment.getColumn(new ColumnKey(columnName));
        DictionaryEncodedColumn dicColumn = column.getDictionaryEncodedColumn();
        return dicColumn.getValue(dicColumn.getIndexByRow(rowIndex));
    }

    private void init() throws Exception {

//        Segment segment = segmentList.get(0);
//        DMRowMetaData inputMetaData = new DMRowMetaData();
//
//        List<List<Object>> inputData = new ArrayList<List<Object>>();
//        // 初始化数据
//        for (int i = 0; i < segment.getRowCount(); i++) {
//            List<Object> row = new ArrayList<Object>();
//            for (int j = 0; j < baseMetaData.getColumnCount(); j++) {
//                SwiftMetaDataColumn column = baseMetaData.getColumn(j + 1);
//
//                // 初始化metaData
//                if (i == 0) {
//                    inputMetaData.addColMeta(new DMColMetaData(column.getName(), DMType.fromSwiftInt(column.getType())));
//                }
//
//                Object cellValue = getCellValueFromSegment(segment, column.getName(), i);
//                row.add(cellValue);
//            }
//            inputData.add(row);
//        }
//
//
//        DMDataModel dmDataModel = new DMDataModel(inputData, inputMetaData);
//
//        DMAbstractAlgorithm algorithm = DMAlgorithmFactory.create(algorithmBean.getAlgorithmName());
//        algorithm.init(algorithmBean, dmDataModel);
//        DMDataModel outputData = algorithm.run();
 //       predictTableData = outputData.getData();
    }

    @Override
    public void close() {
    }

    @Override
    public boolean next() throws SQLException {
        try {
            if(isFirst){
                isFirst = false;
                init();
            }
            List<Object> row;
            int rowCount = predictTableData.size();

            if (rowCursor < rowCount) {
                row = predictTableData.get(rowCursor);
                rowCursor++;
                setRowValue(new ListBasedRow(row));
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
