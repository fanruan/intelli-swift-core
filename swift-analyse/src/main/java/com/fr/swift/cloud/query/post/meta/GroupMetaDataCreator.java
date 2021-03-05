package com.fr.swift.cloud.query.post.meta;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.entity.MetaDataColumnEntity;
import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.config.service.SwiftMetaDataService;
import com.fr.swift.cloud.db.SwiftDatabase;
import com.fr.swift.cloud.exception.meta.SwiftMetaDataException;
import com.fr.swift.cloud.query.aggregator.AggregatorType;
import com.fr.swift.cloud.query.info.bean.element.AggregationBean;
import com.fr.swift.cloud.query.info.bean.element.CalculatedFieldBean;
import com.fr.swift.cloud.query.info.bean.element.DimensionBean;
import com.fr.swift.cloud.query.info.bean.post.CalculatedFieldQueryInfoBean;
import com.fr.swift.cloud.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.cloud.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.cloud.query.info.bean.type.PostQueryType;
import com.fr.swift.cloud.query.info.funnel.FunnelAggregationBean;
import com.fr.swift.cloud.query.info.funnel.FunnelPathsAggregationBean;
import com.fr.swift.cloud.query.info.funnel.FunnelVirtualStep;
import com.fr.swift.cloud.query.info.funnel.group.post.PostGroupBean;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.source.SwiftMetaDataColumn;
import com.fr.swift.cloud.util.Strings;

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
        SwiftMetaData meta = SwiftContext.get().getBean(SwiftMetaDataService.class).getMeta(new SourceKey(queryBean.getTableName()));
        SwiftDatabase schema = meta.getSwiftDatabase();
        List<DimensionBean> dimensionBeans = queryBean.getDimensions();
        dimensionColumns = createDimension(meta, dimensionBeans);
        List<AggregationBean> metricBeans = queryBean.getAggregations();
        List<SwiftMetaDataColumn> metricColumns = createMetricBeanColumn(meta, metricBeans);
        List<SwiftMetaDataColumn> metaDataColumns = new ArrayList<SwiftMetaDataColumn>(dimensionColumns);
        metaDataColumns.addAll(metricColumns);
        List<PostQueryInfoBean> postQueryInfoBeans = queryBean.getPostAggregations();
        for (PostQueryInfoBean postQueryInfoBean : postQueryInfoBeans) {
            PostQueryType type = postQueryInfoBean.getType();
            switch (type) {
                case CAL_FIELD:
                    CalculatedFieldBean calculatedFieldBean = ((CalculatedFieldQueryInfoBean) postQueryInfoBean).getCalField();
                    String name = calculatedFieldBean.getName();
                    metaDataColumns.add(new MetaDataColumnEntity(name, null, Types.DOUBLE, null));
                    break;
                case FUNNEL_TIME_AVG:
                case FUNNEL_CONVERSION_RATE:
                case FUNNEL_TIME_MEDIAN:
                    FunnelPathsAggregationBean funnelMetric = getFunnelMetric(metricBeans);
                    if (null == funnelMetric) {
                        continue;
                    }
                    List<FunnelVirtualStep> events = funnelMetric.getSteps();
                    for (int i = 0; i < events.size() - 1; i++) {
                        FunnelVirtualStep event1 = events.get(i);
                        FunnelVirtualStep event2 = events.get(i + 1);
                        metaDataColumns.add(new MetaDataColumnEntity(event1.getName() + "-" + event2.getName(), null, Types.DOUBLE, null));
                    }
                    break;
                default:
            }
        }
        return new SwiftMetaDataEntity(null, schema, schema.getName(), tableName, tableName, metaDataColumns);
    }

    private FunnelPathsAggregationBean getFunnelMetric(List<AggregationBean> metricBeans) {
        for (AggregationBean metricBean : metricBeans) {
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

    private List<SwiftMetaDataColumn> createMetricBeanColumn(SwiftMetaData meta, List<AggregationBean> metricBeans) throws SwiftMetaDataException {
        List<SwiftMetaDataColumn> metaDataColumns = new ArrayList<SwiftMetaDataColumn>();
        for (AggregationBean metricBean : metricBeans) {
            switch (metricBean.getType()) {
                case FUNNEL:
                    createFunnelColumns((FunnelAggregationBean) metricBean, dimensionColumns, metaDataColumns);
                    break;
                case FUNNEL_PATHS:
                    dimensionColumns.add(new MetaDataColumnEntity("funnel_time", null, Types.VARCHAR, null));
                    dimensionColumns.add(new MetaDataColumnEntity("funnel_path", null, Types.VARCHAR, null));
                    metaDataColumns.add(new MetaDataColumnEntity("conversion_rate", null, Types.BIGINT, null));
                    break;
                default:
                    String alias = metricBean.getAlias();
                    String column = metricBean.getColumn();
                    SwiftMetaDataColumn metaDataColumn = Strings.isEmpty(column) ?
                            new MetaDataColumnEntity(Strings.EMPTY, null, Types.DOUBLE, null) : meta.getColumn(column);
                    String name = alias == null ? column : alias;
                    metaDataColumns.add(new MetaDataColumnEntity(name, metaDataColumn.getRemark(), getMetricColumnType(metaDataColumn.getType()),
                            metaDataColumn.getPrecision(), metaDataColumn.getScale(), metaDataColumn.getColumnId()));
            }

        }
        return metaDataColumns;
    }

    private void createFunnelColumns(FunnelAggregationBean funnelBean, List<SwiftMetaDataColumn> dimensionColumns, List<SwiftMetaDataColumn> metricColumns) {
        dimensionColumns.add(new MetaDataColumnEntity("funnel_time", null, Types.VARCHAR, null));
        PostGroupBean postGroup = funnelBean.getPostGroup();
        if (null != postGroup) {
            dimensionColumns.add(new MetaDataColumnEntity(postGroup.getColumn(), null, Types.VARCHAR, null));
        }
        List<FunnelVirtualStep> events = funnelBean.getSteps();
        for (int i = 0; i < events.size(); i++) {
            FunnelVirtualStep event = events.get(i);
            String name = null;
            if (Strings.isEmpty(event.getName())) {
                name = "funnel_event_" + (i + 1);
            } else {
                name = event.getName();
            }
            metricColumns.add(new MetaDataColumnEntity(name, null, Types.BIGINT, null));
        }
    }

    private int getMetricColumnType(int type) {
        switch (type) {
            case Types.DOUBLE:
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.BIT:
                return type;
            default:
                return Types.DOUBLE;
        }
    }

}
