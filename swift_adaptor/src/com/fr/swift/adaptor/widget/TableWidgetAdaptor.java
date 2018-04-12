package com.fr.swift.adaptor.widget;

import com.finebi.conf.constant.BIReportConstant.SORT;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValue;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValueItem;
import com.finebi.conf.internalimp.dashboard.widget.filter.CustomLinkConfItem;
import com.finebi.conf.internalimp.dashboard.widget.filter.WidgetLinkItem;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.dashboard.widget.FineWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionSort;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.adaptor.encrypt.SwiftEncryption;
import com.fr.swift.adaptor.struct.node.BIGroupNodeAdaptor;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.adaptor.widget.target.CalTargetParseUtils;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.Expander;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.dimension.GroupDimension;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupFormulaTarget;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.cal.TargetInfo;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.NoneSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.GroupNodeFactory;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pony
 * @date 2017/12/21
 * 分组表
 */
public class TableWidgetAdaptor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TableWidgetAdaptor.class);

    public static BIGroupNode calculate(TableWidget widget) {
        BIGroupNode resultNode = new BIGroupNodeAdaptor(new GroupNode(0,0,null,new Number[0]));
        SwiftResultSet resultSet;
        try {
            TargetInfo targetInfo = CalTargetParseUtils.parseCalTarget(widget);
            resultSet = QueryRunnerProvider.getInstance().executeQuery(buildQueryInfo(widget, targetInfo.getMetrics()));
            GroupNode groupNode = GroupNodeFactory.createNode((GroupByResultSet) resultSet, targetInfo.getTargetLength());
            TargetCalculatorUtils.calculate(groupNode, targetInfo.getTargetCalculatorInfoList(), targetInfo.getTargetsForShowList());
            resultNode = new BIGroupNodeAdaptor(groupNode);
        } catch (Exception e) {
            resultNode = new BIGroupNodeAdaptor(new GroupNode(-1, null));
            LOGGER.error(e);
        }
        return resultNode;
    }


    static QueryInfo buildQueryInfo(TableWidget widget, List<Metric> metrics) throws Exception {
        Cursor cursor = null;
        String queryId = widget.getWidgetId();

        List<Dimension> dimensions = getDimensions(widget.getDimensionList());
        FilterInfo filterInfo = getFilterInfo(widget);

        GroupTarget[] targets = getTargets(widget);
        Expander expander = null;
        String fieldId = widget.getDimensionList().isEmpty() ? null : widget.getDimensionList().get(0).getFieldId();
        fieldId = fieldId != null ? fieldId : metrics.isEmpty() ? null : metrics.get(0).getSourceKey().getId();
        fieldId = fieldId == null ?
                widget.getTargetList().isEmpty() ? null : widget.getTargetList().get(0).getFieldId()
                : fieldId;
        FineBusinessTable fineBusinessTable = BusinessTableUtils.getTableByFieldId(fieldId);
        DataSource baseDataSource = DataSourceFactory.transformDataSource(fineBusinessTable);
        return new GroupQueryInfo(cursor, queryId, baseDataSource.getSourceKey(), filterInfo,
                dimensions.toArray(new Dimension[dimensions.size()]),
                metrics.toArray(new Metric[metrics.size()]),
                targets, expander);
    }

    private static FilterInfo getFilterInfo(TableWidget widget) throws Exception {
        List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
        FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(widget.getFilters());
        filterInfos.add(filterInfo);
        //联动设置
        Map<String, WidgetLinkItem> linkItemMap = widget.getValue().getLinkage();
        //联动配置
        Map<String, List<CustomLinkConfItem>> linkConf = widget.getValue().getCustomLinkConf();
        if (linkItemMap != null) {
            for (Map.Entry<String, WidgetLinkItem> entry : linkItemMap.entrySet()) {
                WidgetLinkItem widgetLinkItem = entry.getValue();
                String id = entry.getKey();
                //根据联动设置找到联动配置，生成一个笛卡儿积的过滤条件
                List<CustomLinkConfItem> itemList = linkConf.get(id);
                for (CustomLinkConfItem confItem : itemList) {
                    String columnName = SwiftEncryption.decryptFieldId(confItem.getTo())[1];
                    ClickValue clickValue = widgetLinkItem.getClicked();
                    List<ClickValueItem> clickedList = clickValue.getValue();
                    for (ClickValueItem clickValueItem : clickedList) {
                        String value = clickValueItem.getText();
                        Set<String> values = new HashSet<String>();
                        values.add(value);
                        filterInfos.add(new SwiftDetailFilterInfo<Set<String>>(columnName, values, SwiftDetailFilterType.STRING_IN));
                    }
                }
            }
        }
        return new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND);
    }

    static List<Dimension> getDimensions(List<FineDimension> fineDims) throws Exception {
        List<Dimension> dimensions = new ArrayList<Dimension>();
        for (int i = 0, size = fineDims.size(); i < size; i++) {
            FineDimension fineDim = fineDims.get(i);
            dimensions.add(toDimension(fineDim, i));
        }
        return dimensions;
    }

    static GroupTarget[] getTargets(FineWidget widget) throws Exception {
        List<FineTarget> fineTargets = widget.getTargetList();
        fineTargets = fineTargets == null ? new ArrayList<FineTarget>() : fineTargets;
        GroupTarget[] targets = new GroupTarget[fineTargets.size()];
        for (int i = 0, size = fineTargets.size(); i < size; i++) {
            targets[i] = new GroupFormulaTarget(i);
        }
        return targets;
    }

    static Dimension toDimension(FineDimension fineDim, int index) {
        SourceKey key = new SourceKey(fineDim.getId());
        String columnName = SwiftEncryption.decryptFieldId(fineDim.getFieldId())[1];
        ColumnKey colKey = new ColumnKey(columnName);

        Group group = GroupAdaptor.adaptGroup(fineDim.getGroup());

        FilterInfo filterInfo = null;

        return new GroupDimension(index, key, colKey, group,
                fineDim.getSort() == null ? new AscSort(index) : adaptSort(fineDim.getSort(), index), filterInfo);
    }

    static Sort adaptSort(FineDimensionSort sort, int index) {
        switch (sort.getType()) {
            case SORT.ASC:
                return new AscSort(index);
            case SORT.DESC:
                return new DescSort(index);
            case SORT.NONE:
                return new NoneSort();
            default:
                return null;
        }
    }
}