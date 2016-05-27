package com.fr.bi.field.target.target;

import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;

/**
 * Created by 小灰灰 on 2015/7/14.
 */
public class NoneTargetCountTarget extends BICounterTarget {
    @Override
    public BusinessTable createTableKey() {
        return new BIBusinessTable(null);
    }

    @Override
    public BusinessField createColumnKey() {
        return new BIBusinessField(createTableKey(), "BI_EMPTY");
    }
}