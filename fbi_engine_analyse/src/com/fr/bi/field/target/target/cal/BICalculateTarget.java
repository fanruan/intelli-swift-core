package com.fr.bi.field.target.target.cal;

import com.fr.bi.field.target.target.BIAbstractTarget;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.report.key.TargetGettingKey;

import java.util.List;
import java.util.Map;

/**
 * all from calculate
 *
 * @author Daniel
 */
public abstract class BICalculateTarget extends BISummaryTarget {

    protected Map<String, TargetGettingKey> targetMap;

    public abstract List<BIAbstractTarget> createCalculateUseTarget(BIAbstractTarget[] sumTarget);

    ;

    public Map<String, TargetGettingKey> getTargetMap() {
        return targetMap;
    }

    public void setTargetMap(Map<String, TargetGettingKey> targetMap) {
        this.targetMap = targetMap;
    }
}