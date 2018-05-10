package com.fr.swift.adaptor.widget;

import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.internalimp.bean.dashboard.widget.table.TableWidgetBean;
import com.finebi.conf.internalimp.dashboard.widget.detail.DetailWidget;
import com.finebi.conf.internalimp.dashboard.widget.filter.CustomLinkConfItem;
import com.finebi.conf.internalimp.dashboard.widget.filter.WidgetLinkItem;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.adaptor.linkage.LinkageAdaptor;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftEmptyResult;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.transformer.SortAdaptor;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.DetailQueryInfo;
import com.fr.swift.config.conf.MetaDataConvertUtil;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.adapter.dimension.AllCursor;
import com.fr.swift.query.adapter.dimension.Cursor;
import com.fr.swift.query.adapter.dimension.DetailDimension;
import com.fr.swift.query.adapter.dimension.DetailFormulaDimension;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.target.DetailFormulaTarget;
import com.fr.swift.query.adapter.target.DetailTarget;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftMetaDataImpl;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.utils.BusinessTableUtils;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author pony
 * @date 2017/12/21
 * 明细表
 */
public class DetailWidgetAdaptor extends AbstractWidgetAdaptor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(DetailWidgetAdaptor.class);

    public static BIDetailTableResult calculate(DetailWidget widget) {
        BIDetailTableResult result = null;
        SwiftResultSet resultSet;
        try {
            resultSet = QueryRunnerProvider.getInstance().executeQuery(buildQueryInfo(widget));
            if (resultSet == null) {
                return new SwiftDetailTableResult(new SwiftEmptyResult(), 0, -1);
            }
            result = new SwiftDetailTableResult(resultSet, widget.getTotalRows(), widget.getPage());
        } catch (Exception e) {
            LOGGER.error(e);
        }

        return result;
    }

    static QueryInfo buildQueryInfo(DetailWidget widget) throws Exception {

        Cursor cursor = null;
        String queryId = widget.getWidgetId();
        Dimension[] dimensions = getDimension(widget);
        SourceKey target = new SourceKey(BusinessTableUtils.getSourceIdByTableId(widget.getTableName()));
        SwiftMetaData swiftMetaData = MetaDataConvertUtil.getSwiftMetaDataBySourceKey(target.toString());
        SwiftMetaData metaData = getMetaData(widget, swiftMetaData);
        DetailTarget[] targets = getTargets(widget);
        //没传进来排序顺序
        IntList sortIndex = IntListFactory.createHeapIntList();
        for (int i = 0; i < dimensions.length; i++) {
            sortIndex.add(i);
        }
        List<FilterInfo> filterInfos = handleLinkageFilterList(widget);
        filterInfos.add(FilterInfoFactory.transformFineFilter(metaData.getTableName(), dealWithTargetFilter(widget, widget.getFilters())));
        return new DetailQueryInfo(cursor, queryId, dimensions, target, targets, sortIndex, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND), metaData);
    }

    /**
     * 计算被联动过滤条件
     *
     * @param widget
     * @return
     */
    private static List<FilterInfo> handleLinkageFilterList(DetailWidget widget) {
        List<FilterInfo> fineFilters = new ArrayList<FilterInfo>();
        TableWidgetBean bean = widget.getValue();
        String tableName = widget.getTableName();
        if (null != bean) {
            Map<String, WidgetLinkItem> map = bean.getLinkage();
            Map<String, List<CustomLinkConfItem>> custLinkConf = bean.getCustomLinkConf();
            Iterator<Map.Entry<String, WidgetLinkItem>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, WidgetLinkItem> entry = iterator.next();
                WidgetLinkItem item = entry.getValue();
                String id = entry.getKey();

                try {
                    if (null != custLinkConf && custLinkConf.containsKey(id)) {
                        dealWithCustomLinkConf(widget, fineFilters, item, custLinkConf.get(id));
                    } else {
                        LinkageAdaptor.handleClickItem(tableName, item, fineFilters);
                    }
                } catch (Exception ignore) {
                    LOGGER.error(ignore.getMessage());
                }
            }
        }
        return fineFilters;
    }

    private static SwiftMetaData getMetaData(DetailWidget widget, SwiftMetaData metaData) throws Exception {
        final List<FineDimension> fineDimensions = widget.getDimensionList();
        List<SwiftMetaDataColumn> fields = new ArrayList<SwiftMetaDataColumn>();
        for (int i = 0, len = fineDimensions.size(); i < len; i++) {
            FineDimension fineDimension = fineDimensions.get(i);
            String columnName = fineDimension.getText();
            fields.add(new MetaDataColumn(columnName, Types.VARCHAR));
        }
        return new SwiftMetaDataImpl(metaData.getTableName(), metaData.getRemark(), metaData.getSchemaName(), fields);
    }

    private static Dimension[] getDimension(DetailWidget widget) throws Exception {
        final List<FineDimension> fineDimensions = widget.getDimensionList();
        Dimension[] dimensions = new Dimension[fineDimensions.size()];
        for (int i = 0, size = fineDimensions.size(); i < size; i++) {
            FineDimension fineDimension = fineDimensions.get(i);
            if (fineDimension.getType() == BIDesignConstants.DESIGN.DIMENSION_TYPE.CAL_TARGET){
                dimensions[i] = new DetailFormulaDimension(i, new SourceKey(fineDimension.getId()),
                        FilterInfoFactory.transformFineFilter(widget.getTableName(), dealWithTargetFilter(widget, widget.getFilters())), getFormula(fineDimension.getFieldId(), widget));
            } else {
                String columnName = BusinessTableUtils.getFieldNameByFieldId(fineDimension.getFieldId());
                Sort sort = SortAdaptor.adaptorDimensionSort(fineDimension.getSort(), i);
                //暂时先不管明细表自定义分组
                dimensions[i] = new DetailDimension(i, new SourceKey(fineDimension.getId()), new ColumnKey(columnName), null, sort, FilterInfoFactory.transformFineFilter(widget.getTableName(), dealWithTargetFilter(widget, widget.getFilters())));
            }
        }
        return dimensions;
    }


    private static DetailTarget[] getTargets(DetailWidget widget) throws Exception {
        List<FineTarget> fineTargets = widget.getTargetList();
        if (fineTargets == null) {
            return null;
        }
        DetailTarget[] targets = new DetailTarget[fineTargets.size()];
        for (int i = 0, size = fineTargets.size(); i < size; i++) {
            targets[i] = new DetailFormulaTarget(i);
        }
        return targets;
    }


    private static void dealWithCustomLinkConf(DetailWidget detailWidget, List<FilterInfo> filterInfoList, WidgetLinkItem widgetLinkItem, List<CustomLinkConfItem> customLinkConfItems) throws Exception {
        //自定义设置的维度
        Dimension[] fromColumns = new Dimension[customLinkConfItems.size()];
        //要过滤的维度
        String[] toColumns = new String[customLinkConfItems.size()];
        for (int i = 0; i < customLinkConfItems.size(); i++) {
            CustomLinkConfItem confItem = customLinkConfItems.get(i);
            fromColumns[i] = (new DetailDimension(i, getSourceKey(confItem.getFrom()), new ColumnKey(getColumnName(confItem.getFrom())), null, new AscSort(i, new ColumnKey(getColumnName(confItem.getFrom()))), null));
            toColumns[i] = getColumnName(confItem.getTo());
        }
        //根据点击的值，创建过滤条件
        List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
        TableWidgetBean fromWidget = LinkageAdaptor.handleClickItem(detailWidget.getTableName(), widgetLinkItem, filterInfos);
        //明细表查询
        FilterInfo filterInfo = new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND);
        String queryId = fromWidget.getwId();
        SourceKey target = new SourceKey(BusinessTableUtils.getSourceIdByTableId(detailWidget.getTableName()));
        SwiftMetaData swiftMetaData = MetaDataConvertUtil.getSwiftMetaDataBySourceKey(target.getId());
        SwiftMetaData metaData = getMetaData(detailWidget, swiftMetaData);
        DetailTarget[] targets = getTargets(detailWidget);
        //没传进来排序顺序
        IntList sortIndex = IntListFactory.createHeapIntList();
        for (int i = 0; i < fromColumns.length; i++) {
            sortIndex.add(i);
        }
        DetailQueryInfo info = new DetailQueryInfo(new AllCursor(), queryId, fromColumns, target, targets, sortIndex, filterInfo, metaData);
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(info);
        Set[] results = new HashSet[toColumns.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = new HashSet();
        }
        while (resultSet.next()) {
            Row row = resultSet.getRowData();
            for (int i = 0; i < row.getSize(); i++) {
                results[i].add(row.getValue(i));
            }
        }
        for (int i = 0; i < toColumns.length; i++) {
            filterInfoList.add(new SwiftDetailFilterInfo(new ColumnKey(toColumns[i]), results[i], SwiftDetailFilterType.STRING_IN));
        }
    }

}