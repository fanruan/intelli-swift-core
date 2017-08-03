package com.fr.bi.cal.analyze.cal.index.loader;

import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.general.ComparatorUtils;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TargetAndKey that = (TargetAndKey) o;
        //只要比较下calculator就行了，calculator里包含了target信息，这个类只是封装这些信息，调用方便
        return ComparatorUtils.equals(calculator, that.calculator);
    }
}
