package com.fr.swift.source.etl.datamining.timeseries.holtwinter;

import com.finebi.conf.algorithm.timeseries.*;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.timeseries.HoltWintersBean;
import com.fr.bi.stable.utils.BIDateFormatUtils;
import com.fr.bi.stable.utils.FineDateUtils;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.*;
import com.fr.swift.source.etl.datamining.timeseries.MultiFieldKey;
import weka.filters.supervised.attribute.TSLagMaker;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

/**
 * Created by Jonas on 2018/3/13 4:43
 */
public class HoltWinterResultSet implements SwiftResultSet {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(HoltWinterResultSet.class);
    private HoltWintersBean algorithmBean;
    private List<Segment> segmentList;
    private ListBasedRow listBasedRow;
    private SwiftMetaData selfMetaData;
    private SwiftMetaData baseMetaData;
    private int rowCursor = 0;
    private double missValue = 0;
    private HashMap<MultiFieldKey, List<TimeSeriesActualItem>> multiFieldHashMap = new HashMap<MultiFieldKey, List<TimeSeriesActualItem>>();
    private List<List<Object>> baseTableData = new ArrayList<List<Object>>();
    private List<List<Object>> predictTableData = new ArrayList<List<Object>>();

    public HoltWinterResultSet(AlgorithmBean algorithmBean, SwiftMetaData selfMetaData, SwiftMetaData baseMetaData, List<Segment> segmentList) throws Exception {
        this.algorithmBean = (HoltWintersBean) algorithmBean;
        this.segmentList = segmentList;
        this.selfMetaData = selfMetaData;
        this.baseMetaData = baseMetaData;
        init();
    }

    private Object getCellValueFromSegment(Segment segment, String columnName, int rowIndex) {
        Column column = segment.getColumn(new ColumnKey(columnName));
        DictionaryEncodedColumn dicColumn = column.getDictionaryEncodedColumn();
        return dicColumn.getValue(dicColumn.getIndexByRow(rowIndex));
    }

