package com.finebi.cube.conf.relation.path;

import com.finebi.cube.conf.table.BusinessTable;

/**
 * Created by Connery on 2016/1/14.
 */
class BIFatherTableContainer extends BIDirectlyRelativeTableContainer {
    public BIFatherTableContainer(BusinessTable currentTable) {
        super(currentTable);
    }
}