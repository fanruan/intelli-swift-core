package com.fr.swift.cloud.query.info.bean.parser;

import com.fr.swift.cloud.compare.Comparators;
import com.fr.swift.cloud.db.impl.SwiftDatabase;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.query.filter.SwiftDetailFilterType;
import com.fr.swift.cloud.query.filter.info.FilterInfo;
import com.fr.swift.cloud.query.filter.info.GeneralFilterInfo;
import com.fr.swift.cloud.query.filter.info.NotFilterInfo;
import com.fr.swift.cloud.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.cloud.query.filter.info.value.SwiftNumberInRangeFilterValue;
import com.fr.swift.cloud.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.DetailFilterInfoBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.NFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.NotFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.NullFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.value.RangeFilterValueBean;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.source.ColumnTypeConstants;
import com.fr.swift.cloud.source.ColumnTypeUtils;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaDataColumn;
import com.fr.swift.cloud.util.DateUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Lyon on 2018/6/7.
 */
public class FilterInfoParser {

    public static FilterInfo parse(SourceKey table, FilterInfoBean bean) {
        // TODO: 2018/7/11 化简过滤条件，这边的使用策略可以结合具体场景更智能一点
        if (null == bean) {
            return new SwiftDetailFilterInfo<Object>(null, null, SwiftDetailFilterType.ALL_SHOW);
        }
        // TODO: 2019/9/12 多过滤条件性能差到无法跑出结果了。也可能是我不会用吧
//        bean = FilterInfoBeanOptimizer.optimize(bean);
        // TODO: 2019/9/12 反正有bug，也不敢说，先注释了吧。
//        bean = FilterInfoBeanSimplify.simple(bean);
        switch (bean.getType()) {
            case AND:
            case OR:
                List<FilterInfoBean> filterInfoBeans = (List<FilterInfoBean>) bean.getFilterValue();
                List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
                List<NumberInRangeFilterBean> betweenFilter = new ArrayList<NumberInRangeFilterBean>();
                if (null != filterInfoBeans) {
                    for (FilterInfoBean filterInfoBean : filterInfoBeans) {
                        if (bean.getType() == SwiftDetailFilterType.AND && filterInfoBean.getType() == SwiftDetailFilterType.NUMBER_IN_RANGE) {
                            betweenFilter.add((NumberInRangeFilterBean) filterInfoBean);
                        } else {
                            filterInfoList.add(parse(table, filterInfoBean));
                        }
                    }
                }
                filterInfoList.addAll(rewriteAndFilter(table, betweenFilter));
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
            case STRING_LIKE_IGNORE_CASE:
            case KEY_WORDS: {
                Object filterValue = bean.getFilterValue();
                return createDetailFilterInfo((DetailFilterInfoBean) bean, filterValue == null ? "" : filterValue.toString());
            }
            case NUMBER_IN_RANGE: {
                RangeFilterValueBean valueBean = (RangeFilterValueBean) bean.getFilterValue();
                ColumnTypeConstants.ClassType classType = getClassType(table, ((NumberInRangeFilterBean) bean).getColumn());
                SwiftNumberInRangeFilterValue filterValue = new SwiftNumberInRangeFilterValue();
                if (valueBean.getStart() != null) {
                    filterValue.setMin(convertNumber(valueBean.getStart(), classType));
                }
                if (valueBean.getEnd() != null) {
                    filterValue.setMax(convertNumber(valueBean.getEnd(), classType));
                }
                filterValue.setMinIncluded(valueBean.isStartIncluded());
                filterValue.setMaxIncluded(valueBean.isEndIncluded());
                return createDetailFilterInfo((DetailFilterInfoBean) bean, filterValue);
            }
            default:
                return createDetailFilterInfo((DetailFilterInfoBean) bean, null);
        }
    }

