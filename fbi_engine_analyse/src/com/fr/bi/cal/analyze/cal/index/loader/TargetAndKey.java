package com.fr.bi.cal.analyze.cal.index.loader;

import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.TargetCalculator;

/**
 * Created by 小灰灰 on 2016/9/26.
 */
public class TargetAndKey {
    private TargetCalculator calculator;
    private TargetGettingKey targetGettingKey;
    private String targetId;

    public TargetAndKey(String targetId, TargetCalculator calculator, TargetGettingKey targetGettingKey) {
        this.calculator = calculator;
        this.targetGettingKey = targetGettingKey;
        this.targetId = targetId;
    }

    public TargetCalculator getCalculator() {
        return calculator;
    }

    public TargetGettingKey getTargetGettingKey() {
        return targetGettingKey;
    }

    public String getTargetId() {
        return targetId;
    }
}
