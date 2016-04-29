package com.fr.bi.conf.report.key;

import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.general.ComparatorUtils;

import java.util.List;

public class NumberSummaryFilterKey {

	/* (non-Javadoc)
     * @see java.lang.Object#hashCode()
	 */

    TargetFilter filter;
    private String columnName;
    private List<BITableSourceRelation> relation;

    public NumberSummaryFilterKey(String colName, List<BITableSourceRelation> relation, TargetFilter filter) {
        this.columnName = colName;
        this.relation = relation;
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
        int result = 1;
        result = prime * result + (columnName == null ? 0 : columnName.hashCode());
        result = prime * result + (filter == null ? 0 : filter.hashCode());
        result = prime * result + relation.hashCode();
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
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NumberSummaryFilterKey other = (NumberSummaryFilterKey) obj;
        if (!ComparatorUtils.equals(columnName, other.columnName)) {
            return false;
        }
        if (!ComparatorUtils.equals(relation, other.relation)) {
            return false;
        }
        if (filter == null) {
            if (other.filter != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(filter, other.filter)) {
            return false;
        }
        return true;
    }
}
