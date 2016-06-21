package com.fr.bi.field.target.key.cal.configuration;

import com.fr.bi.stable.constant.BIReportConstant;

import java.util.Map;

public class BISumOfAboveCalTargetKey extends BIConfiguratedCalculatorTargetKey {

    /**
     *
     */
    private static final long serialVersionUID = 5169050385302976329L;

    public BISumOfAboveCalTargetKey(String targetName, String target_id,
                                    Map targetMap, int start_group) {
        super(targetName, target_id, targetMap, start_group);
    }

    @Override
    public int getSummaryType() {
        return BIReportConstant.SUMMARY_TYPE.SUM;
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