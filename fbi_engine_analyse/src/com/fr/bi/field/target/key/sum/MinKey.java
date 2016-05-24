package com.fr.bi.field.target.key.sum;


import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.stable.constant.BIReportConstant;

public class MinKey extends SummaryKey {

    /**
     *
     */
    private static final long serialVersionUID = -713594350816529734L;

    public MinKey(BusinessField ck, TargetFilter filter) {
        super(ck, filter);
    }

    @Override
    public int getSummaryType() {
        return BIReportConstant.SUMMARY_TYPE.MIN;
    }

}