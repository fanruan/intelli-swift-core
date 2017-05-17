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

public class BICubeDoubeRandomFieldSource extends BIRandomFieldSource {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(BICubeDoubeRandomFieldSource.class);
    protected DoubleFieldValueGenerator doubleFieldValueGenerator;

    public BICubeDoubeRandomFieldSource(CubeTableSource tableBelongTo, String fieldName, int classType, int fieldSize, int groupSize, int rowSize) {
        super(tableBelongTo, fieldName, classType, fieldSize,groupSize,rowSize);

        doubleFieldValueGenerator = new DoubleFieldValueGenerator(groupSize, rowSize);
    }

    @Override
    public Object getValue() {
        return doubleFieldValueGenerator.getValue();
    }
}
