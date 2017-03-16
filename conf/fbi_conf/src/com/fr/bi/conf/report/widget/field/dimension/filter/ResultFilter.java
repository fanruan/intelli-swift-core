package com.fr.bi.conf.report.widget.field.dimension.filter;


import com.fr.bi.base.provider.ParseJSONWithUID;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.report.result.BINode;
import com.fr.bi.stable.report.result.TargetCalculator;

import java.util.Map;

public interface ResultFilter extends IDissolubleResultFilter, ParseJSONWithUID {

    /**
     * @param node
     * @param targetsMap
     * @return
     */
    @Override
    boolean showNode(BINode node, Map<String, TargetCalculator> targetsMap, ICubeDataLoader loader);
}