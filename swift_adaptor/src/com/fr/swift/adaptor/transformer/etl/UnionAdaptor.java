package com.fr.swift.adaptor.transformer.etl;

import com.finebi.conf.constant.BIConfConstants;
import com.finebi.conf.internalimp.analysis.bean.operator.union.UnionBean;
import com.finebi.conf.internalimp.analysis.bean.operator.union.UnionBeanValue;
import com.fr.general.ComparatorUtils;
import com.fr.swift.source.etl.union.UnionOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/4/11
 */
public class UnionAdaptor {
    public static UnionOperator fromUnionBean(UnionBean ub) {
        UnionBeanValue ubv = ub.getValue();

        List<List<String>> basis = ubv.getBasis();
        int basisSize = basis.size();
        List<List<String>> listsOfColumn = new ArrayList<List<String>>(basisSize);

        for (int i = 0; i < ubv.getResult().size(); i++) {
            listsOfColumn.add(new ArrayList<String>());
            listsOfColumn.get(i).add(ubv.getResult().get(i));
            for (List<String> columnKeys : basis) {
                String columnName = columnKeys.get(i);
                if (ComparatorUtils.equals(columnName, BIConfConstants.CONF.EMPTY_FIELD)) {
                    listsOfColumn.get(i).add(null);
                } else {
                    listsOfColumn.get(i).add(columnName);
                }
            }
        }

        return new UnionOperator(listsOfColumn);
    }
}