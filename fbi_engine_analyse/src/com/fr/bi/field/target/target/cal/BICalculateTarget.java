package com.fr.bi.field.target.target.cal;

import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.field.target.target.BISummaryTarget;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * all from calculate
 *
 * @author Daniel
 */
public abstract class BICalculateTarget extends BISummaryTarget {

    protected Map<String, BITarget> targetMap;

    public abstract List<BISummaryTarget> createCalculateUseTarget(BISummaryTarget[] sumTarget);

    public abstract Collection<String> getCalculateUseTargetIDs();

    public Map<String, BITarget> getTargetMap() {
        return targetMap;
    }

    public void setTargetMap(Map<String, BITarget> targetMap) {
        this.targetMap = targetMap;
    }
}