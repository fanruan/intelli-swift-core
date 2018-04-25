package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.bean.dashboard.widget.detail.DetailWidgetBean;
import com.finebi.conf.internalimp.dashboard.widget.detail.DetailWidget;
import com.finebi.conf.internalimp.dashboard.widget.filter.WidgetLinkItem;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftEmptyResult;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.transformer.SortAdaptor;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.DetailQueryInfo;
import com.fr.swift.config.conf.MetaDataConvertUtil;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.adapter.dimension.Cursor;
import com.fr.swift.query.adapter.dimension.DetailDimension;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.target.DetailFormulaTarget;
import com.fr.swift.query.adapter.target.DetailTarget;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftMetaDataImpl;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


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
                return new SwiftDetailTableResult(new SwiftEmptyResult(), 0);
            }
            result = new SwiftDetailTableResult(resultSet, widget.getTotalRows());
        } catch (Exception e) {
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
        filterInfos.add(FilterInfoFactory.transformFineFilter(widget.getFilters()));
//        FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(widget.getFilters());
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
        DetailWidgetBean bean = widget.getValue();
        if (null != bean) {
            Map<String, WidgetLinkItem> map = bean.getLinkage();
            if (null == map) {
                return fineFilters;
            }
            Iterator<Map.Entry<String, WidgetLinkItem>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                WidgetLinkItem item = iterator.next().getValue();
                try {
                    handleClickItem(item, fineFilters);
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
            String columnName = BusinessTableUtils.getFieldNameByFieldId(fineDimension.getFieldId());
            fields.add(metaData.getColumn(columnName));
        }
        return new SwiftMetaDataImpl(metaData.getTableName(), metaData.getRemark(), metaData.getSchemaName(), fields);
    }

    private static Dimension[] getDimension(DetailWidget widget) throws Exception {
        final List<FineDimension> fineDimensions = widget.getDimensionList();
        Dimension[] dimensions = new Dimension[fineDimensions.size()];
        for (int i = 0, size = fineDimensions.size(); i < size; i++) {
            FineDimension fineDimension = fineDimensions.get(i);
            String columnName = BusinessTableUtils.getFieldNameByFieldId(fineDimension.getFieldId());
            Sort sort = SortAdaptor.adaptorDimensionSort(fineDimension.getSort(), i);
            dimensions[i] = new DetailDimension(i, new SourceKey(fineDimension.getId()), new ColumnKey(columnName), GroupAdaptor.adaptDashboardGroup(fineDimension.getGroup()), sort, FilterInfoFactory.transformFineFilter(widget.getFilters()));
        }
        return dimensions;
    }


    private static DetailTarget[] getTargets(DetailWidget widget) {
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


}