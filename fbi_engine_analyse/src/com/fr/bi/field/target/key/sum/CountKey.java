package com.fr.bi.field.target.key.sum;


import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.general.ComparatorUtils;

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

    public CountKey(BIField ck, TargetFilter filter) {
        this(ck, filter, null);
    }
    public CountKey(BIField ck, TargetFilter filter, BIKey distinct_field) {
        super(ck, filter);
        this.distinct_field = distinct_field;
    }

    /**
     * hash值
     *
     * @return hash值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (distinct_field != null ? distinct_field.hashCode() : 0);
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        CountKey other = (CountKey) obj;
        return ComparatorUtils.equals(distinct_field, other.distinct_field);
    }

    @Override
    public int getSummaryType() {
        return BIReportConstant.SUMMARY_TYPE.COUNT;
    }


}