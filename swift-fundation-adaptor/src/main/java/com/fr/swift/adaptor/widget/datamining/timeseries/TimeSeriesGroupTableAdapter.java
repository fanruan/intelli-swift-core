package com.fr.swift.adaptor.widget.datamining.timeseries;

import com.finebi.conf.algorithm.common.DMUtils;
import com.finebi.conf.algorithm.timeseries.MultiHoltWintersForecast;
import com.finebi.conf.algorithm.timeseries.TimeSeriesActualItem;
import com.finebi.conf.algorithm.timeseries.TimeSeriesFactory;
import com.finebi.conf.algorithm.timeseries.TimeSeriesForecast;
import com.finebi.conf.algorithm.timeseries.TimeSeriesPeriodicity;
import com.finebi.conf.algorithm.timeseries.TimeSeriesPredictItem;
import com.finebi.conf.algorithm.timeseries.TimeSeriesUtils;
import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.timeseries.HoltWintersBean;
import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.CrossTableWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.finebi.conf.utils.transform.FineDataTransformUtils;
import com.finebi.log.BILoggerFactory;
import com.fr.swift.adaptor.widget.datamining.DMErrorWrap;
import com.fr.swift.adaptor.widget.datamining.DMSwiftWidgetUtils;
import com.fr.swift.adaptor.widget.datamining.SwiftAlgorithmResultAdapter;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.query.aggregator.WrappedAggregator;
import com.fr.swift.query.info.element.target.TargetInfo;
import com.fr.swift.query.info.group.GroupQueryInfoImpl;
import com.fr.swift.query.info.group.XGroupQueryInfo;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSetImpl;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.source.SwiftResultSet;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Jonas on 2018/5/9.
 */
public class TimeSeriesGroupTableAdapter extends SwiftAlgorithmResultAdapter<HoltWintersBean, AbstractTableWidget, NodeResultSet, GroupQueryInfoImpl> {

    private double[][] confidence;
    private boolean isCalculateConfidence = false;

    public TimeSeriesGroupTableAdapter(HoltWintersBean bean, AbstractTableWidget widget, NodeResultSet result, GroupQueryInfoImpl info, DMErrorWrap errorWrap) {
        super(bean, widget, result, info, errorWrap);
    }

