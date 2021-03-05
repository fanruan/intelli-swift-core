package com.fr.swift.cloud.query.info.bean.parser;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.service.SwiftMetaDataService;
import com.fr.swift.cloud.query.group.Groups;
import com.fr.swift.cloud.query.group.impl.NoGroupRule;
import com.fr.swift.cloud.query.group.info.IndexInfoImpl;
import com.fr.swift.cloud.query.info.bean.element.DimensionBean;
import com.fr.swift.cloud.query.info.bean.element.SortBean;
import com.fr.swift.cloud.query.info.element.dimension.DetailDimension;
import com.fr.swift.cloud.query.info.element.dimension.DetailFormulaDimension;
import com.fr.swift.cloud.query.info.element.dimension.Dimension;
import com.fr.swift.cloud.query.info.element.dimension.GroupDimension;
import com.fr.swift.cloud.query.info.element.dimension.GroupFormulaDimension;
import com.fr.swift.cloud.query.sort.AscSort;
import com.fr.swift.cloud.query.sort.DescSort;
import com.fr.swift.cloud.query.sort.Sort;
import com.fr.swift.cloud.query.sort.SortType;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/7.
 */
class DimensionParser {

    /**
     * TODO: 2018/6/7 解析各类bean过程中相关参数的合法性校验以及相关异常处理规范
     */
    static List<Dimension> parse(SourceKey table, List<DimensionBean> dimensionBeans, List<SortBean> sortBeans) {
        List<Dimension> dimensions = new ArrayList<Dimension>();
        for (int i = 0; i < dimensionBeans.size(); i++) {
            DimensionBean dimensionBean = dimensionBeans.get(i);
            ColumnKey columnKey = new ColumnKey(dimensionBean.getColumn());
            SortBean sortBean = getDimensionSort(dimensionBean.getColumn(), sortBeans);
            Sort sort = null;
            if (sortBean != null) {
                sort = sortBean.getType() == SortType.ASC ? new AscSort(i) : new DescSort(i);
            }
            switch (dimensionBean.getType()) {
                case GROUP:
                    dimensions.add(new GroupDimension(i, columnKey, Groups.newGroup(new NoGroupRule()), sort,
                            new IndexInfoImpl(true, false)));
                    break;
                case GROUP_FORMULA:
                    dimensions.add(new GroupFormulaDimension(i, Groups.newGroup(new NoGroupRule()), sort, FormulaParser.parse(dimensionBean.getFormula())));
                    break;
                case DETAIL:
                    dimensions.add(new DetailDimension(i, columnKey, Groups.newGroup(new NoGroupRule()), sort,
                            new IndexInfoImpl(true, false)));
                    break;
                case DETAIL_ALL_COLUMN: {
                    SwiftMetaData meta = SwiftContext.get().getBean(SwiftMetaDataService.class).getMeta(table);
                    List<String> fields = meta.getFieldNames();
                    for (int n = 0; n < fields.size(); n++) {
                        dimensions.add(new DetailDimension(n, new ColumnKey(fields.get(n)),
                                Groups.newGroup(new NoGroupRule()), sort,
                                new IndexInfoImpl(true, false)));
                    }
                    break;
                }
                case DETAIL_FORMULA:
                    dimensions.add(new DetailFormulaDimension(i, FormulaParser.parse(dimensionBean.getFormula())));
                    break;
                default:
            }
        }
        return dimensions;
    }

    private static SortBean getDimensionSort(String name, List<SortBean> sortBeans) {
        if (null != sortBeans && null != name) {
            for (SortBean sortBean : sortBeans) {
                if (Util.equals(sortBean.getName(), name)) {
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
            switch (dimensionBean.getType()) {
                case GROUP:
                    dimensions.add(new GroupDimension(i, columnKey, Groups.newGroup(new NoGroupRule()), null, new IndexInfoImpl(false, false)));
                    break;
                case DETAIL:
                    dimensions.add(new DetailDimension(i, columnKey, Groups.newGroup(new NoGroupRule()), null, new IndexInfoImpl(false, false)));
                    break;
                default:
            }
        }

        return dimensions;
    }
}
