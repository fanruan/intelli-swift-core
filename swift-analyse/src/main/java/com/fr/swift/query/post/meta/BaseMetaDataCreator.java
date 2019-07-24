package com.fr.swift.query.post.meta;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.exception.meta.SwiftMetaDataColumnAbsentException;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.FormulaBean;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.Types;
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
            MetaDataColumnBean columnBean = null;
            String name = alias == null ? column : alias;
            try {
                SwiftMetaDataColumn metaDataColumn = meta.getColumn(column);
                columnBean = new MetaDataColumnBean(name, metaDataColumn.getRemark(), metaDataColumn.getType(),
                        metaDataColumn.getPrecision(), metaDataColumn.getScale(), metaDataColumn.getColumnId());
            } catch (SwiftMetaDataColumnAbsentException e) {
                // 没有字段（todate这种直接传时间戳的，没有column的）
                columnBean = new MetaDataColumnBean(name, Types.VARCHAR);
            }
            FormulaBean formula = dimensionBean.getFormula();
            if (null != formula) {
                switch (formula.getType()) {
                    case TO_DATE_FORMAT:
                        columnBean.setType(Types.VARCHAR);
                        break;
                    case TO_DATE:
                        columnBean.setType(Types.DATE);
                        break;
                    default:
                        break;
                }
            }
            metaDataColumns.add(columnBean);
        }
        return metaDataColumns;
    }
}
