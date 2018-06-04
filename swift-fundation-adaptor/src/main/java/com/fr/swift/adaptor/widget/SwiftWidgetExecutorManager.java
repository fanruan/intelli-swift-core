package com.fr.swift.adaptor.widget;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberMaxAndMinValue;
import com.finebi.conf.internalimp.dashboard.widget.chart.VanChartWidget;
import com.finebi.conf.internalimp.dashboard.widget.chart.geom.GeomWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.number.SingleSliderWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.string.ListLabelWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.time.DateControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.time.DateIntervalControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.time.DatePaneControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.time.MonthControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.time.QuarterControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.time.YearControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeLabelWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeListWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeWidget;
import com.finebi.conf.internalimp.dashboard.widget.detail.DetailWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.CrossTableWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.StringControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.StringListControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.service.engine.excutor.EngineWidgetExecutorManager;
import com.finebi.conf.structure.dashboard.widget.FineWidget;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.finebi.conf.structure.result.BIResult;
import com.finebi.conf.structure.result.BIStringDetailResult;
import com.finebi.conf.structure.result.control.number.BISingleSliderResult;
import com.finebi.conf.structure.result.control.string.BIListLabelResult;
import com.finebi.conf.structure.result.control.time.BIMonthControlResult;
import com.finebi.conf.structure.result.control.time.BIQuarterResult;
import com.finebi.conf.structure.result.control.time.BIYearControlResult;
import com.finebi.conf.structure.result.control.tree.BITreeResult;
import com.finebi.conf.structure.result.table.BIComplexGroupResult;
import com.finebi.conf.structure.result.table.BICrossTableResult;
import com.finebi.conf.structure.result.table.BIGroupNode;
import com.finebi.conf.structure.result.table.BITableResult;
import com.fr.swift.adaptor.widget.date.MonthControlWidgetAdaptor;
import com.fr.swift.adaptor.widget.date.QuarterControlWidgetAdaptor;
import com.fr.swift.adaptor.widget.date.YearControlWidgetAdaptor;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/2/26
 */
public class SwiftWidgetExecutorManager implements EngineWidgetExecutorManager {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftWidgetExecutorManager.class);

    @Override
    public BITableResult visit(TableWidget tableWidget) {
        return TableWidgetAdaptor.calculate(tableWidget);
    }

    @Override
    public BIComplexGroupResult visit(VanChartWidget vanChartWidget) throws Exception {
        HashMap<Integer, BIGroupNode> map = new HashMap<Integer, BIGroupNode>();

        Map<String, GeomWidget> attrWidgetMap = vanChartWidget.getAttrWidgetMap();

        Iterator<Map.Entry<String, GeomWidget>> entries = attrWidgetMap.entrySet().iterator();
        int index = 0;
        while (entries.hasNext()) {
            Map.Entry<String, GeomWidget> entry = entries.next();
            GeomWidget geomWidget = entry.getValue();
            geomWidget.setGroupNodeId(index);
            TableWidget tableWidget = new VanChartWidget();
            tableWidget.init(vanChartWidget.getValue());
            tableWidget.setDimensions(vanChartWidget.getDimensionList(geomWidget));
            tableWidget.setTargets(vanChartWidget.getTargetList(geomWidget));
            BITableResult visit = this.visit(tableWidget);
            map.put(index++, visit.getNode());
        }
        return new SwiftComplexGroupResult(new ArrayList<BIGroupNode>(map.values()));
    }

    @Override
    public BICrossTableResult visit(CrossTableWidget tableWidget) {
        return CrossTableWidgetAdaptor.calculate(tableWidget);
    }

    @Override
    public BIDetailTableResult visit(DetailWidget detailWidget) {
        return DetailWidgetAdaptor.calculate(detailWidget);
    }

    @Override
    public BIStringDetailResult visit(StringControlWidget detailWidget) {
        return StringControlWidgetAdaptor.calculate(detailWidget);
    }

    @Override
    public BISingleSliderResult visit(SingleSliderWidget detailWidget) {
        return SingleSliderWidgetAdaptor.calculate(detailWidget);
    }

    @Override
    public BIYearControlResult visit(YearControlWidget detailWidget) {
        return YearControlWidgetAdaptor.calculate(detailWidget);
    }

    @Override
    public BIQuarterResult visit(QuarterControlWidget detailWidget) {
        return QuarterControlWidgetAdaptor.calculate(detailWidget);
    }

    @Override
    public BIMonthControlResult visit(MonthControlWidget detailWidget) {
        return MonthControlWidgetAdaptor.calculate(detailWidget);
    }

    @Override
    public BITreeResult visit(TreeWidget treeWidget) {
        return TreeWidgetAdaptor.calculate(treeWidget);
    }

    @Override
    public BIResult visit(TreeLabelWidget treeLabelWidget) {
        return TreeLabelWidgetAdaptor.calculate(treeLabelWidget);
    }

    @Override
    public BIResult visit(TreeListWidget treeListWidget) {
        return TreeListWidgetAdaptor.calculate(treeListWidget);
    }

    @Override
    public BIResult visit(DateControlWidget dateControlWidget) {
        return null;
    }

    @Override
    public BIResult visit(DatePaneControlWidget datePaneControlWidget) {
        return null;
    }

    @Override
    public BIResult visit(DateIntervalControlWidget dateIntervalControlWidget) {
        return null;
    }

    @Override
    public BIResult visit(StringListControlWidget stringListControlWidget) {
        return StringControlWidgetAdaptor.calculate(stringListControlWidget);
    }

    @Override
    public BIListLabelResult visit(ListLabelWidget listLabelWidget) {
        return ListLabelWidgetAdaptor.calculate(listLabelWidget);
    }


    public NumberMaxAndMinValue getFieldMaxAndMinValueByFieldId(String s, FineWidget fineWidget) {
        return null;
    }


    public NumberMaxAndMinValue getFieldMaxAndMinValueByFieldId(String fieldId) throws Exception {
        return AbstractWidgetAdaptor.getMaxMinNumValue(fieldId);
    }

    @Override
    public Map<String, List<Object>> getClickValue(FineWidget widget, Map clicked, List<String> fieldsId) {
//        return JumpAdaptor.getClickValue(widget, clicked, fieldsId);
        return null;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}