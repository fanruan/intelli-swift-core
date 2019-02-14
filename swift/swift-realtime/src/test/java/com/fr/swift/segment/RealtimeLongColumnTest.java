package com.fr.swift.segment;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.RealtimeLongColumn;
import org.junit.Before;

public class RealtimeLongColumnTest extends BaseRealtimeColumnTest<Long> {

    @Before
    public void setUp() throws Exception {
        super.setUp();

        data1 = new Long[BOUND];
        for (int i = 0; i < data1.length; i++) {
            long l = r.nextInt(BOUND);
            data1[i] = l < BOUND / 10 ? null : l;
        }

        data2 = new Long[BOUND];
        for (int i = 0; i < data2.length; i++) {
            long l = r.nextInt(BOUND);
            data2[i] = l < BOUND / 10 ? null : l;
        }
    }

    @Override
    Column<Long> getColumn() {
        return new RealtimeLongColumn(new ResourceLocation("cubes/seg0/column0", StoreType.MEMORY));
    }
}