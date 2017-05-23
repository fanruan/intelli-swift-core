package com.finebi.table.gen;
/**
 * This class created on 2017/5/17.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.data.source.CubeTableSource;

public class BICubeLongRandomFieldSource extends BIRandomFieldSource {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(BICubeLongRandomFieldSource.class);
    protected LongFieldValueGenerator longFieldValueGenerator;

    public BICubeLongRandomFieldSource(CubeTableSource tableBelongTo, String fieldName, int classType, int fieldSize, int groupSize, int rowSize) {
        super(tableBelongTo, fieldName, classType, fieldSize, groupSize, rowSize);

        longFieldValueGenerator = new LongFieldValueGenerator(groupSize, rowSize);
    }

    @Override
    public Object getValue() {
        return longFieldValueGenerator.getValue();
    }
}
