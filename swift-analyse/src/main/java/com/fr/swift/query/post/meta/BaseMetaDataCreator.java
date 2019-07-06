package com.fr.swift.query.post.meta;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-07
 */
abstract class BaseMetaDataCreator<T extends QueryInfoBean> implements MetaDataCreator<T> {
    protected List<SwiftMetaDataColumn> createDimension(SwiftMetaData meta, List<DimensionBean> dimensionBeans) throws SwiftMetaDataException {
        List<SwiftMetaDataColumn> metaDataColumns = new ArrayList<SwiftMetaDataColumn>();
        for (DimensionBean dimensionBean : dimensionBeans) {
            String alias = dimensionBean.getAlias();
            String column = dimensionBean.getColumn();
            SwiftMetaDataColumn metaDataColumn = meta.getColumn(column);
            String name = alias == null ? column : alias;
            metaDataColumns.add(new MetaDataColumnBean(name, metaDataColumn.getRemark(), metaDataColumn.getType(),
                    metaDataColumn.getPrecision(), metaDataColumn.getScale(), metaDataColumn.getColumnId()));
        }
        return metaDataColumns;
    }
}
