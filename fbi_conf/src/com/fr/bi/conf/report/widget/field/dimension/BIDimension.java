package com.fr.bi.conf.report.widget.field.dimension;

import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.operation.sort.ISort;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.report.result.BINode;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.TargetCalculator;

import java.util.List;
import java.util.Map;

/**
 * Created by GUY on 2015/4/14.
 */
public interface BIDimension extends BITargetAndDimension {
    IGroup getGroup();

    void setGroup(IGroup group);

    ISort getSort();

    int getSortType();

    void setSortType(int sortType);

    String getSortTarget();

    Object toFilterObject(Object data);

    String toString(Object v);

    DimensionFilter getFilter();

    void setFilter(DimensionFilter filter);

    DimensionCalculator createCalculator(BusinessField column, List<BITableSourceRelation> relations);

    List<String> getUsedTargets();

    boolean showNode(BINode node,
                     Map<String, TargetCalculator> targetsMap);

    Object getValueByType(Object data);
}