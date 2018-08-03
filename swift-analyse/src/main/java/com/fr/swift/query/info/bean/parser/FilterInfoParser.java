package com.fr.swift.query.info.bean.parser;

import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.NotFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.filter.info.value.SwiftNumberInRangeFilterValue;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.DetailFilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NotFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NullFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.value.RangeFilterValueBean;
import com.fr.swift.query.info.bean.parser.optimize.FilterInfoBeanOptimizer;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2018/6/7.
 */
class FilterInfoParser {

    static FilterInfo parse(SourceKey table, FilterInfoBean bean) {
        // TODO: 2018/7/11 化简过滤条件，这边的使用策略可以结合具体场景更智能一点
        if (null == bean) {
            return new SwiftDetailFilterInfo<Object>(null, null, SwiftDetailFilterType.ALL_SHOW);
        }
        bean = FilterInfoBeanOptimizer.optimize(bean);
        switch (bean.getType()) {
            case AND:
            case OR:
                List<FilterInfoBean> filterInfoBeans = (List<FilterInfoBean>) bean.getFilterValue();
                List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
                if (null != filterInfoBeans) {
                    for (FilterInfoBean filterInfoBean : filterInfoBeans) {
                        filterInfoList.add(parse(table, filterInfoBean));
                    }
                }
                return new GeneralFilterInfo(filterInfoList, bean.getType() == SwiftDetailFilterType.OR ? GeneralFilterInfo.OR : GeneralFilterInfo.AND);
            case NOT:
                FilterInfoBean filterInfoBean = ((NotFilterBean) bean).getFilterValue();
                FilterInfo filterInfo = parse(table, filterInfoBean);
                return new NotFilterInfo(filterInfo);
            case NULL:
                return new SwiftDetailFilterInfo<Object>(new ColumnKey(((NullFilterBean) bean).getColumn()), null,
                        SwiftDetailFilterType.NULL);
            case ALL_SHOW:
                return new SwiftDetailFilterInfo<Object>(null, null, SwiftDetailFilterType.ALL_SHOW);
            case EMPTY:
                return new SwiftDetailFilterInfo<Object>(null, null, SwiftDetailFilterType.EMPTY);
            case IN: {
                InFilterBean inFilterBean = (InFilterBean) bean;
                ColumnKey columnKey = new ColumnKey(inFilterBean.getColumn());
                columnKey.setRelation(RelationSourceParser.parse(inFilterBean.getRelation()));
                return new SwiftDetailFilterInfo<Set<Object>>(columnKey,
                        parseSet(table, inFilterBean.getColumn(), inFilterBean.getFilterValue()), SwiftDetailFilterType.IN);
            }
            case BOTTOM_N:
            case TOP_N: {
                Integer n = ((NFilterBean) bean).getFilterValue();
                return createDetailFilterInfo((DetailFilterInfoBean) bean, n == null ? 0 : n);
            }
            case STRING_LIKE:
            case STRING_ENDS_WITH:
            case STRING_STARTS_WITH:
            case KEY_WORDS: {
                Object filterValue = bean.getFilterValue();
                return createDetailFilterInfo((DetailFilterInfoBean) bean, filterValue == null ? "" : filterValue.toString());
            }
            case NUMBER_IN_RANGE: {
                RangeFilterValueBean valueBean = (RangeFilterValueBean) bean.getFilterValue();
                ColumnTypeConstants.ClassType classType = getClassType(table, ((NumberInRangeFilterBean) bean).getColumn());
                SwiftNumberInRangeFilterValue filterValue = new SwiftNumberInRangeFilterValue();
                if (valueBean.getStart() != null) {
                    filterValue.setMin((Number) convert(valueBean.getStart(), classType));
                }
                if (valueBean.getEnd() != null) {
                    filterValue.setMax((Number) convert(valueBean.getEnd(), classType));
                }
                filterValue.setMinIncluded(valueBean.isStartIncluded());
                filterValue.setMaxIncluded(valueBean.isEndIncluded());
                return createDetailFilterInfo((DetailFilterInfoBean) bean, filterValue);
            }
            default:
                return createDetailFilterInfo((DetailFilterInfoBean) bean, null);
        }
    }

    private static SwiftDetailFilterInfo createDetailFilterInfo(DetailFilterInfoBean bean, Object filterValue) {
        ColumnKey columnKey = new ColumnKey(bean.getColumn());
        columnKey.setRelation(RelationSourceParser.parse(bean.getRelation()));
        return new SwiftDetailFilterInfo(columnKey, filterValue, bean.getType());
    }

    private static Set parseSet(SourceKey table, String columnName, Set values) {
        Set<Object> set = new HashSet<Object>();
        ColumnTypeConstants.ClassType type = getClassType(table, columnName);
        for (Object object : values) {
            set.add(convert(object, type));
        }
        return set;
    }

    private static ColumnTypeConstants.ClassType getClassType(SourceKey table, String columnName) {
        SwiftMetaDataColumn column = null;
        try {
            column = SwiftDatabase.getInstance().getTable(table).getMetadata().getColumn(columnName);
        } catch (SQLException e) {
            SwiftLoggers.getLogger(FilterInfoParser.class).error("failed to read metadata of table: " + table.toString());
        }
        return ColumnTypeUtils.getClassType(column);
    }

    private static Object convert(Object origin, ColumnTypeConstants.ClassType type) {
        switch (type) {
            case INTEGER:
            case LONG:
            case DATE:  // TODO: 2018/7/3 date这边如果要支持多种日志format格式要另做解析，暂时默认日期都传毫秒时间戳
                return Long.parseLong(origin.toString());
            case DOUBLE:
                return Double.parseDouble(origin.toString());
            default:
                return origin == null ? "" : origin.toString();
        }
    }
}
