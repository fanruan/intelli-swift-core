package com.fr.swift.query.post.utils;

import com.fr.stable.StringUtils;
import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.info.bean.element.CalculatedFieldBean;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.post.CalculatedFieldQueryInfoBean;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.post.PostQueryType;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.meta.SwiftMetaDataImpl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/1.
 */
public class SwiftMetaDataUtils {

    public static SwiftMetaData createMetaData(QueryBean bean) throws SwiftMetaDataException {
        if (bean.getQueryType() == QueryType.GROUP) {
            return createGroupMetaData((GroupQueryInfoBean) bean);
        }
        return createDetailMetaData((DetailQueryInfoBean) bean);
    }

    private static SwiftMetaData createDetailMetaData(DetailQueryInfoBean bean) throws SwiftMetaDataException {
        final String tableName = bean.getTableName();
        SwiftMetaData meta = SwiftContext.get().getBean(SwiftMetaDataService.class).getMetaDataByKey(bean.getTableName());
        SwiftDatabase.Schema schema = meta.getSwiftSchema();
        List<String> fieldNames = new ArrayList<String>();
        List<SwiftMetaDataColumn> metaDataColumns = new ArrayList<SwiftMetaDataColumn>();
        List<DimensionBean> dimensionBeans = bean.getDimensionBeans();
        for (DimensionBean dimensionBean : dimensionBeans) {
            String alias = dimensionBean.getName();
            String column = dimensionBean.getColumn();
            if (alias == null) {
                fieldNames.add(column);
            } else {
                fieldNames.add(alias);
            }
            metaDataColumns.add(meta.getColumn(column));
        }
        return new SwiftMetaDataImpl(tableName, schema, fieldNames, metaDataColumns);
    }

    private static SwiftMetaData createGroupMetaData(GroupQueryInfoBean bean) throws SwiftMetaDataException {
        final String tableName = bean.getTableName();
        SwiftMetaData meta = SwiftContext.get().getBean(SwiftMetaDataService.class).getMetaDataByKey(bean.getTableName());
        SwiftDatabase.Schema schema = meta.getSwiftSchema();
        List<String> fieldNames = new ArrayList<String>();
        List<SwiftMetaDataColumn> metaDataColumns = new ArrayList<SwiftMetaDataColumn>();
        List<DimensionBean> dimensionBeans = bean.getDimensionBeans();
        for (DimensionBean dimensionBean : dimensionBeans) {
            String alias = dimensionBean.getName();
            String column = dimensionBean.getColumn();
            if (alias == null) {
                fieldNames.add(column);
            } else {
                fieldNames.add(alias);
            }
            metaDataColumns.add(meta.getColumn(column));
        }
        List<MetricBean> metricBeans = bean.getMetricBeans();
        for (MetricBean metricBean : metricBeans) {
            String alias = metricBean.getName();
            String column = metricBean.getColumn();
            if (alias == null) {
                fieldNames.add(column);
            } else {
                fieldNames.add(alias);
            }
            metaDataColumns.add(StringUtils.isEmpty(column) ?
                    new MetaDataColumnBean(null, null, Types.DOUBLE, null) : meta.getColumn(column));
        }
        List<PostQueryInfoBean> postQueryInfoBeans = bean.getPostQueryInfoBeans();
        for (PostQueryInfoBean postQueryInfoBean : postQueryInfoBeans) {
            if (postQueryInfoBean.getType() != PostQueryType.CAL_FIELD) {
                continue;
            }
            List<CalculatedFieldBean> calculatedFieldBeans = ((CalculatedFieldQueryInfoBean) postQueryInfoBean).getCalculatedFieldBeans();
            for (CalculatedFieldBean calculatedFieldBean : calculatedFieldBeans) {
                String name = calculatedFieldBean.getName();
                fieldNames.add(name);
                metaDataColumns.add(new MetaDataColumnBean(name, null, Types.DOUBLE, null));
            }
        }
        return new SwiftMetaDataImpl(tableName, schema, fieldNames, metaDataColumns);
    }
}
