package com.fr.swift.source.etl.datamining.timeseries;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.finebi.conf.rlang.RDataModel;
import com.finebi.conf.rlang.algorithm.timeseries.RTimeSeriesAbstract;
import com.fr.json.JSONArray;
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
import com.fr.swift.source.etl.datamining.timeseries.arima.ArimaResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2018/3/13 4:43
 */
public abstract class AbstractTimeSeriesResultSet implements SwiftResultSet {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ArimaResultSet.class);
    private AlgorithmBean algorithmBean;
    private List<Segment> segmentList;
    private ListBasedRow listBasedRow = null;
    private SwiftMetaData selfMetaData = null;
    private SwiftMetaData baseMetaData = null;
    private RDataModel dataModel = null;
    private int rowCursor = 0;
    private boolean isFirst = true;
    protected RTimeSeriesAbstract timeSeriesAlgorithm;

    public AbstractTimeSeriesResultSet(AlgorithmBean algorithmBean, SwiftMetaData selfMetaData, SwiftMetaData baseMetaData, List<Segment> segmentList) throws Exception {
        this.algorithmBean = algorithmBean;
        this.segmentList = segmentList;
        this.selfMetaData = selfMetaData;
        this.baseMetaData = baseMetaData;
    }

    protected abstract void setExtraConfiguration();

    private void init() throws Exception {
        // 初始化参数
        timeSeriesAlgorithm.setTimeName("field2");
        timeSeriesAlgorithm.setForecastName("field1");
        timeSeriesAlgorithm.setStep(15);
        timeSeriesAlgorithm.setInterval(RTimeSeriesAbstract.TimeCycle.MONTH);
        timeSeriesAlgorithm.setConfidence(95);
        timeSeriesAlgorithm.setFill(true);
        timeSeriesAlgorithm.setMissValue(0);
        timeSeriesAlgorithm.setPeriod(12);

        setExtraConfiguration();

        RDataModel rDataModel;
        JSONArray tableDataJa = new JSONArray();
        String[] columnNameArr = new String[baseMetaData.getColumnCount()];
        int[] columnTypeArr = new int[baseMetaData.getColumnCount()];


        // 初始化数据,把所有数据传进去
        Segment segment = segmentList.get(0);
        for (int j = 0; j < baseMetaData.getColumnCount(); ++j) {
            String columnName = baseMetaData.getColumn(j + 1).getName();
            Column column = segment.getColumn(new ColumnKey(columnName));
            DictionaryEncodedColumn dicColumn = column.getDictionaryEncodedColumn();
            JSONArray columnJa = new JSONArray();
            for (int i = 0; i < segment.getRowCount(); ++i) {

                Object cellValue = dicColumn.getValue(dicColumn.getIndexByRow(i));
                columnJa.put(cellValue);

                if (i == 0) {
                    columnNameArr[j] = columnName;
                    columnTypeArr[j] = baseMetaData.getColumn(j + 1).getType();
                }

            }
            tableDataJa.put(columnJa);
        }

        rDataModel = new RDataModel(tableDataJa, columnNameArr, columnTypeArr);

        timeSeriesAlgorithm.loadData(rDataModel);
        dataModel = timeSeriesAlgorithm.run();
    }

    @Override
    public void close() {

    }

    @Override
    public boolean next() throws SQLException {
        try {
            if (isFirst) {
                isFirst = false;
                init();
            }
            List row = new ArrayList();
            if (rowCursor < dataModel.getRowCount()) {
                for (int i = 0; i < dataModel.getColumnCount(); i++) {
                    row.add(dataModel.getValueAt(i, rowCursor));
                }
                setRowValue(new ListBasedRow(row));
                rowCursor++;
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new SQLException(e);
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
