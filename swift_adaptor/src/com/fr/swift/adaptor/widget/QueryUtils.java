package com.fr.swift.adaptor.widget;

import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.utils.FineTableUtils;
import com.fr.swift.adaptor.transformer.IndexingDataSourceFactory;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.cal.info.SingleTableGroupQueryInfo;
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
     * @param dimension
     * @param filterInfo
     * @param id
     * @return
     */
    public static List getOneDimensionFilterValues(FineDimension dimension, FilterInfo filterInfo, String id) {
        try {
            String fieldId = dimension.getFieldId();
            FineBusinessTable fineBusinessTable = FineTableUtils.getTableByFieldId(fieldId);
            FineBusinessField fineBusinessField = fineBusinessTable.getFieldByFieldId(fieldId);
            DataSource baseDataSource = IndexingDataSourceFactory.transformDataSource(fineBusinessTable);
            GroupDimension groupDimension = new GroupDimension(0, baseDataSource.getSourceKey(), new ColumnKey(fineBusinessField.getName()), GroupAdaptor.adaptGroup(dimension.getGroup()), null, null);
            SingleTableGroupQueryInfo valueInfo = new SingleTableGroupQueryInfo(new RowCursor(), id, new Dimension[]{groupDimension}, new Metric[0], new GroupTarget[0], filterInfo, null);
            GroupByResultSet valuesResultSet = QueryRunnerProvider.getInstance().executeQuery(valueInfo);
            Iterator<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> it = valuesResultSet.getRowResultIterator();
            Map<Integer, Object> dic = valuesResultSet.getGlobalDictionaries().get(0);
            List values = new ArrayList();
            while (it.hasNext()){
                RowIndexKey<int[]> indexKey = it.next().getKey();
                Object v = dic.get(indexKey.getKey()[0]);
                if (v != null){
                    values.add(v);
                }
            }
            return values;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return new ArrayList();
    }

}
