package com.fr.swift.query.info.bean.parser;

import com.fr.general.ComparatorUtils;
import com.fr.swift.query.group.Groups;
import com.fr.swift.query.group.impl.NoGroupRule;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.swift.query.info.element.dimension.DetailDimension;
import com.fr.swift.query.info.element.dimension.DetailFormulaDimension;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.dimension.GroupDimension;
import com.fr.swift.query.info.element.dimension.GroupFormulaDimension;
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
            DimensionBean dimensionBean = dimensionBeans.get(i);
            ColumnKey columnKey = new ColumnKey(dimensionBean.getColumn());
            columnKey.setRelation(RelationSourceParser.parse(dimensionBean.getRelation()));
            SortBean sortBean = getDimensionSort(dimensionBean.getColumn(), sortBeans);
            Sort sort = null;
            if (sortBean != null) {
                sort = sortBean.getType() == SortType.ASC ? new AscSort(i) : new DescSort(i);
            }
            // TODO: 2018/6/7 维度分组
            switch (dimensionBean.getDimensionType()) {
                case DETAIL_FORMULA:
                    dimensions.add(new DetailFormulaDimension(i, new SourceKey(dimensionBean.getTable()), dimensionBean.getFormula()));
                    break;
                case GROUP_FORMULA:
                    dimensions.add(new GroupFormulaDimension(i, new SourceKey(dimensionBean.getTable()), Groups.newGroup(new NoGroupRule()), sort, dimensionBean.getFormula()));
                    break;
                case GROUP:
                    dimensions.add(new GroupDimension(i, new SourceKey(dimensionBean.getTable()), columnKey, Groups.newGroup(new NoGroupRule()), sort));
                    break;
                case DETAIL:
                    dimensions.add(new DetailDimension(i, new SourceKey(dimensionBean.getTable()), columnKey, Groups.newGroup(new NoGroupRule()), sort));
                    break;
            }
        }
        return dimensions;
    }

    private static SortBean getDimensionSort(String name, List<SortBean> sortBeans) {
        if (null != sortBeans && null != name) {
            for (SortBean sortBean : sortBeans) {
                if (ComparatorUtils.equals(sortBean.getColumn(), name)) {
                    return sortBean;
                }
            }
        }
        return null;
    }

    static List<Dimension> parse(List<DimensionBean> joinedFields) {
        List<Dimension> dimensions = new ArrayList<Dimension>();
        for (int i = 0; i < joinedFields.size(); i++) {
            DimensionBean dimensionBean = joinedFields.get(i);
            ColumnKey columnKey = new ColumnKey(dimensionBean.getColumn());
            columnKey.setRelation(RelationSourceParser.parse(dimensionBean.getRelation()));
            switch (dimensionBean.getDimensionType()) {
                case DETAIL_FORMULA:
                    dimensions.add(new DetailFormulaDimension(i, new SourceKey(dimensionBean.getTable()), dimensionBean.getFormula()));
                    break;
                case GROUP_FORMULA:
                    dimensions.add(new GroupFormulaDimension(i, new SourceKey(dimensionBean.getTable()), Groups.newGroup(new NoGroupRule()), null, dimensionBean.getFormula()));
                    break;
                case GROUP:
                    dimensions.add(new GroupDimension(i, new SourceKey(dimensionBean.getTable()), columnKey, Groups.newGroup(new NoGroupRule()), null));
                    break;
                case DETAIL:
                    dimensions.add(new DetailDimension(i, new SourceKey(dimensionBean.getTable()), columnKey, Groups.newGroup(new NoGroupRule()), null));
                    break;
            }
        }

        return dimensions;
    }
}
