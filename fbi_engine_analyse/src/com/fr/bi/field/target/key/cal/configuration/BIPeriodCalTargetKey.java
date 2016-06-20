package com.fr.bi.field.target.key.cal.configuration;

import com.fr.bi.stable.constant.BIReportConstant;

import java.util.Map;

public class BIPeriodCalTargetKey extends BIConfiguratedCalculatorTargetKey {

    /**
     *
     */
    private static final long serialVersionUID = -2798444466230489292L;

    public BIPeriodCalTargetKey(String targetName, String target_id,
                                Map targetMap, int start_group) {
        super(targetName, target_id, targetMap, start_group);
    }

    @Override
    public int getSummaryType() {
        return BIReportConstant.SUMMARY_TYPE.SUM;
    }


}