    private void init() throws Exception {
        // 初始化参数
        String timeFieldName = algorithmBean.getTimeFieldName();
        String targetFiledName = algorithmBean.getTargetFiledName();
        List<String> groupFields = algorithmBean.getGroupFields();
        boolean isFillMissValue = algorithmBean.isFillMissValue();
        boolean isDateField = true;
        if (!HoltWinterOperator.isDateType(baseMetaData.getColumn(timeFieldName).getType())) {
            isDateField = false;
        }

        if (isFillMissValue) {
            this.missValue = algorithmBean.getMissValue();
        }

        // 设置时间序列预测参数
        TimeSeriesForecast timeSeriesForecast = new TimeSeriesForecast();
        timeSeriesForecast.setAheadStep(algorithmBean.getAheadStep());
        timeSeriesForecast.setConfidenceLevel(algorithmBean.getConfidenceLevel());
        timeSeriesForecast.setIncludeSeasonalCorrection(algorithmBean.isIncludeSeasonalCorrection());
        timeSeriesForecast.setIncludeTrendCorrection(algorithmBean.isIncludeTrendCorrection());
        timeSeriesForecast.setSeasonCycleLength(algorithmBean.getSeasonCycleLength());
        timeSeriesForecast.setCalculateConfidenceInterval(algorithmBean.isCalculateConfidenceInterval());
        timeSeriesForecast.setMultiEffect(algorithmBean.isMultiEffect());
        int periodicityInt = algorithmBean.getPeriodicity();
        if (!isDateField) {
            // 如果是数值型的话直接当日来处理
            periodicityInt = TimeSeriesPeriodicity.UNKNOWN;
        }
        TSLagMaker.Periodicity periodicity = TimeSeriesPeriodicity.convertWekaPeriodicity(periodicityInt);
        timeSeriesForecast.setPeriodicity(periodicity);

        // 暂时只考虑第一个segment吧
        Segment segment = segmentList.get(0);

        // 遍历数据把数据全部放到hashMap中，获取到每个分组的所有时间值和预测值
        for (int i = 0; i < segment.getRowCount(); ++i) {
            // time field
            // 把日期都归约到天
            long timeCellValue = (Long) getCellValueFromSegment(segment, timeFieldName, i);
            if (isDateField) {
                DateFormat sdf = BIDateFormatUtils.DATE_FORMAT1;
                String s = sdf.format(new Date(timeCellValue));
                Date date = sdf.parse(s);
                timeCellValue = date.getTime();
            }

            MultiFieldKey multiFieldKey = new MultiFieldKey();
            for (String fieldName : groupFields) {
                String cellValue = getCellValueFromSegment(segment, fieldName, i).toString();
                multiFieldKey.addFieldValue(cellValue);
            }
            // forecast field
            double targetCellValue = Double.parseDouble(getCellValueFromSegment(segment, targetFiledName, i).toString());

            List<TimeSeriesActualItem> multiFieldValue = multiFieldHashMap.get(multiFieldKey);
            if (multiFieldValue == null) {
                multiFieldValue = new LinkedList<TimeSeriesActualItem>();
                multiFieldHashMap.put(multiFieldKey, multiFieldValue);
            }
            TimeSeriesActualItem timeSeriesActualItem = new TimeSeriesActualItem(timeCellValue, targetCellValue);
            multiFieldValue.add(timeSeriesActualItem);
        }

        // 把每个分组的时间列去重并求和排序
        for (Map.Entry<MultiFieldKey, List<TimeSeriesActualItem>> entry : multiFieldHashMap.entrySet()) {
            List<TimeSeriesActualItem> timeSeriesList = entry.getValue();
            MultiFieldKey multiFieldKey = entry.getKey();
            if (timeSeriesList.size() < 2) {
                continue;
            }
            // step.1 把时间数据按照升序进行排序
            Collections.sort(timeSeriesList);
            // step.2 如果是自动的话，计算出值来
            if (isDateField) {
                if (periodicityInt == TimeSeriesPeriodicity.UNKNOWN) {
                    long averageTime = 0;
                    for (int i = 1; i < timeSeriesList.size(); i++) {
                        TimeSeriesActualItem nowItem = timeSeriesList.get(i);
                        TimeSeriesActualItem lastItem = timeSeriesList.get(i - 1);
                        averageTime += nowItem.getTimestamp() - lastItem.getTimestamp();
                    }
                    averageTime /= (timeSeriesList.size() - 1);
                    periodicityInt = TimeSeriesPeriodicity.calculatePeriodicity(averageTime);
                }
            }

            // step.3 把重复的时间的值进行求和
            for (int i = 1; i < timeSeriesList.size(); i++) {
                TimeSeriesActualItem nowItem = timeSeriesList.get(i);
                TimeSeriesActualItem lastItem = timeSeriesList.get(i - 1);
                Calendar nowCal = Calendar.getInstance();
                Calendar lastCal = Calendar.getInstance();
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                nowCal.setTimeInMillis(nowItem.getTimestamp());
                lastCal.setTimeInMillis(lastItem.getTimestamp());
                switch (periodicityInt) {
                    case TimeSeriesPeriodicity.YEARLY:
                        if (nowCal.get(Calendar.YEAR) == lastCal.get(Calendar.YEAR)) {
                            cal.set(lastCal.get(Calendar.YEAR), Calendar.JANUARY, 1);
                            lastItem.setActual(lastItem.getActual() + nowItem.getActual());
                            lastItem.setTimestamp(cal.getTimeInMillis());
                            timeSeriesList.remove(i);
                            i--;
                        }
                        break;
                    case TimeSeriesPeriodicity.QUARTERLY:
                        int nowSeason = FineDateUtils.getSeasonByMonth(nowCal.get(Calendar.MONTH));
                        int lastSeason = FineDateUtils.getSeasonByMonth(lastCal.get(Calendar.MONTH));
                        if (nowSeason == lastSeason) {
                            if (nowSeason == 0) {
                                cal.set(nowCal.get(Calendar.YEAR), Calendar.JANUARY, 1);
                            } else if (nowSeason == 1) {
                                cal.set(nowCal.get(Calendar.YEAR), Calendar.APRIL, 1);
                            } else if (nowSeason == 2) {
                                cal.set(nowCal.get(Calendar.YEAR), Calendar.JULY, 1);
                            } else {
                                cal.set(nowCal.get(Calendar.YEAR), Calendar.OCTOBER, 1);
                            }
                            lastItem.setActual(lastItem.getActual() + nowItem.getActual());
                            lastItem.setTimestamp(cal.getTimeInMillis());
                            timeSeriesList.remove(i);
                            i--;
                        }
                        break;
                    case TimeSeriesPeriodicity.MONTHLY:
                        if (FineDateUtils.atSameYearMonth(nowItem.getTimestamp(), lastItem.getTimestamp())) {
                            cal.set(lastCal.get(Calendar.YEAR), lastCal.get(Calendar.MONTH), 1);
                            lastItem.setActual(lastItem.getActual() + nowItem.getActual());
                            lastItem.setTimestamp(cal.getTimeInMillis());
                            timeSeriesList.remove(i);
                            i--;
                        }
                        break;
                    case TimeSeriesPeriodicity.WEEKLY:
                        if (nowCal.get(Calendar.WEEK_OF_YEAR) == lastCal.get(Calendar.WEEK_OF_YEAR)) {
                            cal.set(Calendar.YEAR, lastCal.get(Calendar.YEAR));
                            cal.set(Calendar.WEEK_OF_YEAR, lastCal.get(Calendar.WEEK_OF_YEAR));
                            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                            lastItem.setActual(lastItem.getActual() + nowItem.getActual());
                            lastItem.setTimestamp(cal.getTimeInMillis());
                            timeSeriesList.remove(i);
                            i--;
                        }
                        break;
                    default:
                        if (lastItem.compareTo(nowItem) == 0) {
                            lastItem.setActual(lastItem.getActual() + nowItem.getActual());
                            timeSeriesList.remove(i);
                            i--;
                        }
                        break;
                }
            }

            // step.5 填充缺失值
            if (isDateField && isFillMissValue) {
                TimeSeriesMissValue timeSeriesMissValue = new TimeSeriesMissValue(missValue, periodicityInt);
                timeSeriesList = timeSeriesMissValue.fill(timeSeriesList);
            }

            // 把整理好的原始数据数据放到baseTableData中
            for (TimeSeriesActualItem timeSeriesActualItem : timeSeriesList) {
                List<Object> row = new ArrayList<Object>();
                // 放到baseTableData中
                row.add(timeSeriesActualItem.getTimestamp());
                if (multiFieldKey.getFields().size() > 0) {
                    row.addAll(multiFieldKey.getFields());
                }
                row.add(timeSeriesActualItem.getActual());
                row.add(null);
                baseTableData.add(row);
            }

            // step.6 遍历分组过后的基础数据并预测，将结果放入到predictTableData中
            List<TimeSeriesActualItem> listForForecastData = entry.getValue();
            // 开始针对每一个分组的数据进行预测
            List<TimeSeriesPredictItem> timeSeriesPredictItems;
            try {
                timeSeriesPredictItems = timeSeriesForecast.forecast(listForForecastData, isDateField);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                timeSeriesPredictItems = new ArrayList<TimeSeriesPredictItem>();
            }
            for (TimeSeriesPredictItem timeSeriesPredictItem : timeSeriesPredictItems) {
                List<Object> row = new ArrayList<Object>();
                row.add(timeSeriesPredictItem.getTimestamp());
                if (multiFieldKey.getFields().size() > 0) {
                    row.addAll(multiFieldKey.getFields());
                }
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
            List<Object> row;
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
