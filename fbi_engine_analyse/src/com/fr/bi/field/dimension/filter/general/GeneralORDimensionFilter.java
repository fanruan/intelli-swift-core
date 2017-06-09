package com.fr.bi.field.dimension.filter.general;


import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.bi.report.result.BINode;
import com.fr.bi.report.result.TargetCalculator;

import java.util.Map;

public class GeneralORDimensionFilter extends GeneralDimensionFilter {

    private static final long serialVersionUID = 5257377024724929244L;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GeneralORDimensionFilter)) {
            return false;
        }

        return super.equals(o);
    }

    @Override
    public boolean showNode(BINode node, Map<String, TargetCalculator> targetsMap, ICubeDataLoader loader) {
        for (int i = 0, len = childs.length; i < len; i++) {
            if (childs[i] != null && childs[i].showNode(node, targetsMap, loader)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        GroupValueIndex index = null;
        for (int i = 0; i < childs.length; i++) {
            GroupValueIndex gvi = childs[i] == null ? null : childs[i].createFilterIndex(dimension, target, loader, userId);
            if (index == null) {
                index = gvi;
            } else {
                index = index.OR(gvi);
            }
        }
        return index;
    }
}