package com.finebi.table.gen;
/**
 * This class created on 2017/5/17.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;

public class DoubleFieldValueGenerator extends RandomFieldValueGenerator<Double> {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(DoubleFieldValueGenerator.class);

    public DoubleFieldValueGenerator(int groupSize, int rowSize) {
        super(groupSize, rowSize);
    }

    @Override
    protected void generateGroup() {
        for (int i = 0; i < groupSize; i++) {
            groupValue.add(random.getRandomDouble());
        }
    }
}
