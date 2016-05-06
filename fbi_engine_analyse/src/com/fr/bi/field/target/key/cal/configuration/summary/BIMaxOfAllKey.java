package com.fr.bi.field.target.key.cal.configuration.summary;


import com.fr.bi.stable.constant.BIReportConstant;

import java.util.Map;

public class BIMaxOfAllKey extends BISummaryOfAllCalTargetKey {

    /**
     *
     */
    private static final long serialVersionUID = 5879476725200977152L;

    public BIMaxOfAllKey(String targetName, String target_id,
                         Map targetMap, int start_group) {
        super(targetName, target_id, targetMap, start_group);
    }

    @Override
    public int getSummaryType() {
        return BIReportConstant.SUMMARY_TYPE.MAX;
    }

}