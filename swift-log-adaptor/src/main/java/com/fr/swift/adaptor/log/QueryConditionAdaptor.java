package com.fr.swift.adaptor.log;

import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.restriction.Restriction;
import com.fr.stable.query.sort.SortItem;
import com.fr.swift.db.Table;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.NotFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.filter.info.value.SwiftNumberInRangeFilterValue;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.element.dimension.DetailDimension;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.target.DetailTarget;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2018/4/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class QueryConditionAdaptor {

    public static QueryInfo adaptCondition(QueryCondition condition, Table swiftTable, List<String> fieldNames) throws SQLException {
        SwiftMetaData metaData = swiftTable.getMeta();
        SourceKey sourceKey = swiftTable.getSourceKey();

        String queryId = sourceKey.getId();
        List<Dimension> dimensions = new ArrayList<Dimension>();

        List<Sort> sorts = new ArrayList<Sort>();
        List<SortItem> sortItems = condition.getSortList();
        for (SortItem sortItem : sortItems) {
            int columnIndex = metaData.getColumnIndex(sortItem.getColumnName()) - 1;
            sorts.add(sortItem.isDesc() ? new DescSort(columnIndex) : new AscSort(columnIndex));
        }
        for (int i = 0; i < fieldNames.size(); i++) {
            dimensions.add(new DetailDimension(i, sourceKey, new ColumnKey(fieldNames.get(i)), null, null));
        }
        List<DetailTarget> targets = null;
        return new DetailQueryInfo(queryId, sourceKey, restriction2FilterInfo(condition.getRestriction()),
                dimensions, sorts, targets, metaData);
    }

    public static QueryInfo adaptCondition(QueryCondition condition, Table swiftTable) throws SQLException {
        return adaptCondition(condition, swiftTable, swiftTable.getMeta().getFieldNames());
    }

    static FilterInfo restriction2FilterInfo(Restriction restriction) {
        return new GeneralFilterInfo(adaptFilters(restriction), GeneralFilterInfo.AND);
    }

    private static List<FilterInfo> adaptFilters(Restriction restriction) {
        List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
        String columnName = restriction.getColumnName();
        Object value = restriction.getColumnValue();
        Set<Object> values = (Set<Object>) restriction.getColumnValues();

        switch (restriction.getType()) {
            case EQ:
                filterInfos.add(new SwiftDetailFilterInfo<Set<String>>(new ColumnKey(columnName), (Set) Collections.singleton(value), SwiftDetailFilterType.IN));
                break;
            case NEQ:
                filterInfos.add(new NotFilterInfo(new SwiftDetailFilterInfo<Set<String>>(new ColumnKey(columnName), (Set) Collections.singleton(value), SwiftDetailFilterType.IN)));
                break;
            case GT:
                SwiftNumberInRangeFilterValue gtValue = new SwiftNumberInRangeFilterValue();
                gtValue.setMin((Double.parseDouble(String.valueOf(value))));
                filterInfos.add(new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(new ColumnKey(columnName), gtValue, SwiftDetailFilterType.NUMBER_IN_RANGE));
                break;
            case GTE:
                SwiftNumberInRangeFilterValue gteValue = new SwiftNumberInRangeFilterValue();
                gteValue.setMin((Double.parseDouble(String.valueOf(value))));
                gteValue.setMinIncluded(true);
                filterInfos.add(new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(new ColumnKey(columnName), gteValue, SwiftDetailFilterType.NUMBER_IN_RANGE));
                break;
            case LT:
                SwiftNumberInRangeFilterValue ltValue = new SwiftNumberInRangeFilterValue();
                ltValue.setMax((Double.parseDouble(String.valueOf(value))));
                filterInfos.add(new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(new ColumnKey(columnName), ltValue, SwiftDetailFilterType.NUMBER_IN_RANGE));
                break;
            case LTE:
                SwiftNumberInRangeFilterValue lteValue = new SwiftNumberInRangeFilterValue();
                lteValue.setMax((Double.parseDouble(String.valueOf(value))));
                lteValue.setMaxIncluded(true);
                filterInfos.add(new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(new ColumnKey(columnName), lteValue, SwiftDetailFilterType.NUMBER_IN_RANGE));
                break;
            case IN:
                filterInfos.add(new SwiftDetailFilterInfo<Set<Object>>(new ColumnKey(columnName), values, SwiftDetailFilterType.IN));
                break;
            case NIN:
                filterInfos.add(new NotFilterInfo(new SwiftDetailFilterInfo<Set<Object>>(new ColumnKey(columnName), values, SwiftDetailFilterType.IN)));
                break;
            case LIKE:
                filterInfos.add(new SwiftDetailFilterInfo<String>(new ColumnKey(columnName), (String) value, SwiftDetailFilterType.STRING_LIKE));
                break;
            case STARTWITH:
                filterInfos.add(new SwiftDetailFilterInfo<String>(new ColumnKey(columnName), (String) value, SwiftDetailFilterType.STRING_STARTS_WITH));
                break;
            case ENDWITH:
                filterInfos.add(new SwiftDetailFilterInfo<String>(new ColumnKey(columnName), (String) value, SwiftDetailFilterType.STRING_ENDS_WITH));
                break;
            case AND:
                List<FilterInfo> andFilters = new ArrayList<FilterInfo>();
                for (Restriction childRestriction : restriction.getChildRestrictions()) {
                    andFilters.addAll(adaptFilters(childRestriction));
                }
                filterInfos.add(new GeneralFilterInfo(andFilters, GeneralFilterInfo.AND));
                break;
            case OR:
                List<FilterInfo> orFilters = new ArrayList<FilterInfo>();
                for (Restriction childRestriction : restriction.getChildRestrictions()) {
                    orFilters.addAll(adaptFilters(childRestriction));
                }
                filterInfos.add(new GeneralFilterInfo(orFilters, GeneralFilterInfo.OR));
            default:
        }
        return filterInfos;
    }

}
