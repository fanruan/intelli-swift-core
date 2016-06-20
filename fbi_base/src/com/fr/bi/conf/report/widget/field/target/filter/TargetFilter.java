package com.fr.bi.conf.report.widget.field.target.filter;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.report.filter.FieldFilter;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.stable.xml.XMLable;

public interface TargetFilter extends XMLable, FieldFilter {

    /**
     * 指标上加的过滤
     *
     * @param target
     * @param loader
     * @param userID
     * @return
     */
    GroupValueIndex createFilterIndex(BusinessTable target, ICubeDataLoader loader, long userID);

    boolean hasAllCalculatorFilter();
}