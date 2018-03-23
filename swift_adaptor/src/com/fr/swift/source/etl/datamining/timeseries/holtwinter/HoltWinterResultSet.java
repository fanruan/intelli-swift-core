package com.fr.swift.source.etl.datamining.timeseries.holtwinter;

import com.finebi.conf.algorithm.timeseries.TimeSeriesForecast;
import com.finebi.conf.algorithm.timeseries.TimeSeriesPredictItem;
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
import com.fr.swift.source.etl.datamining.timeseries.MultiFieldKey;
import com.finebi.conf.algorithm.timeseries.TimeSeriesActualItem;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Jonas on 2018/3/13 4:43
 */
public class HoltWinterResultSet implements SwiftResultSet {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(HoltWinterResultSet.class);
    private HoltWintersBean algorithmBean;
    private List<Segment> segmentList;
    private ListBasedRow listBasedRow = null;
    private SwiftMetaData selfMetaData = null;
    private SwiftMetaData baseMetaData = null;
    private int rowCursor = 0;
    private boolean isFirst = true;
    private HashMap<MultiFieldKey, List<TimeSeriesActualItem>> multiFieldHashMap = new HashMap<MultiFieldKey, List<TimeSeriesActualItem>>();
    private List<List<Object>> baseTableData = new ArrayList<List<Object>>();
    private List<List<Object>> predictTableData = new ArrayList<List<Object>>();

    private String timeFieldName = "data";
    private String targetFiledName = "sum";
    private List<String> groupFields = new ArrayList<String>();
    private int aheadStep = 12;

    public HoltWinterResultSet(AlgorithmBean algorithmBean, SwiftMetaData selfMetaData, SwiftMetaData baseMetaData, List<Segment> segmentList) throws Exception {
        this.algorithmBean = (HoltWintersBean) algorithmBean;
        this.segmentList = segmentList;
        this.selfMetaData = selfMetaData;
        this.baseMetaData = baseMetaData;
    }

    private Object getCellValueFromSegment(Segment segment, String columnName, int rowIndex) {
        Column column = segment.getColumn(new ColumnKey(columnName));
        DictionaryEncodedColumn dicColumn = column.getDictionaryEncodedColumn();
        Object cellValue = dicColumn.getValue(dicColumn.getIndexByRow(rowIndex));
        return cellValue;
    }

    private void init() throws Exception {
        // 初始化参数
        this.timeFieldName = algorithmBean.getTimeFieldName();
        this.targetFiledName = algorithmBean.getTargetFiledName();
        this.groupFields = algorithmBean.getGroupFields();
        this.aheadStep = algorithmBean.getAheadStep();

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        groupFields.add("store_id");
//        groupFields.add("shop_id");


        // 暂时只考虑第一个segment吧
        Segment segment = segmentList.get(0);

        // 遍历数据把数据全部放到hashMap中，获取到每个分组的所有时间值和预测值
        for (int i = 0; i < segment.getRowCount(); ++i) {

            // time field
            long timeCellValue = (Long) getCellValueFromSegment(segment, timeFieldName, i);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String s = sdf.format(new Date(timeCellValue));
            Date date = sdf.parse(s);
            timeCellValue = date.getTime();

            MultiFieldKey multiFieldKey = new MultiFieldKey();
            for (String fieldName : groupFields) {
                String cellValue = getCellValueFromSegment(segment, fieldName, i).toString();
                multiFieldKey.addFieldValue(cellValue);
            }
            // forecast field
            double targetCellValue = Double.parseDouble(getCellValueFromSegment(segment, targetFiledName, i).toString());

            List<TimeSeriesActualItem> multiFieldValue = multiFieldHashMap.get(multiFieldKey);
            if (multiFieldValue == null) {
                multiFieldValue = new ArrayList<TimeSeriesActualItem>();
                multiFieldHashMap.put(multiFieldKey, multiFieldValue);
            }
            TimeSeriesActualItem timeSeriesActualItem = new TimeSeriesActualItem(timeCellValue, targetCellValue);
            multiFieldValue.add(timeSeriesActualItem);
        }

        // 把每个分组的时间列去重并求和排序
        for (Map.Entry<MultiFieldKey, List<TimeSeriesActualItem>> entry : multiFieldHashMap.entrySet()) {
            List<TimeSeriesActualItem> timeSeriesList = entry.getValue();
            MultiFieldKey multiFieldKey = entry.getKey();
            // 把时间数据按照升序进行排序
            Collections.sort(timeSeriesList);
            // 把重复的时间的值进行求和
            for (int i = 1; i < timeSeriesList.size(); i++) {
                TimeSeriesActualItem nowItem = timeSeriesList.get(i);
                TimeSeriesActualItem lastItem = timeSeriesList.get(i - 1);
                if (lastItem.compareTo(nowItem) == 0) {
                    lastItem.setActual(lastItem.getActual() + nowItem.getActual());
                    timeSeriesList.remove(i);
                    i--;
                }
            }
            // 把整理好的原始数据数据放到baseTableData中
            for (int i = 0; i < timeSeriesList.size(); i++) {
                TimeSeriesActualItem nowItem = timeSeriesList.get(i);
                List row = new ArrayList();
                row.add(nowItem.getTimestamp());
                for (Object fieldValue : multiFieldKey.getFields()) {
                    row.add(fieldValue);
                }
                row.add(nowItem.getActual());
                row.add(null);
                baseTableData.add(row);
            }
        }

        // 遍历分组过后的基础数据并预测，将结果放入到predictTableData中
        for (Map.Entry<MultiFieldKey, List<TimeSeriesActualItem>> entry : multiFieldHashMap.entrySet()) {
            MultiFieldKey key = entry.getKey();
            List<TimeSeriesActualItem> listForForecastData = entry.getValue();

            // 开始针对每一个数据进行预测
            TimeSeriesForecast timeSeriesForecast = new TimeSeriesForecast();
            List<TimeSeriesPredictItem> timeSeriesPredictItems;
            try {
                timeSeriesPredictItems = timeSeriesForecast.forecast(listForForecastData);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                timeSeriesPredictItems = new ArrayList<TimeSeriesPredictItem>();
            }

            for (int i = 0; i < timeSeriesPredictItems.size(); i++) {
                TimeSeriesPredictItem timeSeriesPredictItem = timeSeriesPredictItems.get(i);
                List<Object> row = new ArrayList<Object>();
                row.add(timeSeriesPredictItem.getTimestamp());
                row.addAll(key.getFields());
                row.add(null);
                row.add(timeSeriesPredictItem.getPredict());
                predictTableData.add(row);
            }
        }
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
            List row;
            int rowCount = baseTableData.size();

            if (rowCursor < rowCount) {
                row = baseTableData.get(rowCursor);
            } else {
                int rowDelta = rowCursor - rowCount;
                if (rowDelta < predictTableData.size()) {
                    row = predictTableData.get(rowDelta);
                } else {
                    return false;
                }
            }
            rowCursor++;
            setRowValue(new ListBasedRow(row));
            return true;
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
