package com.fr.swift.adaptor.widget;

import com.finebi.conf.constant.BIReportConstant.SORT;
import com.finebi.conf.internalimp.dashboard.widget.detail.DetailWidget;
import com.finebi.conf.provider.SwiftTableConfProvider;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionSort;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftEmptyResult;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.transformer.IndexingDataSourceFactory;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.DetailQueryInfo;
import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.config.conf.MetaDataConvertUtil;
import com.fr.swift.query.adapter.dimension.DetailDimension;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.target.DetailFormulaTarget;
import com.fr.swift.query.adapter.target.DetailTarget;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.NoneSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;

import java.util.List;


/**
 * @author pony
 * @date 2017/12/21
 * 明细表
 */
public class DetailWidgetAdaptor {

    public static BIDetailTableResult calculate(DetailWidget widget) {
        BIDetailTableResult result = null;
        SwiftResultSet resultSet;
        try {
            resultSet = QueryRunnerProvider.getInstance().executeQuery(buildQueryInfo(widget));
            if (resultSet == null) {
                return new SwiftDetailTableResult(new SwiftEmptyResult());
            }
            result = new SwiftDetailTableResult(resultSet);
        } catch (Exception e) {
        }

        return result;
    }

    static QueryInfo buildQueryInfo(DetailWidget widget) throws Exception {

        Cursor cursor = null;
        String queryId = widget.getWidgetId();
        Dimension[] dimensions = getDimension(widget);

        FineBusinessTable table = new SwiftTableConfProvider().getSingleTable(widget.getTableName());
        SourceKey target = IndexingDataSourceFactory.transformDataSource(table).getSourceKey();
        SwiftMetaData swiftMetaData = MetaDataConvertUtil.getSwiftMetaDataBySourceKey(target.toString());
        DetailTarget[] targets = getTargets(widget);
        //没传进来排序顺序
        IntList sortIndex = IntListFactory.createHeapIntList();
        for (int i = 0; i < dimensions.length; i++) {
            sortIndex.add(i);
        }

//        IntList sortIndex = null;
        FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(widget.getFilters());
        return new DetailQueryInfo(cursor, queryId, dimensions, target, targets, sortIndex, filterInfo, swiftMetaData);
    }

    private static Dimension[] getDimension(DetailWidget widget) throws Exception {
        final List<FineDimension> fineDimensions = widget.getDimensionList();
        Dimension[] dimensions = new Dimension[fineDimensions.size()];
        for (int i = 0, size = fineDimensions.size(); i < size; i++) {
            FineDimension fineDimension = fineDimensions.get(i);
            Sort sort = fineDimension.getSort() == null ? null : adaptSort(fineDimension.getSort(), i);
            dimensions[i] = new DetailDimension(i, new SourceKey(fineDimension.getId()), new ColumnKey(fineDimension.getText()),
                    GroupAdaptor.adaptGroup(fineDimension.getGroup()), sort,
                    FilterInfoFactory.transformFineFilter(widget.getFilters()));
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

    private static Sort adaptSort(FineDimensionSort sort, int index) {
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