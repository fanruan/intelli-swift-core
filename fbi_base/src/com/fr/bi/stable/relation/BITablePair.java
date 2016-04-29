package com.fr.bi.stable.relation;

import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

/**
 * Created by Connery on 2016/1/13.
 */
public class BITablePair {
    private Table from;
    private Table to;

    public BITablePair(Table from, Table to) {
        BINonValueUtils.checkNull(from, to);
        this.from = from;
        this.to = to;
    }

    public Table getFrom() {
        return from;
    }

    public Table getTo() {
        return to;
    }

    public BITablePair generateReversePair() {
        return new BITablePair(to, from);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BITablePair)) {
            return false;
        }
        BITablePair that = (BITablePair) o;
        if (from != null ? !ComparatorUtils.equals(from, that.from) : that.from != null) {
            return false;
        }
        return !(to != null ? !ComparatorUtils.equals(to, that.to) : that.to != null);
    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BITablePair{");
        sb.append("first=").append(from);
        sb.append(", second=").append(to);
        sb.append('}');
        return sb.toString();
    }
}