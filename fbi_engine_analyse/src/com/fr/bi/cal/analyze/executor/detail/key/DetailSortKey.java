package com.fr.bi.cal.analyze.executor.detail.key;

import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.ComparatorUtils;

import java.util.List;

/**
 * Created by 小灰灰 on 2015/10/13.
 */
public class DetailSortKey {
    private List<BIDetailTarget> sortList ;

    private GroupValueIndex gvi;

    private Table table;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetailSortKey)) {
            return false;
        }

        DetailSortKey that = (DetailSortKey) o;

        if (gvi != null ? !ComparatorUtils.equals(gvi, that.gvi) : that.gvi != null){
            return false;
        }
        if (sortList != null ? !ComparatorUtils.equals(sortList, that.sortList) : that.sortList != null) {
            return false;
        }
        if (table != null ? !ComparatorUtils.equals(table, that.table) : that.table != null) {
            return false;
        }

        return true;
    }

    /**
     * hash值
     * @return hash值
     */
    @Override
    public int hashCode() {
        int result = sortList != null ? sortList.hashCode() : 0;
        result = 31 * result + (gvi != null ? gvi.hashCode() : 0);
        result = 31 * result + (table != null ? table.hashCode() : 0);
        return result;
    }

    public DetailSortKey(GroupValueIndex gvi, Table table, List<BIDetailTarget> sortList) {
        this.gvi = gvi;
        this.table = table;
        this.sortList = sortList;
    }

}