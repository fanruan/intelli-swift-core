package com.fr.swift.adaptor.log;

import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.restriction.Restriction;
import com.fr.stable.query.sort.SortItem;
import com.fr.swift.db.Table;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NotFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.OrFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.StringOneValueFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.value.RangeFilterValueBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.query.sort.SortType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * This class created on 2018/4/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class QueryConditionAdaptor {

    public static QueryInfoBean adaptCondition(QueryCondition condition, Table swiftTable, List<String> fieldNames) {
        DetailQueryInfoBean queryInfoBean = new DetailQueryInfoBean();
        queryInfoBean.setQueryId(UUID.randomUUID().toString());
        String tableName = swiftTable.getSourceKey().getId();
        queryInfoBean.setTableName(tableName);

        List<DimensionBean> dimensions = new ArrayList<DimensionBean>();
        for (int i = 0; i < fieldNames.size(); i++) {
            // TODO: 2018/6/21 维度上的排序没适配
            DimensionBean bean = new DimensionBean();
            bean.setColumn(fieldNames.get(i));
            bean.setType(DimensionType.DETAIL);
            dimensions.add(bean);
        }
        queryInfoBean.setDimensions(dimensions);

        List<SortBean> sorts = new ArrayList<SortBean>();
        List<SortItem> sortItems = condition.getSortList();
        for (SortItem sortItem : sortItems) {
            SortBean bean = new SortBean();
            bean.setName(sortItem.getColumnName());
            bean.setType(sortItem.isDesc() ? SortType.DESC : SortType.ASC);
            sorts.add(bean);
        }
        queryInfoBean.setSorts(sorts);

        queryInfoBean.setFilter(restriction2FilterInfo(condition.getRestriction()));
        return queryInfoBean;
    }

    public static QueryInfoBean adaptCondition(QueryCondition condition, Table swiftTable) throws SQLException {
        return adaptCondition(condition, swiftTable, swiftTable.getMeta().getFieldNames());
    }

    public static FilterInfoBean restriction2FilterInfo(Restriction restriction) {
        return adaptFilters(restriction);
    }

    private static Set<Object> obj2String(Set<Object> objects) {
        Set values = new HashSet<Object>();
        for (Object obj : objects) {
            if (obj == null) {
                values.add("");
            } else {
                values.add(obj.toString());
            }
        }
        return values;
    }

    private static FilterInfoBean adaptFilters(Restriction restriction) {
        String columnName = restriction.getColumnName();
        Object value = restriction.getColumnValue();
        value = value == null ? "" : value.toString();
        Set<Object> values = (Set<Object>) restriction.getColumnValues();
        values = values == null ? new HashSet<Object>() : obj2String(values);
        FilterInfoBean filterInfoBean = null;
        switch (restriction.getType()) {
            case EQ:
                filterInfoBean = new InFilterBean();
                ((InFilterBean) filterInfoBean).setColumn(columnName);
                filterInfoBean.setFilterValue(Collections.singleton(value));
                break;
            case NEQ: {
                filterInfoBean = new NotFilterBean();
                FilterInfoBean in = new InFilterBean();
                ((InFilterBean) in).setColumn(columnName);
                in.setFilterValue(Collections.singleton(value));
                filterInfoBean.setFilterValue(in);
                break;
            }
            case GT:
                filterInfoBean = new NumberInRangeFilterBean();
                ((NumberInRangeFilterBean) filterInfoBean).setColumn(columnName);
                RangeFilterValueBean gtValue = new RangeFilterValueBean();
                gtValue.setStart(String.valueOf(value));
                filterInfoBean.setFilterValue(gtValue);
                break;
            case GTE:
                filterInfoBean = new NumberInRangeFilterBean();
                ((NumberInRangeFilterBean) filterInfoBean).setColumn(columnName);
                RangeFilterValueBean gteValue = new RangeFilterValueBean();
                gteValue.setStart(String.valueOf(value));
                gteValue.setStartIncluded(true);
                filterInfoBean.setFilterValue(gteValue);
                break;
            case LT:
                filterInfoBean = new NumberInRangeFilterBean();
                ((NumberInRangeFilterBean) filterInfoBean).setColumn(columnName);
                RangeFilterValueBean ltValue = new RangeFilterValueBean();
                ltValue.setEnd(String.valueOf(value));
                filterInfoBean.setFilterValue(ltValue);
                break;
            case LTE:
                filterInfoBean = new NumberInRangeFilterBean();
                ((NumberInRangeFilterBean) filterInfoBean).setColumn(columnName);
                RangeFilterValueBean lteValue = new RangeFilterValueBean();
                lteValue.setEnd(String.valueOf(value));
                lteValue.setEndIncluded(true);
                filterInfoBean.setFilterValue(lteValue);
                break;
            case IN:
                filterInfoBean = new InFilterBean();
                ((InFilterBean) filterInfoBean).setColumn(columnName);
                filterInfoBean.setFilterValue(values);
                break;
            case NIN: {
                filterInfoBean = new NotFilterBean();
                FilterInfoBean in = new InFilterBean();
                ((InFilterBean) filterInfoBean).setColumn(columnName);
                in.setFilterValue(values);
                filterInfoBean.setFilterValue(in);
                break;
            }
            case LIKE:
                filterInfoBean = new StringOneValueFilterBean();
                filterInfoBean.setType(SwiftDetailFilterType.STRING_LIKE);
                ((StringOneValueFilterBean) filterInfoBean).setColumn(columnName);
                filterInfoBean.setFilterValue(value);
                break;
            case STARTWITH:
                filterInfoBean = new StringOneValueFilterBean();
                filterInfoBean.setType(SwiftDetailFilterType.STRING_STARTS_WITH);
                ((StringOneValueFilterBean) filterInfoBean).setColumn(columnName);
                filterInfoBean.setFilterValue(value);
                break;
            case ENDWITH:
                filterInfoBean = new StringOneValueFilterBean();
                filterInfoBean.setType(SwiftDetailFilterType.STRING_ENDS_WITH);
                ((StringOneValueFilterBean) filterInfoBean).setColumn(columnName);
                filterInfoBean.setFilterValue(value);
                break;
            case AND: {
                filterInfoBean = new AndFilterBean();
                List<FilterInfoBean> andFilters = new ArrayList<FilterInfoBean>();
                for (Restriction childRestriction : restriction.getChildRestrictions()) {
                    andFilters.add(adaptFilters(childRestriction));
                }
                filterInfoBean.setFilterValue(andFilters);
                break;
            }
            case OR: {
                filterInfoBean = new OrFilterBean();
                List<FilterInfoBean> orFilters = new ArrayList<FilterInfoBean>();
                for (Restriction childRestriction : restriction.getChildRestrictions()) {
                    orFilters.add(adaptFilters(childRestriction));
                }
                filterInfoBean.setFilterValue(orFilters);
            }
            default:
        }
        return filterInfoBean;
    }

}
