package com.fr.swift.query.post.meta;

import com.fr.swift.SwiftContext;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.SwiftSchema;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.CalculatedFieldBean;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.FunnelAggregationBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.FunnelEventBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.FunnelPathsAggregationBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.group.post.PostGroupBean;
import com.fr.swift.query.info.bean.post.CalculatedFieldQueryInfoBean;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.type.PostQueryType;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Strings;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lyon
 * @date 2018/11/28
 */
public class GroupMetaDataCreator extends BaseMetaDataCreator<GroupQueryInfoBean> {
    private List<SwiftMetaDataColumn> dimensionColumns;

    @Override
    public SwiftMetaData create(GroupQueryInfoBean queryBean) throws SwiftMetaDataException {
        final String tableName = queryBean.getTableName();
        SwiftMetaData meta = SwiftContext.get().getBean(SwiftMetaDataService.class).getMetaDataByKey(queryBean.getTableName());
        SwiftSchema schema = meta.getSwiftSchema();
        List<DimensionBean> dimensionBeans = queryBean.getDimensions();
        dimensionColumns = createDimension(meta, dimensionBeans);
        List<MetricBean> metricBeans = queryBean.getAggregations();
        List<SwiftMetaDataColumn> metricColumns = createMetricBeanColumn(meta, metricBeans);
        List<SwiftMetaDataColumn> metaDataColumns = new ArrayList<SwiftMetaDataColumn>(dimensionColumns);
        metaDataColumns.addAll(metricColumns);
        List<PostQueryInfoBean> postQueryInfoBeans = queryBean.getPostAggregations();
        for (PostQueryInfoBean postQueryInfoBean : postQueryInfoBeans) {
            PostQueryType type = postQueryInfoBean.getType();
            switch (type) {
                case CAL_FIELD:
                    break;
                case FUNNEL_TIME_AVG:
                case FUNNEL_CONVERSION_RATE:
                case FUNNEL_TIME_MEDIAN:
                    FunnelPathsAggregationBean funnelMetric = getFunnelMetric(metricBeans);
                    if (null == funnelMetric) {
                        continue;
                    }
                    List<FunnelEventBean> events = funnelMetric.getEvents();
                    for (int i = 0; i < events.size() - 1; i++) {
                        FunnelEventBean event1 = events.get(i);
                        FunnelEventBean event2 = events.get(i + 1);
                        metaDataColumns.add(new MetaDataColumnBean(event1.getName() + "-" + event2.getName(), null, Types.DOUBLE, null));
                    }
                    break;
                default:
                    CalculatedFieldBean calculatedFieldBean = ((CalculatedFieldQueryInfoBean) postQueryInfoBean).getCalField();
                    String name = calculatedFieldBean.getName();
                    metaDataColumns.add(new MetaDataColumnBean(name, null, Types.DOUBLE, null));
            }
        }
        return new SwiftMetaDataBean(null, schema, schema.getName(), tableName, tableName, metaDataColumns);
    }

    private FunnelPathsAggregationBean getFunnelMetric(List<MetricBean> metricBeans) {
        for (MetricBean metricBean : metricBeans) {
            AggregatorType aggregatorType = metricBean.getType();
            switch (aggregatorType) {
                case FUNNEL:
                case FUNNEL_PATHS:
                    return (FunnelPathsAggregationBean) metricBean;
                default:
            }
        }
        return null;
    }

    private List<SwiftMetaDataColumn> createMetricBeanColumn(SwiftMetaData meta, List<MetricBean> metricBeans) throws SwiftMetaDataException {
        List<SwiftMetaDataColumn> metaDataColumns = new ArrayList<SwiftMetaDataColumn>();
        for (MetricBean metricBean : metricBeans) {
            switch (metricBean.getType()) {
                case FUNNEL:
                    createFunnelColumns((FunnelAggregationBean) metricBean, dimensionColumns, metaDataColumns);
                    break;
                case FUNNEL_PATHS:
                    dimensionColumns.add(new MetaDataColumnBean("funnel_time", null, Types.VARCHAR, null));
                    dimensionColumns.add(new MetaDataColumnBean("funnel_path", null, Types.VARCHAR, null));
                    metaDataColumns.add(new MetaDataColumnBean("conversion_rate", null, Types.BIGINT, null));
                    break;
                default:
                    String alias = metricBean.getAlias();
                    String column = metricBean.getColumn();
                    SwiftMetaDataColumn metaDataColumn = Strings.isEmpty(column) ?
                            new MetaDataColumnBean(Strings.EMPTY, null, Types.DOUBLE, null) : meta.getColumn(column);
                    String name = alias == null ? column : alias;
                    metaDataColumns.add(new MetaDataColumnBean(name, metaDataColumn.getRemark(), metaDataColumn.getType(),
                            metaDataColumn.getPrecision(), metaDataColumn.getScale(), metaDataColumn.getColumnId()));
            }

        }
        return metaDataColumns;
    }

    private void createFunnelColumns(FunnelAggregationBean funnelBean, List<SwiftMetaDataColumn> dimensionColumns, List<SwiftMetaDataColumn> metricColumns) {
        dimensionColumns.add(new MetaDataColumnBean("funnel_time", null, Types.VARCHAR, null));
        PostGroupBean postGroup = funnelBean.getPostGroup();
        if (null != postGroup) {
            dimensionColumns.add(new MetaDataColumnBean(postGroup.getColumn(), null, Types.VARCHAR, null));
        }
        List<FunnelEventBean> events = funnelBean.getEvents();
        for (int i = 0; i < events.size(); i++) {
            FunnelEventBean event = events.get(i);
            String name = null;
            if (Strings.isEmpty(event.getName())) {
                name = "funnel_event_" + (i + 1);
            } else {
                name = event.getName();
            }
            metricColumns.add(new MetaDataColumnBean(name, null, Types.BIGINT, null));
        }
    }

}
