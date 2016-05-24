package com.fr.bi.field.target.target;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITable;

/**
 * Created by 小灰灰 on 2015/7/14.
 */
public class NoneTargetCountTarget extends BICounterTarget {
    @Override
    public BusinessTable createTableKey() {
        return BITable.BI_EMPTY_TABLE();
    }

    @Override
    public BusinessField createColumnKey() {
        return new BIField(BITable.BI_EMPTY_TABLE(), "pony");
    }
}