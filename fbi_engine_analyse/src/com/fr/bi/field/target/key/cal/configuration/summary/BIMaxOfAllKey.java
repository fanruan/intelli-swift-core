package com.fr.bi.field.target.key.cal.configuration.summary;


import com.fr.bi.stable.constant.BIReportConstant;

import java.util.Map;

public class BIMaxOfAllKey extends BISummaryOfAllCalTargetKey {

    /**
     *
     */
    private static final long serialVersionUID = 5879476725200977152L;

    public BIMaxOfAllKey(String targetName, String cal_target_name,
                         Map targetMap, int start_group) {
        super(targetName, cal_target_name, targetMap, start_group);
    }

    @Override
    public int getSummaryType() {
        return BIReportConstant.SUMMARY_TYPE.MAX;
    }

}