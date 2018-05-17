package com.fr.swift.adaptor.widget;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.constant.BIDesignConstants.DESIGN;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberMaxAndMinValue;
import com.finebi.conf.internalimp.bean.dashboard.widget.field.WidgetBeanField;
import com.finebi.conf.internalimp.bean.dashboard.widget.table.TableWidgetBean;
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
import com.finebi.conf.structure.result.table.BICrossTableResult;
import com.finebi.conf.structure.result.table.BIGroupNode;
import com.finebi.conf.structure.result.table.BITableResult;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.linkage.LinkageAdaptor;
import com.fr.swift.adaptor.widget.date.MonthControlWidgetAdaptor;
import com.fr.swift.adaptor.widget.date.QuarterControlWidgetAdaptor;
import com.fr.swift.adaptor.widget.date.YearControlWidgetAdaptor;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.DetailQueryInfo;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.adapter.dimension.AllCursor;
import com.fr.swift.query.adapter.dimension.DetailDimension;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.structure.Pair;
import com.fr.swift.utils.BusinessTableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    public BIComplexGroupResult visit(VanChartWidget vanChartWidget) {
        final BITableResult result = TableWidgetAdaptor.calculate(vanChartWidget);
        return new BIComplexGroupResult() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public BIGroupNode getNode(int index) {
                return result.getNode();
            }

            @Override
            public ResultType getResultType() {
                return ResultType.BICOMPLEXGROUP;
            }
        };
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

    @Override
    public NumberMaxAndMinValue getFieldMaxAndMinValueByFieldId(String fieldId) throws Exception {
        return AbstractWidgetAdaptor.getMaxMinNumValue(fieldId);
    }

    @Override
    public Map<String, Object> getClickValue(FineWidget widget, Map clicked, List<String> fieldsId) throws SQLException {
        Map<String, Object> clickValues = new HashMap<String, Object>();
        for (Pair<String, Object> pair : getClickValue0(widget, clicked, fieldsId)) {
            clickValues.put(pair.getKey(), pair.getValue());
        }
        return clickValues;
    }

    private static List<Pair<String, Object>> getClickGroupValue(TableWidget widget, Map clicked, List<String> fieldIds) throws SQLException {
        List<Map> clickMaps = (List<Map>) clicked.get("value");
        // fieldId -> value
        Map<String, String> clicks = new HashMap<String, String>();
        TableWidgetBean widgetBean = widget.getValue();
        for (Map clickMap : clickMaps) {
            String dId = clickMap.get("dId").toString();
            clicks.put(widgetBean.getDimensions().get(dId).getFieldId(), clickMap.get("text").toString());
        }
        List<WidgetBeanField> widgetBeanFields = widgetBean.getFields();
        List<Dimension> dims = new ArrayList<Dimension>();
        SourceKey sourceKey = null;
        for (int i = 0; i < fieldIds.size(); i++) {
            String fieldId = fieldIds.get(i);
            if (sourceKey == null) {
                sourceKey = BusinessTableUtils.getSourceKey(fieldId);
            }
            ColumnKey columnKey = BusinessTableUtils.getColumnKey(fieldId);
            for (WidgetBeanField widgetBeanField : widgetBeanFields) {
                if (ComparatorUtils.equals(fieldId, widgetBeanField.getId())) {
                    FilterInfo filterInfo = null;
                    if (clicks.containsKey(fieldId)) {
                        filterInfo = LinkageAdaptor.dealFilterInfo(columnKey, clicks.get(fieldId), widgetBeanField.getType());
                    }
                    dims.add(new DetailDimension(i, sourceKey, columnKey, null, null, filterInfo));
                    break;
                }
            }
        }
        QueryInfo queryInfo = new DetailQueryInfo(new AllCursor(), widget.getWidgetId(), dims.toArray(new Dimension[0]), sourceKey, null, null, null, null);
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfo);
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        if (resultSet.next()) {
            Row row = resultSet.getRowData();
            for (int i = 0; i < fieldIds.size(); i++) {
                String fieldId = fieldIds.get(i);
                pairs.add(Pair.of(fieldId, row.getValue(i)));
            }
        }
        return pairs;
    }

    private static List<Pair<String, Object>> getClickDetailValue(DetailWidget widget, Map clicked, List<String> fieldIds) throws SQLException {
        int pageCount = Integer.valueOf(clicked.get("pageCount").toString());
        int rowIndex = Integer.valueOf(clicked.get("rowIndex").toString());
        int rowCount = (pageCount - 1) * DESIGN.DEFAULT_PAGE_ROW_SIZE + rowIndex;
        Dimension[] dims = new DetailDimension[fieldIds.size()];
        int i = 0;
        SourceKey sourceKey = null;
        for (String fieldId : fieldIds) {
            if (sourceKey == null) {
                sourceKey = new SourceKey(BusinessTableUtils.getSourceIdByFieldId(fieldId));
            }
            dims[i] = new DetailDimension(i, sourceKey, new ColumnKey(BusinessTableUtils.getFieldNameByFieldId(fieldId)), null, null, null);
            i++;
        }
        QueryInfo queryInfo = new DetailQueryInfo(new AllCursor(), widget.getWidgetId(), dims, sourceKey, null, null, null, null);
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfo);
        int cursor = 0;
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        while (resultSet.next()) {
            Row row = resultSet.getRowData();
            if (cursor++ < rowCount) {
                continue;
            }
            for (int j = 0; j < fieldIds.size(); j++) {
                pairs.add(Pair.of(fieldIds.get(j), row.getValue(j)));
            }
            return pairs;
        }
        return pairs;
    }

    private static List<Pair<String, Object>> getClickValue0(FineWidget widget, Map clicked, List<String> fieldIds) throws SQLException {
        switch (widget.getType()) {
            case 4:
                return getClickDetailValue((DetailWidget) widget, clicked, fieldIds);
            case 1:
                return getClickGroupValue((TableWidget) widget, clicked, fieldIds);
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}