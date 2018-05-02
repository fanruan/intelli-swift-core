package com.fr.swift.adaptor.log;

import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.restriction.Restriction;
import com.fr.stable.query.sort.SortItem;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.DetailQueryInfo;
import com.fr.swift.db.Table;
import com.fr.swift.query.adapter.dimension.Cursor;
import com.fr.swift.query.adapter.dimension.DetailDimension;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.target.DetailTarget;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.filter.info.value.SwiftNumberInRangeFilterValue;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.NoneSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2018/4/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class QueryConditionAdaptor {

    public static QueryInfo adaptorCondition(QueryCondition condition, Table swiftTable) throws SQLException {
        SwiftMetaData metaData = swiftTable.getMeta();
        SourceKey sourceKey = swiftTable.getSourceKey();

        Cursor cursor = null;
        String queryId = sourceKey.getId();
        Dimension[] dimensions = new Dimension[metaData.getColumnCount()];

        IntList sortIndex = IntListFactory.createHeapIntList();
        List<SortItem> sortItems = condition.getSortList();
        Map<String, SortItem> sortMap = new HashMap<String, SortItem>();
        for (SortItem sortItem : sortItems) {
            sortIndex.add(metaData.getColumnIndex(sortItem.getColumnName()));
            sortMap.put(sortItem.getColumnName(), sortItem);
        }

        for (int i = 0; i < dimensions.length; i++) {
            SortItem sortItem = sortMap.get(metaData.getColumnName(i + 1));
            Sort sort = new NoneSort();
            if (sortItem != null) {
                if (sortItem.isDesc()) {
                    sort = new DescSort(i);
                } else {
                    sort = new AscSort(i);
                }
            }
            dimensions[i] = new DetailDimension(i, sourceKey, new ColumnKey(metaData.getColumnName(i + 1)), null, sort, null);
        }
        DetailTarget[] targets = null;
        List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
        Restriction restriction = condition.getRestriction();
        filterInfos.addAll(adaptFilters(restriction));
        return new DetailQueryInfo(cursor, queryId, dimensions, sourceKey, targets, sortIndex, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND), metaData);
    }

    private static List<FilterInfo> adaptFilters(Restriction restriction) {
        List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
        String columnName = restriction.getColumnName();
        Object value = restriction.getColumnValue();
        Set<Object> values = (Set<Object>) restriction.getColumnValues();

        switch (restriction.getType()) {
            case EQ:
                filterInfos.add(new SwiftDetailFilterInfo<Set<String>>(columnName, (Set) Collections.singleton(value), SwiftDetailFilterType.STRING_IN));
                break;
            case NEQ:
                filterInfos.add(new SwiftDetailFilterInfo<Set<String>>(columnName, (Set) Collections.singleton(value), SwiftDetailFilterType.STRING_NOT_IN));
                break;
            case GT:
                SwiftNumberInRangeFilterValue gtValue = new SwiftNumberInRangeFilterValue();
                gtValue.setMin((Double.parseDouble(String.valueOf(value))));
                filterInfos.add(new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnName, gtValue, SwiftDetailFilterType.NUMBER_IN_RANGE));
                break;
            case GTE:
                SwiftNumberInRangeFilterValue gteValue = new SwiftNumberInRangeFilterValue();
                gteValue.setMin((Double.parseDouble(String.valueOf(value))));
                gteValue.setMinIncluded(true);
                filterInfos.add(new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnName, gteValue, SwiftDetailFilterType.NUMBER_IN_RANGE));
                break;
            case LT:
                SwiftNumberInRangeFilterValue ltValue = new SwiftNumberInRangeFilterValue();
                ltValue.setMax((Double.parseDouble(String.valueOf(value))));
                filterInfos.add(new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnName, ltValue, SwiftDetailFilterType.NUMBER_IN_RANGE));
                break;
            case LTE:
                SwiftNumberInRangeFilterValue lteValue = new SwiftNumberInRangeFilterValue();
                lteValue.setMax((Double.parseDouble(String.valueOf(value))));
                lteValue.setMaxIncluded(true);
                filterInfos.add(new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnName, lteValue, SwiftDetailFilterType.NUMBER_IN_RANGE));
                break;
            case IN:
                filterInfos.add(new SwiftDetailFilterInfo<Set<Object>>(columnName, values, SwiftDetailFilterType.STRING_IN));
                break;
            case NIN:
                filterInfos.add(new SwiftDetailFilterInfo<Set<Object>>(columnName, values, SwiftDetailFilterType.STRING_NOT_IN));
                break;
            case LIKE:
                filterInfos.add(new SwiftDetailFilterInfo<String>(columnName, (String) value, SwiftDetailFilterType.STRING_LIKE));
                break;
            case STARTWITH:
                filterInfos.add(new SwiftDetailFilterInfo<String>(columnName, (String) value, SwiftDetailFilterType.STRING_STARTS_WITH));
                break;
            case ENDWITH:
                filterInfos.add(new SwiftDetailFilterInfo<String>(columnName, (String) value, SwiftDetailFilterType.STRING_ENDS_WITH));
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
