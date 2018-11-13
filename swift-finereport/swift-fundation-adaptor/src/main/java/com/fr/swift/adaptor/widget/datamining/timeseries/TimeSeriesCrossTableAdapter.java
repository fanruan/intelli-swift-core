package com.fr.swift.adaptor.widget.datamining.timeseries;

import com.finebi.conf.algorithm.timeseries.MultiHoltWintersForecast;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.timeseries.HoltWintersBean;
import com.finebi.conf.internalimp.dashboard.widget.table.CrossTableWidget;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.finebi.conf.utils.transform.FineDataTransformUtils;
import com.fr.swift.adaptor.widget.datamining.DMErrorWrap;
import com.fr.swift.adaptor.widget.datamining.DMSwiftWidgetUtils;
import com.fr.swift.adaptor.widget.datamining.SwiftAlgorithmResultAdapter;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.info.group.XGroupQueryInfo;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.XNodeMergeResultSet;
import com.fr.swift.result.node.iterator.PostOrderNodeIterator;
import com.fr.swift.source.SwiftResultSet;
import edu.emory.mathcs.backport.java.util.Arrays;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2018/5/15.
 */
public class TimeSeriesCrossTableAdapter extends SwiftAlgorithmResultAdapter<HoltWintersBean, CrossTableWidget, NodeResultSet, XGroupQueryInfo> {

    public TimeSeriesCrossTableAdapter(HoltWintersBean bean, CrossTableWidget widget, NodeResultSet result, XGroupQueryInfo info, DMErrorWrap errorWrap) {
        super(bean, widget, result, info, errorWrap);
    }

    @Override
    public SwiftResultSet getResult() throws Exception {
//        if (info.getColDimensionInfo().getDimensions().length == 0 || info.getDimensionInfo().getDimensions().length == 0) {
//            return handleGroupTable();
//        } else {
            // 行列表头都不为空
            return handleCrossTable();
//        }
    }

    private SwiftResultSet handleGroupTable() {
        TimeSeriesGroupTableAdapter groupTableAdapter = new TimeSeriesGroupTableAdapter(bean, widget, result, info, errorWrap);
        return groupTableAdapter.getResult();
    }

    private SwiftResultSet handleCrossTable() throws Exception {
        errorWrap.setError("Multiple dimensions cannot be predicted.");

        XNodeMergeResultSet XResultSet = (XNodeMergeResultSet) result;

        List<FineTarget> targetList = widget.getTargetList();
//        TargetInfo targetInfo = info.getTargetInfo();

        boolean isCalculateConfidence = bean.isCalculateConfidenceInterval();

        // 把指标长度设置成两倍
        List<FineTarget> fineTargets = new ArrayList<FineTarget>();
        List<Aggregator> aggregators = null;
//        List<Aggregator> aggregators = info.getTargetInfo().getResultAggregators();
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


        XLeftNode leftNode = (XLeftNode) XResultSet.getNode();
//        int leftDepth = info.getDimensionInfo().getDimensions().length;
        int leftDepth = 0;
        PostOrderNodeIterator<XLeftNode> xLeftIterator = new PostOrderNodeIterator<XLeftNode>(leftDepth, leftNode);
        while (xLeftIterator.hasNext()) {
            XLeftNode next = xLeftIterator.next();
            List<AggregatorValue[]> newXValue = new ArrayList<AggregatorValue[]>();
            AggregatorValue[][] xValue = next.getXValue();
            for (int i = 0; i < xValue.length; i++) {
                newXValue.add(xValue[i]);
                AggregatorValue[] addNewValue = (AggregatorValue[]) Arrays.copyOf(xValue[i], xValue[i].length);
                Arrays.fill(addNewValue, null);
                newXValue.add(addNewValue);
            }
            AggregatorValue[][] newXValueArr = new AggregatorValue[newXValue.size()][];
            newXValue.toArray(newXValueArr);
            next.setXValues(newXValueArr);
        }


        return XResultSet;
    }
}
