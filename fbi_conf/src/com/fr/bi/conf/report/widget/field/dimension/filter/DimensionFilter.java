package com.fr.bi.conf.report.widget.field.dimension.filter;


import com.fr.bi.conf.report.filter.FieldFilter;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.report.result.BINode;
import com.fr.bi.stable.report.result.TargetCalculator;

import java.util.List;
import java.util.Map;

public interface DimensionFilter extends FieldFilter {

    List<String> getUsedTargets();

    boolean canCreateDirectFilter();

    boolean showNode(BINode node, Map<String, TargetCalculator> targetsMap, ICubeDataLoader loader);
}