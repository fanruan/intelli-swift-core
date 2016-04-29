package com.fr.bi.field.target.key.cal.configuration;

import com.fr.bi.stable.constant.BIReportConstant;

import java.util.Map;

public class BIRankCalTargetKey extends BIConfiguratedCalculatorTargetKey {
    /**
     *
     */
    private static final long serialVersionUID = 1025031441134463188L;

    /**
     * 默认为升序排名
     */
    private int type = BIReportConstant.TARGET_TYPE.CAL_VALUE.RANK_TPYE.ASC;

    public BIRankCalTargetKey(String targetName, String cal_target_name, Map targetMap, int start_group, int type) {
        super(targetName, cal_target_name, targetMap, start_group);
        this.type = type;
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
        result = prime * result + type;
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
        BIRankCalTargetKey other = (BIRankCalTargetKey) obj;
        if (type != other.type) {
            return false;
        }
        return true;
    }
}