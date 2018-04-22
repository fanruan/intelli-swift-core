package com.fr.swift.adaptor.widget;

import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.result.group.AllCursor;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.dimension.GroupDimension;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.NodeResultSetImpl;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.SourceKey;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2018/3/24.
 */
public class QueryUtils {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(QueryUtils.class);

    /**
     * 获取一个维度过滤之后的值，各种控件都会用到
     *
     * @param dimension
     * @param filterInfo
     * @param id
     * @return
     */
    public static List getOneDimensionFilterValues(FineDimension dimension, FilterInfo filterInfo, String id) {
        try {
            String fieldId = dimension.getFieldId();
            SourceKey sourceKey = new SourceKey(BusinessTableUtils.getSourceIdByFieldId(fieldId));
            String fieldName = BusinessTableUtils.getFieldNameByFieldId(fieldId);
            GroupDimension groupDimension = new GroupDimension(0, sourceKey, new ColumnKey(fieldName), GroupAdaptor.adaptDashboardGroup(dimension.getGroup()), null, null);
            GroupQueryInfo valueInfo = new GroupQueryInfo(new AllCursor(), id, sourceKey, filterInfo, new Dimension[]{groupDimension}, new Metric[0], new GroupTarget[0], null);
            NodeResultSetImpl nodeResultSet = (NodeResultSetImpl) QueryRunnerProvider.getInstance().executeQuery(valueInfo);
            SwiftNode n = nodeResultSet.getNode();
            List values = new ArrayList();
            for (int i = 0; i < n.getChildrenSize(); i++){
                if (n.getChild(i).getData() != null){
                    values.add(n.getChild(i).getData());
                }
            }
            return values;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return new ArrayList();
    }

}
