package com.fr.swift.adaptor.widget;

import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.fr.swift.adaptor.transformer.SortAdaptor;
import com.fr.swift.adaptor.transformer.filter.dimension.DimensionFilterAdaptor;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.group.info.cursor.AllCursor;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.dimension.DimensionInfo;
import com.fr.swift.query.info.element.dimension.DimensionInfoImpl;
import com.fr.swift.query.info.element.dimension.GroupDimension;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.info.element.target.TargetInfo;
import com.fr.swift.query.info.element.target.cal.ResultTarget;
import com.fr.swift.query.info.element.target.cal.TargetInfoImpl;
import com.fr.swift.query.info.group.GroupQueryInfoImpl;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2018/3/24
 */
public class QueryUtils {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(QueryUtils.class);

    /**
     * 获取一个维度过滤之后的值，各种控件都会用到
     *
     * @param dimension
     * @param filterInfo 已经包含了dimension里面的明细过滤了
     * @param id
     * @return
     */
    public static List getOneDimensionFilterValues(FineDimension dimension, FilterInfo filterInfo, String id) {
        try {
            String fieldId = dimension.getFieldId();
            SourceKey sourceKey = new SourceKey(BusinessTableUtils.getSourceIdByFieldId(fieldId));
            String fieldName = BusinessTableUtils.getFieldNameByFieldId(fieldId);
            FilterInfo currentDimensionFilter = DimensionFilterAdaptor.transformDimensionFineFilter(dimension);
            GroupDimension groupDimension = new GroupDimension(0, sourceKey, new ColumnKey(fieldName),
                    GroupAdaptor.adaptDashboardGroup(dimension),
                    SortAdaptor.adaptorDimensionSort(dimension.getSort(), 0), currentDimensionFilter);
            TargetInfo targetInfo = new TargetInfoImpl(0, new ArrayList<Metric>(0), new ArrayList<GroupTarget>(0), new ArrayList<ResultTarget>(0), new ArrayList<Aggregator>(0));
            DimensionInfo dimensionInfo = new DimensionInfoImpl(
                    new AllCursor(),
                    filterInfo,
                    null, new Dimension[]{groupDimension}
            );
            GroupQueryInfoImpl valueInfo = new GroupQueryInfoImpl(id, sourceKey, dimensionInfo, targetInfo);
            NodeResultSet nodeResultSet = QueryRunnerProvider.getInstance().executeQuery(valueInfo);
            SwiftNode n = nodeResultSet.getNode();
            List values = new ArrayList();
            for (int i = 0; i < n.getChildrenSize(); i++) {
                // 如果有空值，这边把空值也一起加进来了
                values.add(n.getChild(i).getData());
            }
            return values;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return new ArrayList();
    }

}
