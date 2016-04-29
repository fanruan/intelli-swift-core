package com.fr.bi.field.target.key.cal.configuration.summary;

import com.fr.bi.field.target.key.cal.configuration.BIConfiguratedCalculatorTargetKey;

import java.util.Map;

public abstract class BISummaryOfAllCalTargetKey extends BIConfiguratedCalculatorTargetKey {

    /**
     *
     */
    private static final long serialVersionUID = -1673630319417282474L;

    public BISummaryOfAllCalTargetKey(String targetName, String cal_target_name,
                                     Map targetMap, int start_group) {
        super(targetName, cal_target_name, targetMap, start_group);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (int) serialVersionUID;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

}