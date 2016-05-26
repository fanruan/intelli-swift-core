package com.fr.bi.field.target.key.sum;

import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.report.result.BITargetKey;
import com.fr.general.ComparatorUtils;

public abstract class SummaryKey extends BIField implements BITargetKey {

    /**
     *
     */
    private static final long serialVersionUID = 4488941101288139865L;
    private TargetFilter filter;

    protected SummaryKey(BusinessField ck, TargetFilter filter) {
        super(ck.getFieldID().getIdentity(), ck.getFieldName());
        this.filter = filter;
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
        result = prime * result + (int) (getSummaryType() ^ (getSummaryType() >>> 32));
        result = prime * result + (filter == null ? 0 : filter.hashCode());
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
        SummaryKey other = (SummaryKey) obj;
        if (filter == null) {
            if (other.filter != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(filter, other.filter)) {
            return false;
        }
        if (getSummaryType() != other.getSummaryType()) {
            return false;
        }
        return true;
    }

}
