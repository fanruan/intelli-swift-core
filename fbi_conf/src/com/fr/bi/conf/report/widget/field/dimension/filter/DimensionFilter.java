package com.fr.bi.conf.report.widget.field.dimension.filter;


import com.fr.bi.conf.report.filter.FieldFilter;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.report.result.TargetCalculator;

import java.util.List;
import java.util.Map;

public interface DimensionFilter extends FieldFilter {

    public List<String> getUsedTargets();

    boolean canCreateDirectFilter();

    boolean needParentRelation();

    boolean showNode(LightNode node, Map<String, TargetCalculator> targetsMap, ICubeDataLoader loader);
}