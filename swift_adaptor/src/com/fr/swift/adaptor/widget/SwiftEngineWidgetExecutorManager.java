package com.fr.swift.adaptor.widget;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberMaxAndMinValue;
import com.finebi.conf.internalimp.dashboard.widget.chart.VanChartWidget;
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
import com.finebi.conf.structure.result.table.BICrossNode;
import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.cal.Query;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.builder.QueryBuilder;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/2/26
 */
public class SwiftEngineWidgetExecutorManager implements EngineWidgetExecutorManager {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftEngineWidgetExecutorManager.class);
    @Override
    public BIGroupNode visit(TableWidget tableWidget) {
        return TableWidgetAdaptor.calculate(tableWidget);
    }

    @Override
    public BIComplexGroupResult visit(VanChartWidget vanChartWidget) {
        return null;
    }

    @Override
    public BICrossNode visit(CrossTableWidget tableWidget) {
        return null;
    }

    @Override
    public BIDetailTableResult visit(DetailWidget detailWidget) {
        return null;
    }

    @Override
    public BIStringDetailResult visit(StringControlWidget detailWidget) {
        SwiftResultSet resultSet = getWidgetResultSet(detailWidget);
        return null;
    }

    @Override
    public BISingleSliderResult visit(SingleSliderWidget detailWidget) {
        return null;
    }

    @Override
    public BIYearControlResult visit(YearControlWidget detailWidget) {
        return null;
    }

    @Override
    public BIQuarterResult visit(QuarterControlWidget detailWidget) {
        return null;
    }

    @Override
    public BIMonthControlResult visit(MonthControlWidget detailWidget) {
        return null;
    }

    @Override
    public BITreeResult visit(TreeWidget detailWidget) {
        return null;
    }

    @Override
    public BIResult visit(TreeLabelWidget labelWidget) {
        return null;
    }

    @Override
    public BIResult visit(TreeListWidget labelWidget) {
        return null;
    }

    @Override
    public BIResult visit(DateControlWidget dateControlWidget) throws Exception {
        return null;
    }

    @Override
    public BIResult visit(DatePaneControlWidget datePaneControlWidget) throws Exception {
        return null;
    }

    @Override
    public BIResult visit(DateIntervalControlWidget dateIntervalControlWidget) throws Exception {
        return null;
    }

    @Override
    public BIResult visit(StringListControlWidget stringListControlWidget) throws Exception {
        return null;
    }

    @Override
    public BIListLabelResult visit(ListLabelWidget listLabelWidget) {
        return null;
    }

    @Override
    public NumberMaxAndMinValue getFieldMaxAndMinValueByFieldId(String fieldId) {
        return null;
    }


    @Override
    public Map<String, Object> getClickValue(FineWidget widget, Map clicked, List<String> fieldsId) {
        return null;
    }

    private SwiftResultSet getWidgetResultSet(FineWidget detailWidget){
        try {
            QueryInfo info = WidgetAdaptor.buildQueryInfo(detailWidget);
            Query query = QueryBuilder.buildQuery(info);
            return query.getQueryResult();
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}