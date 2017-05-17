package com.finebi.table.gen;
/**
 * This class created on 2017/5/17.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

public abstract class BIRandomFieldSource extends BICubeFieldSource {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(BIRandomFieldSource.class);

    public BIRandomFieldSource(CubeTableSource tableBelongTo, String fieldName, int classType, int fieldSize, int groupSize, int rowSize) {
        super(tableBelongTo, fieldName, classType, fieldSize);
    }

    public abstract Object getValue();
}
