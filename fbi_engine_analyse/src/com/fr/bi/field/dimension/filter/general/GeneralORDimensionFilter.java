package com.fr.bi.field.dimension.filter.general;


import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.report.result.TargetCalculator;

import java.util.Map;

public class GeneralORDimensionFilter extends GeneralDimensionFilter {

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
    public boolean showNode(LightNode node, Map<String, TargetCalculator> targetsMap, ICubeDataLoader loader) {
        for (int i = 0, len = childs.length; i < len; i++) {
            if (childs[i] != null && childs[i].showNode(node, targetsMap, loader)) {
                return true;
            }
        }
        return false;
    }
}