    // 虽然通过query重写的方式能处理查询这块的兼容和优化问题，但是重写过程比较繁琐。最好通过好的api设计及完善的文档来引导用户合理使用api
    private static List<FilterInfo> rewriteAndFilter(SourceKey table, List<NumberInRangeFilterBean> betweenFilter) {
        List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
        if (betweenFilter.isEmpty()) {
            return filterInfoList;
        }
        // 按字段分组
        Map<String, List<NumberInRangeFilterBean>> name2beanMap = new HashMap<String, List<NumberInRangeFilterBean>>();
        for (NumberInRangeFilterBean bean : betweenFilter) {
            List<NumberInRangeFilterBean> beans = name2beanMap.get(bean.getColumn());
            if (beans == null) {
                beans = new ArrayList<NumberInRangeFilterBean>();
                name2beanMap.put(bean.getColumn(), beans);
            }
            beans.add(bean);
        }
        for (Map.Entry<String, List<NumberInRangeFilterBean>> entry : name2beanMap.entrySet()) {
            ColumnTypeConstants.ClassType classType = getClassType(table, entry.getKey());
            Comparator valueComparator = initComparator(table, entry.getKey());
            final Comparator<RangeFilterValueBean> beanComparator = new BeanComparator(classType, valueComparator);
            final Comparator<NumberInRangeFilterBean> comparator = new Comparator<NumberInRangeFilterBean>() {
                @Override
                public int compare(NumberInRangeFilterBean o1, NumberInRangeFilterBean o2) {
                    return beanComparator.compare(o1.getFilterValue(), o2.getFilterValue());
                }
            };
            List<NumberInRangeFilterBean> beans = entry.getValue();
            // 排序
            Collections.sort(beans, comparator);
            // 合并区间
            List<NumberInRangeFilterBean> mergedBeans = new ArrayList<NumberInRangeFilterBean>();
            NumberInRangeFilterBean prev = null;
            for (NumberInRangeFilterBean inter : beans) {
                if (prev == null || valueComparator.compare(
                        convertValue(inter.getFilterValue().getStart(), classType, true),
                        convertValue(prev.getFilterValue().getEnd(), classType, false)) > 0) {
                    mergedBeans.add(inter);
                    prev = inter;
                } else if (valueComparator.compare(inter.getFilterValue().getEnd(), prev.getFilterValue().getEnd()) > 0) {
                    prev.getFilterValue().setEnd(inter.getFilterValue().getEnd());
                    prev.getFilterValue().setEndIncluded(inter.getFilterValue().isEndIncluded());
                }
            }
            // 解析重写后的bean
            for (FilterInfoBean filterInfoBean : mergedBeans) {
                filterInfoList.add(parse(table, filterInfoBean));
            }
        }
        return filterInfoList;
    }

    private static Object convertValue(Object origin, ColumnTypeConstants.ClassType classType, boolean start) {
        switch (classType) {
            case INTEGER:
                return origin == null ? (start ? Integer.MIN_VALUE : Integer.MAX_VALUE) : Integer.parseInt(origin.toString());
            case LONG:
                return origin == null ? (start ? Long.MIN_VALUE : Long.MAX_VALUE) : Long.parseLong(origin.toString());
            case DATE:
                return origin == null ? (start ? Long.MIN_VALUE : Long.MAX_VALUE) : DateUtils.string2Date(origin.toString()).getTime();
            default:
                return origin == null ? (start ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY) : Double.parseDouble(origin.toString());
        }
    }

    private static Comparator initComparator(SourceKey table, String column) {
        ColumnTypeConstants.ClassType classType = getClassType(table, column);
        switch (classType) {
            case DOUBLE:
                return Comparators.<Double>asc();
            default:
                return Comparators.<Long>asc();
        }
    }

    private static class BeanComparator implements Comparator<RangeFilterValueBean> {

        private ColumnTypeConstants.ClassType classType;
        private Comparator comparator;

        public BeanComparator(ColumnTypeConstants.ClassType classType, Comparator comparator) {
            this.classType = classType;
            this.comparator = comparator;
        }

        @Override
        public int compare(RangeFilterValueBean o1, RangeFilterValueBean o2) {
            return comparator.compare(convertValue(o1.getStart(), classType, false), convertValue(o2.getStart(), classType, false));
        }
    }

    private static SwiftDetailFilterInfo createDetailFilterInfo(DetailFilterInfoBean bean, Object filterValue) {
        ColumnKey columnKey = new ColumnKey(bean.getColumn());
        return new SwiftDetailFilterInfo(columnKey, filterValue, bean.getType());
    }

    private static Set parseSet(SourceKey table, String columnName, Set values) {
        Set<Object> set = new HashSet<Object>();
        if (null == values) {
            return set;
        }
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
                return Integer.parseInt(origin.toString());
            case LONG:
                return Long.parseLong(origin.toString());
            case DATE:
                return DateUtils.string2Date(origin.toString()).getTime();
            case DOUBLE:
                return Double.parseDouble(origin.toString());
            default:
                return origin == null ? "" : origin.toString();
        }
    }

    /**
     * postQuery的，如果count(字符字段) 只用convert取出来的object是String
     * 强转成Number会抛异常
     *
     * @param origin
     * @param type
     * @return
     */
    private static Number convertNumber(Object origin, ColumnTypeConstants.ClassType type) {
        final Object convert = convert(origin, type);
        return convert instanceof Number ? (Number) convert : Double.parseDouble(convert.toString());
    }
}
