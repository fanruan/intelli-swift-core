package com.fr.bi.field.target.key.sum;

import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BIField;

/**
 * Created by 小灰灰 on 2015/7/3.
 */
public class StringAppendKey extends SummaryKey {
    private static final long serialVersionUID = -4908142174648729359L;

    public StringAppendKey(BIField ck, TargetFilter filter) {
        super(ck, filter);
    }

    @Override
    public int getSummaryType() {
        return BIReportConstant.SUMMARY_TYPE.SUM;
    }
}