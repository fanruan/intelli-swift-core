package com.fr.bi.field.target.key.cal.configuration.summary;


import com.fr.bi.stable.constant.BIReportConstant;

import java.util.Map;

public class BICountOfAllKey extends BISummaryOfAllCalTargetKey {

    /**
     *
     */
    private static final long serialVersionUID = -2455481615321220936L;

    public BICountOfAllKey(String targetName, String cal_target_name,
                           Map targetMap, int start_group) {
        super(targetName, cal_target_name, targetMap, start_group);
    }

    @Override
    public int getSummaryType() {
        return BIReportConstant.SUMMARY_TYPE.COUNT;
    }


}