package com.fr.swift.query.post.meta;

import com.fr.swift.SwiftContext;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyon on 2018/11/28.
 */
public class DetailMetaDataCreator implements MetaDataCreator<DetailQueryInfoBean> {

    @Override
    public SwiftMetaData create(DetailQueryInfoBean bean) throws SwiftMetaDataException {
        final String tableName = bean.getTableName();
        SwiftMetaData meta = SwiftContext.get().getBean(SwiftMetaDataService.class).getMetaDataByKey(bean.getTableName());
        SwiftDatabase schema = meta.getSwiftDatabase();
        List<SwiftMetaDataColumn> metaDataColumns = new ArrayList<SwiftMetaDataColumn>();
        List<DimensionBean> dimensionBeans = bean.getDimensions();
        if (dimensionBeans.size() == 1 && dimensionBeans.get(0).getType() == DimensionType.DETAIL_ALL_COLUMN) {
            return meta;
        }
        for (DimensionBean dimensionBean : dimensionBeans) {
            String alias = dimensionBean.getAlias();
            String column = dimensionBean.getColumn();
            SwiftMetaDataColumn metaDataColumn = meta.getColumn(column);
            String name = alias == null ? column : alias;
            metaDataColumns.add(new MetaDataColumnBean(name, metaDataColumn.getRemark(), metaDataColumn.getType(),
                    metaDataColumn.getPrecision(), metaDataColumn.getScale(), metaDataColumn.getColumnId()));
        }
        return new SwiftMetaDataBean(null, schema, schema.getName(), tableName, tableName, metaDataColumns);
    }
}
