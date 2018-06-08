package com.fr.swift.query.info.bean.parser;

import com.fr.general.ComparatorUtils;
import com.fr.swift.query.group.Groups;
import com.fr.swift.query.group.impl.NoGroupRule;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.dimension.GroupDimension;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/7.
 */
class DimensionParser {

    // TODO: 2018/6/7 解析各类bean过程中相关参数的合法性校验以及相关异常处理规范
    static List<Dimension> parse(List<DimensionBean> dimensionBeans, List<SortBean> sortBeans) {
        List<Dimension> dimensions = new ArrayList<Dimension>();
        for (int i = 0; i < dimensionBeans.size(); i++) {
            String fieldName = dimensionBeans.get(i).getFieldName();
            SortBean sortBean = getDimensionSort(fieldName, sortBeans);
            Sort sort = null;
            if (sortBean != null) {
                sort = sortBean.getType() == SortType.ASC ? new AscSort(i) : new DescSort(i);
            }
            // TODO: 2018/6/7 维度分组
            dimensions.add(new GroupDimension(0, new SourceKey(fieldName), new ColumnKey(fieldName), Groups.newGroup(new NoGroupRule()), sort, null));
        }
        return dimensions;
    }

    private static SortBean getDimensionSort(String name, List<SortBean> sortBeans) {
        for (SortBean sortBean : sortBeans) {
            if (ComparatorUtils.equals(sortBean.getFieldName(), name)) {
                return sortBean;
            }
        }
        return null;
    }

    static List<Dimension> parse(List<String> joinedFields) {
        List<Dimension> dimensions = new ArrayList<Dimension>();
        for (String field : joinedFields) {
            dimensions.add(new GroupDimension(0, new SourceKey(field), new ColumnKey(field), Groups.newGroup(new NoGroupRule()), null, null));
        }
        return dimensions;
    }
}
