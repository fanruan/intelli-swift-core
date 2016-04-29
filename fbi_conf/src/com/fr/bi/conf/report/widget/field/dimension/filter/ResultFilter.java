package com.fr.bi.conf.report.widget.field.dimension.filter;


import com.fr.bi.base.provider.ParseJSONWithUID;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.report.result.TargetCalculator;

import java.util.Map;

public interface ResultFilter extends IDissolubleResultFilter, ParseJSONWithUID {

    @Override
    public boolean needParentRelation();

    /**
     * @param node
     * @param targetsMap
     * @return
     */
    @Override
    public boolean showNode(LightNode node,
                            Map<String, TargetCalculator> targetsMap, ICubeDataLoader loader);


}