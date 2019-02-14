package com.fr.swift.query.post.meta;

import com.fr.swift.SwiftContext;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.info.bean.element.CalculatedFieldBean;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.MetricBean;
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
 * Created by lyon on 2018/11/28.
 */
public class GroupMetaDataCreator implements MetaDataCreator<GroupQueryInfoBean> {

    @Override
    public SwiftMetaData create(GroupQueryInfoBean queryBean) throws SwiftMetaDataException {
        final String tableName = queryBean.getTableName();
        SwiftMetaData meta = SwiftContext.get().getBean(SwiftMetaDataService.class).getMetaDataByKey(queryBean.getTableName());
        SwiftDatabase schema = meta.getSwiftDatabase();
        List<SwiftMetaDataColumn> metaDataColumns = new ArrayList<SwiftMetaDataColumn>();
        List<DimensionBean> dimensionBeans = queryBean.getDimensions();
        for (DimensionBean dimensionBean : dimensionBeans) {
            String alias = dimensionBean.getAlias();
            String column = dimensionBean.getColumn();
            SwiftMetaDataColumn metaDataColumn = meta.getColumn(column);
            String name = alias == null ? column : alias;
            metaDataColumns.add(new MetaDataColumnBean(name, metaDataColumn.getRemark(), metaDataColumn.getType(),
                    metaDataColumn.getPrecision(), metaDataColumn.getScale(), metaDataColumn.getColumnId()));
        }
        List<MetricBean> metricBeans = queryBean.getAggregations();
        for (MetricBean metricBean : metricBeans) {
            String alias = metricBean.getAlias();
            String column = metricBean.getColumn();
            SwiftMetaDataColumn metaDataColumn = Strings.isEmpty(column) ?
                    new MetaDataColumnBean(Strings.EMPTY, null, Types.DOUBLE, null) : meta.getColumn(column);
            String name = alias == null ? column : alias;
            metaDataColumns.add(new MetaDataColumnBean(name, metaDataColumn.getRemark(), metaDataColumn.getType(),
                    metaDataColumn.getPrecision(), metaDataColumn.getScale(), metaDataColumn.getColumnId()));
        }
        List<PostQueryInfoBean> postQueryInfoBeans = queryBean.getPostAggregations();
        for (PostQueryInfoBean postQueryInfoBean : postQueryInfoBeans) {
            if (postQueryInfoBean.getType() != PostQueryType.CAL_FIELD) {
                continue;
            }
            CalculatedFieldBean calculatedFieldBean = ((CalculatedFieldQueryInfoBean) postQueryInfoBean).getCalField();
            String name = calculatedFieldBean.getName();
            metaDataColumns.add(new MetaDataColumnBean(name, null, Types.DOUBLE, null));
        }
        return new SwiftMetaDataBean(null, schema, schema.getName(), tableName, tableName, metaDataColumns);
    }
}
