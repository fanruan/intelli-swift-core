package com.fr.swift.source.etl.datamining.timeseries;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
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
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.timeseries.HoltWinters;
import weka.classifiers.timeseries.WekaForecaster;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.filters.supervised.attribute.TSLagMaker;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Jonas on 2018/3/13 4:43
 */
public abstract class AbstractTimeSeriesResultSet implements SwiftResultSet {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(AbstractTimeSeriesResultSet.class);
    private AlgorithmBean algorithmBean;
    private List<Segment> segmentList;
    private ListBasedRow listBasedRow = null;
    private SwiftMetaData selfMetaData = null;
    private SwiftMetaData baseMetaData = null;
    private int rowCursor = 0;
    private boolean isFirst = true;
    private HashMap<MultiFieldKey, List<MultiFieldValueItem>> multiFieldHashMap = new HashMap<MultiFieldKey, List<MultiFieldValueItem>>();
    private List<List<Object>> waitForAppadData = new ArrayList<List<Object>>();

    public AbstractTimeSeriesResultSet(AlgorithmBean algorithmBean, SwiftMetaData selfMetaData, SwiftMetaData baseMetaData, List<Segment> segmentList) throws Exception {
        this.algorithmBean = algorithmBean;
        this.segmentList = segmentList;
        this.selfMetaData = selfMetaData;
        this.baseMetaData = baseMetaData;
    }

    protected abstract void setExtraConfiguration();

    private Object getCellValueFromSegment(Segment segment, String columnName, int rowIndex) {
        Column column = segment.getColumn(new ColumnKey(columnName));
        DictionaryEncodedColumn dicColumn = column.getDictionaryEncodedColumn();
        Object cellValue = dicColumn.getValue(dicColumn.getIndexByRow(rowIndex));
        return cellValue;
    }

    private void init() throws Exception {
        // 初始化参数

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String timeFieldName = "data";
        String targetFiledName = "sum";

        List<String> additionalField = new ArrayList<String>();
        additionalField.add("store_id");
//        additionalField.add("shop_id");
        int aheadStep = 12;

        // init data, put down data set
        Segment segment = segmentList.get(0);

        for (int i = 0; i < segment.getRowCount(); ++i) {

            MultiFieldKey multiFieldKey = new MultiFieldKey();
            for (String fieldName : additionalField) {
                String cellValue = getCellValueFromSegment(segment, fieldName, i).toString();
                multiFieldKey.addFieldValue(cellValue);
            }

            // time field
            long timeCellValue = (Long) getCellValueFromSegment(segment, timeFieldName, i);
            // forecast field
            double targetCellValue = Double.parseDouble(getCellValueFromSegment(segment, targetFiledName, i).toString());

            List<MultiFieldValueItem> multiFieldValue = multiFieldHashMap.get(multiFieldKey);
            if (multiFieldValue == null) {
                multiFieldValue = new ArrayList<MultiFieldValueItem>();
                multiFieldHashMap.put(multiFieldKey, multiFieldValue);
            }
            MultiFieldValueItem multiFieldValueItem = new MultiFieldValueItem(timeCellValue, targetCellValue);
            multiFieldValue.add(multiFieldValueItem);
        }


        for (Map.Entry<MultiFieldKey, List<MultiFieldValueItem>> entry : multiFieldHashMap.entrySet()) {
            MultiFieldKey key = entry.getKey();
            List<MultiFieldValueItem> listForForecastData = entry.getValue();
            WekaForecaster forecaster = forecast(listForForecastData);

            List<List<NumericPrediction>> forecastData = forecaster.forecast(aheadStep, System.out);

            TSLagMaker.Periodicity periodicity = forecaster.getTSLagMaker().getPeriodicity();

            long deltaTime = (long)periodicity.deltaTime();
            long theLastTimeStemp = listForForecastData.get(listForForecastData.size() - 1).getTimestamp();


            for (int i = 0; i < aheadStep; i++) {
                List<NumericPrediction> predictAtStep = forecastData.get(i);
                NumericPrediction predForTarget = predictAtStep.get(0);
                List<Object> row = new ArrayList<Object>();
                theLastTimeStemp += deltaTime;
                row.add(simpleDateFormat.format(new Date(theLastTimeStemp)));
                row.addAll(key.getFields());
                row.add(predForTarget.predicted());
                waitForAppadData.add(row);
            }
        }

        System.out.println("test");
    }


    public WekaForecaster forecast(List<MultiFieldValueItem> items) {
        List<NumericPrediction> returnData = new ArrayList<NumericPrediction>();
        try {

            ArrayList<Attribute> attributes = new ArrayList<Attribute>();
            attributes.add(new Attribute("date"));
            attributes.add(new Attribute("forecast"));
            Instances instances = new Instances("instances", attributes, 2);
            for (int i = 0; i < items.size(); i++) {
                DenseInstance dataInstance = new DenseInstance(2);
                dataInstance.setDataset(instances);

                MultiFieldValueItem item = items.get(i);
                dataInstance.setValue(0, item.getTimestamp());
                dataInstance.setValue(1, item.getValue());
                instances.add(dataInstance);
            }

            // new forecaster
            WekaForecaster forecaster = new WekaForecaster();

            // set the targets we want to forecast. This method calls
            // setFieldsToLag() on the lag maker object for us
            forecaster.setFieldsToForecast("forecast");

            // default underlying classifier is SMOreg (SVM) - we'll use
            // gaussian processes for regression instead
            HoltWinters holtWinters = new HoltWinters();
            holtWinters.setSeasonCycleLength(1);
            forecaster.setBaseForecaster(holtWinters);

            forecaster.getTSLagMaker().setTimeStampField("date"); // date time stamp
            forecaster.getTSLagMaker().setMinLag(1);
            forecaster.getTSLagMaker().setMaxLag(2); // monthly data

            // add a month of the year indicator field
            forecaster.getTSLagMaker().setAddMonthOfYear(true);

            // add a quarter of the year indicator field
            forecaster.getTSLagMaker().setAddQuarterOfYear(true);

            // build the model
            forecaster.buildForecaster(instances, System.out);

            // prime the forecaster with enough recent historical data
            // to cover up to the maximum lag. In our case, we could just supply
            // the 12 most recent historical instances, as this covers our maximum
            // lag period
            forecaster.primeForecaster(instances);

            return forecaster;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
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
            List row = new ArrayList();
            if (rowCursor < waitForAppadData.size()) {
                row = waitForAppadData.get(rowCursor);
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