    @Override
    public SwiftResultSet getResult() {

        GroupNode rootNode = (GroupNode) result.getNode();

        try {
            List<FineDimension> dimensionList = new ArrayList<FineDimension>();

            boolean isCrossTable = widget.getType() == BIDesignConstants.DESIGN.WIDGET.CROSS_TABLE;
            if (isCrossTable) {
                CrossTableWidget crossTableWidget = (CrossTableWidget) widget;
                XGroupQueryInfo xGroupQueryInfo = (XGroupQueryInfo) info;
                if (xGroupQueryInfo.getColDimensionInfo().getDimensions().length != 0) {
                    dimensionList = crossTableWidget.getColDimensionList();
                }
//                } else if (info.getDimensionInfo().getDimensions().length != 0) {
//                    dimensionList = widget.getDimensionList();
//                }
            } else {
                dimensionList = widget.getDimensionList();
            }

            TargetInfo targetInfo = null;
//            TargetInfo targetInfo = info.getTargetInfo();

            isCalculateConfidence = bean.isCalculateConfidenceInterval();

            List<FineTarget> targetList = widget.getTargetList();
            // 维度是时间列，目标列,且维度个数必须为一个
            if (dimensionList.size() != 1 || dimensionList.get(0).getType() != BIDesignConstants.DESIGN.DIMENSION_TYPE.DATE) {
                throw new Exception("The dimension must have only one time dimension.");
            }

            FineDimension dateDimension = dimensionList.get(0);

            // 把指标长度设置成两倍
            List<FineTarget> fineTargets = new ArrayList<FineTarget>();
            List<Aggregator> aggregators = targetInfo.getResultAggregators();
            for (int i = 0; i < targetList.size(); i++) {
                FineTarget fineTarget = targetList.get(i);
                fineTargets.add(fineTarget);
                String fieldNamePrefix = FineDataTransformUtils.formatData(fineTarget.getText());
                fineTargets.add(DMSwiftWidgetUtils.createFineTarget(fineTarget, fieldNamePrefix + MultiHoltWintersForecast.FORECAST_SUFFIX));
                Aggregator targetAggregator = aggregators.get(i);
                aggregators.add(i + 1, targetAggregator);
                if (isCalculateConfidence) {
                    fineTargets.add(DMSwiftWidgetUtils.createFineTarget(fineTarget, fieldNamePrefix + MultiHoltWintersForecast.LOWER_SUFFIX));
                    fineTargets.add(DMSwiftWidgetUtils.createFineTarget(fineTarget, fieldNamePrefix + MultiHoltWintersForecast.UPPER_SUFFIX));
                    aggregators.add(i + 2, targetAggregator);
                    aggregators.add(i + 3, targetAggregator);
                }
            }
            widget.setTargets(fineTargets);



            boolean isDesc = dateDimension.getSort() == null || dateDimension.getSort().getType() == 0;
            int periodicity;
            int groupType = dateDimension.getGroup().getType();
            int childLength = rootNode.getChildrenSize();
            double[] missValue = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            List<TimeSeriesPredictItem> predictItems;
            GroupNode resultRootNode = new GroupNode(-1,rootNode.getData());
            List<TimeSeriesActualItem> actualItems = new ArrayList<TimeSeriesActualItem>();

            // resultRootNode.setData();


            // Node转换成预测类型
            for (int i = 0; i < childLength; i++) {
                int n = i;
                if (!isDesc) {
                    n = childLength - i - 1;
                }
                SwiftNode child = rootNode.getChild(n);
                Long time = DMUtils.objConvertToTime(child.getData(), false);
//                Number[] summaryValue = child.getSummaryValue();
                AggregatorValue[] summaryValue = child.getAggregatorValue();
                if (missValue == null) {
                    missValue = new double[summaryValue.length];
                }
                double[] target = new double[summaryValue.length];
                for (int j = 0; j < summaryValue.length; j++) {
                    missValue[j] = bean.getMissValue();
                    target[j] = summaryValue[j].calculate();
                }
                if (time == null) {
                    time = (long) bean.getMissValue();
                }
                actualItems.add(new TimeSeriesActualItem(time, target));
            }

            // 根据维度分组类型，决定时间粒度
            switch (groupType) {
                // 年要特殊处理一下，因为年返回的是只有年份数据
                case BIDesignConstants.DESIGN.GROUP.Y:
                    for (TimeSeriesActualItem item : actualItems) {
                        Date date = format.parse(String.valueOf(item.getTimestamp()));
                        item.setTimestamp(date.getTime());
                    }
                    periodicity = TimeSeriesPeriodicity.YEARLY;
                    break;
                case BIDesignConstants.DESIGN.GROUP.YS:
                    periodicity = TimeSeriesPeriodicity.QUARTERLY;
                    break;
                case BIDesignConstants.DESIGN.GROUP.YM:
                    periodicity = TimeSeriesPeriodicity.MONTHLY;
                    break;
                case BIDesignConstants.DESIGN.GROUP.YW:
                    periodicity = TimeSeriesPeriodicity.WEEKLY;
                    break;
                case BIDesignConstants.DESIGN.GROUP.YMD:
                    periodicity = TimeSeriesPeriodicity.DAILY;
                    break;
                default:
                    throw new Exception("Group type does not support.");
            }
            // 填充缺失值
            List<TimeSeriesActualItem> fillActualItems = TimeSeriesUtils.fillMissValue(actualItems, missValue, periodicity, bean.isFillMissValue(), true);

            // 重新设置bean值
            bean.setPeriodicity(periodicity);
            // 设置算法参数
            TimeSeriesForecast forecast = TimeSeriesFactory.create(bean);
            predictItems = forecast.forecast(fillActualItems, true);

            // 把训练数据加入到node
            for (TimeSeriesActualItem item : fillActualItems) {
                // 再把年份数据还原回来
                if (groupType == BIDesignConstants.DESIGN.GROUP.Y) {
                    String yyyy = format.format(new Date(item.getTimestamp()));
                    item.setTimestamp(Long.parseLong(yyyy));
                }
                GroupNode newNode = new GroupNode(0, item.getTimestamp());
                newNode.setAggregatorValue(doubleArrToNumberArr(item.getActual(), false));
                resultRootNode.addChild(newNode);
            }

            // 把预测数据加入到node
            for (TimeSeriesPredictItem item : predictItems) {
                // 再把年份数据还原回来
                if (groupType == BIDesignConstants.DESIGN.GROUP.Y) {
                    String yyyy = format.format(new Date(item.getTimestamp()));
                    item.setTimestamp(Long.parseLong(yyyy));
                }
                GroupNode newNode = new GroupNode();
                newNode.setData(item.getTimestamp());
                if (isCalculateConfidence) {
                    confidence = item.getPredictionIntervals();
                }
                newNode.setAggregatorValue(doubleArrToNumberArr(item.getPredict(), true));
                resultRootNode.addChild(newNode);
            }
            // resultRootNode.setAggregatorValue(NumberArrToAggregatorValueArr(sums));
            // 使用结果汇总聚合器汇总，相对于明细的汇总方式，可能一样也可能不一样。这边可以通过细分做进一步优化。
//            GroupNodeAggregateUtils.aggregate(NodeType.GROUP, info.getDimensionInfo().getDimensions().length,
//                    resultRootNode, aggregators);

            if (!isDesc) {
                Collections.reverse(resultRootNode.getChildren());
            }
            return new NodeMergeResultSetImpl(resultRootNode, new ArrayList<Map<Integer, Object>>());

        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            errorWrap.setError(e.getMessage());
            return generatorErrorResult(rootNode, e.getMessage());
        }
    }

