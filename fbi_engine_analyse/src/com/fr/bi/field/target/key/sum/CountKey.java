package com.fr.bi.field.target.key.sum;


import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.stable.constant.BIReportConstant;

/**
 * 计算个数
 *
 * @author Daniel
 */
public class CountKey extends SummaryKey {

	/* (non-Javadoc)
     * @see java.lang.Object#hashCode()
	 */

    /**
     *
     */
    private static final long serialVersionUID = 3447669947358786022L;
    private BIKey distinct_field;

    public CountKey(BusinessField ck, TargetFilter filter) {
        this(ck, filter, null);
    }

    public CountKey(BusinessField ck, TargetFilter filter, BIKey distinct_field) {
        super(ck, filter);
        this.distinct_field = distinct_field;
    }



    @Override
    public int getSummaryType() {
        return BIReportConstant.SUMMARY_TYPE.COUNT;
    }


}