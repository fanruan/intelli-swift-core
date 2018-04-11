package com.fr.swift.adaptor.widget;

import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.info.SingleTableGroupQueryInfo;
import com.fr.swift.cal.info.TableGroupQueryInfo;
import com.fr.swift.cal.result.group.AllCursor;
import com.fr.swift.cal.result.group.RowCursor;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.dimension.GroupDimension;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
            FineBusinessTable fineBusinessTable = BusinessTableUtils.getTableByFieldId(fieldId);
            FineBusinessField fineBusinessField = fineBusinessTable.getFieldByFieldId(fieldId);
            DataSource baseDataSource = DataSourceFactory.transformDataSource(fineBusinessTable);
            GroupDimension groupDimension = new GroupDimension(0, baseDataSource.getSourceKey(), new ColumnKey(fineBusinessField.getName()), GroupAdaptor.adaptGroup(dimension.getGroup()), null, null);
            SingleTableGroupQueryInfo valueInfo = new SingleTableGroupQueryInfo(new AllCursor(), id, new Dimension[]{groupDimension}, new Metric[0], new GroupTarget[0], filterInfo, null);
            GroupByResultSet<int[]> valuesResultSet = (GroupByResultSet<int[]>) QueryRunnerProvider.getInstance().executeQuery(valueInfo);
            Iterator<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> it = valuesResultSet.getResultList().iterator();
            Map<Integer, Object> dic = valuesResultSet.getRowGlobalDictionaries().get(0);
            List values = new ArrayList();
            while (it.hasNext()) {
                RowIndexKey<int[]> indexKey = it.next().getKey();
                Object v = dic.get(indexKey.getKey()[0]);
                if (v != null) {
                    values.add(v);
                }
            }
            return values;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return new ArrayList();
    }

    public static List getOneDimensionFilterValues(List<FineDimension> dimensions, List<String> cursor,
                                                   FilterInfo filterInfo, String id) {
        try {
            // TODO: 2018/3/29 这边先假设组件只使用一张表里面的字段吧
            List<Dimension> dimensionList = createGroupDimension(dimensions);
            DataSource dataSource = getDataSource(dimensions.get(0));
            TableGroupQueryInfo tableGroupQueryInfo = new TableGroupQueryInfo(
                    dimensionList.toArray(new Dimension[dimensionList.size()]), new Metric[0], dataSource.getSourceKey());
            // TODO: 2018/3/29 先不管分页全部取出来吧
            QueryInfo queryInfo = new GroupQueryInfo(new RowCursor(cursor), id, filterInfo,
                    new TableGroupQueryInfo[]{tableGroupQueryInfo},
                    dimensionList.toArray(new Dimension[dimensionList.size()]),
                    new Metric[0], new GroupTarget[0], null);
            SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfo);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return new ArrayList();
    }

    private static DataSource getDataSource(FineDimension fineDimension) throws Exception {
        String fieldId = fineDimension.getFieldId();
        FineBusinessTable fineBusinessTable = BusinessTableUtils.getTableByFieldId(fieldId);
        return DataSourceFactory.transformDataSource(fineBusinessTable);
    }

    private static List<Dimension> createGroupDimension(List<FineDimension> fineDimensions) throws Exception {
        List<Dimension> dimensionList = new ArrayList<Dimension>();
        for (int i = 0; i < fineDimensions.size(); i++) {
            String fieldId = fineDimensions.get(i).getFieldId();
            String fieldName = BusinessTableUtils.getFieldNameByFieldId(fieldId);
            DataSource dataSource = getDataSource(fineDimensions.get(i));
            GroupDimension groupDimension = new GroupDimension(i, dataSource.getSourceKey(),
                    new ColumnKey(fieldName),
                    GroupAdaptor.adaptGroup(fineDimensions.get(i).getGroup()), null, null);
            dimensionList.add(groupDimension);
        }
        return dimensionList;
    }
}
