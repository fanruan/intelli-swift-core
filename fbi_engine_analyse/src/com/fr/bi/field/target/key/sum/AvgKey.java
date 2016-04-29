package com.fr.bi.field.target.key.sum;

import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.report.result.BITargetKey;
import com.fr.general.ComparatorUtils;

public class AvgKey extends SummaryKey {
    /**
     *
     */
    private static final long serialVersionUID = -3295444053220157499L;
    private BITargetKey sum;
    private BITargetKey count;

    public AvgKey(BIField ck, TargetFilter filter) {
        super(ck, filter);
        this.sum = new SumKey(ck, filter);
        this.count = new CountKey(ck, filter);
    }


    @Override
    public int getSummaryType() {
        return 0;
    }


    /**
     * hashֵ
     *
     * @return hashֵ
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((count == null) ? 0 : count.hashCode());
        result = prime * result + ((sum == null) ? 0 : sum.hashCode());
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
        AvgKey other = (AvgKey) obj;
        if (count == null) {
            if (other.count != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(count, other.count)) {
            return false;
        }
        if (sum == null) {
            if (other.sum != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(sum, other.sum)) {
            return false;
        }
        return true;
    }
}