    private AggregatorValue[] doubleArrToNumberArr(double[] doubles, boolean isPredictCol) {
        if (doubles == null) {
            return null;
        }
        int totalLen = !isCalculateConfidence ? doubles.length * 2 : doubles.length * 4;
        Double[] numbers = new Double[totalLen];
        Arrays.fill(numbers, null);
        for (int i = 0; i < doubles.length; i++) {
            int n = !isCalculateConfidence ? i * 2 : i * 4;
            Double num = doubles[i];
            if (!isPredictCol) {
                numbers[n] = num; // 实际值
                if (isCalculateConfidence) {
                    numbers[n + 2] = null; // 低置信区间
                    numbers[n + 3] = null; // 高置信区间
                }
            } else {
                numbers[n + 1] = num;  // 预测值
                if (isCalculateConfidence) {
                    numbers[n + 2] = confidence[i][0]; // 低置信区间
                    numbers[n + 3] = confidence[i][1]; // 高置信区间
                }
            }
        }
        return NumberArrToAggregatorValueArr(numbers);
    }

    private SwiftResultSet generatorErrorResult(GroupNode rootNode, String err) {

        GroupNode resultRootNode = new GroupNode(-1, null);
        GroupNode node = getNewSummaryValueNode(resultRootNode, rootNode);

        // 求汇总值
//        GroupNodeAggregateUtils.aggregate(NodeType.GROUP, info.getDimensionInfo().getDimensions().length,
//                resultRootNode, info.getTargetInfo().getResultAggregators());

        NodeMergeResultSetImpl dmTableResult = new NodeMergeResultSetImpl(node, new ArrayList<Map<Integer, Object>>());
//        dmTableResult.setError(err);
        return dmTableResult;
    }

    private GroupNode getNewSummaryValueNode(GroupNode fineNode, GroupNode biNode) {
        // 设置本身
        fineNode.setData(biNode.getData());
        fineNode.setDepth(biNode.getDepth());
        fineNode.setAggregatorValue(generatorErrorSummaryValue(biNode.getAggregatorValue()));

        // 迭代孩子
        for (int i = 0; i < biNode.getChildrenSize(); i++) {
            GroupNode biChildren = biNode.getChild(i);

            GroupNode newNode = new GroupNode();
            newNode.setParent(fineNode);
            fineNode.addChild(getNewSummaryValueNode(newNode, biChildren));
        }
        return fineNode;
    }

    private AggregatorValue[] generatorErrorSummaryValue(AggregatorValue[] doubles) {
        if (doubles == null) {
            return null;
        }
        int totalLen = !isCalculateConfidence ? doubles.length * 2 : doubles.length * 4;
        Double[] numbers = new Double[totalLen];
        for (int i = 0; i < doubles.length; i++) {
            Double num = doubles[i].calculate();
            int n = !isCalculateConfidence ? i * 2 : i * 4;
            numbers[n] = num;
            numbers[n + 1] = null;
            if (isCalculateConfidence) {
                numbers[n + 2] = null;
                numbers[n + 3] = null;
            }
        }

        return NumberArrToAggregatorValueArr(numbers);
    }

    private AggregatorValue[] NumberArrToAggregatorValueArr(Number[] numbers) {
        AggregatorValue[] arr = new AggregatorValue[numbers.length];
        List<Aggregator> aggregators = null;
//        List<Aggregator> aggregators = info.getTargetInfo().getResultAggregators();
        for (int i = 0; i < numbers.length; i++) {
            Number num = numbers[i];
            if (num == null) {
                arr[i] = null;
            } else {
                // 这个地方得更改一下isAggregatorTypeChanged为false
                Aggregator aggregator = aggregators.get(i);
                if (aggregator instanceof WrappedAggregator) {
                    try {
                        Field changedAgg = aggregator.getClass().getDeclaredField("changedAgg");
                        Field metricAgg = aggregator.getClass().getDeclaredField("metricAgg");
                        changedAgg.setAccessible(true);
                        metricAgg.setAccessible(true);
                        changedAgg.set(aggregator, metricAgg.get(aggregator));
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error(e.getMessage(), e);
                    }
                }
                arr[i] = aggregator.createAggregatorValue(new DoubleAmountAggregatorValue(num.doubleValue()));
            }
        }
        return arr;
    }
}
