package com.fr.bi.cal.analyze.cal.index.loader;

import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.TargetCalculator;

/**
 * Created by 小灰灰 on 2016/9/26.
 */
public class TargetAndKey {
    private TargetCalculator calculator;
    private TargetGettingKey targetGettingKey;

    public TargetCalculator getCalculator() {
        return calculator;
    }

    public TargetGettingKey getTargetGettingKey() {
        return targetGettingKey;
    }

    public TargetAndKey(TargetCalculator calculator, TargetGettingKey targetGettingKey) {
        this.calculator = calculator;
        this.targetGettingKey = targetGettingKey;
    }
}
