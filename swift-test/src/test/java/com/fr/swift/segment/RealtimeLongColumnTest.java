package com.fr.swift.segment;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.RealtimeLongColumn;
import com.fr.swift.test.TestResource;

import java.util.stream.Stream;

public class RealtimeLongColumnTest extends BaseRealtimeColumnTest<Long> {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        data1 = Stream.generate(() -> {
            long i = r.nextInt(BOUND);
            return i < BOUND / 10 ? null : i;
        }).limit(BOUND).toArray(Long[]::new);

        data2 = Stream.generate(() -> {
            long i = r.nextInt(BOUND);
            return i < BOUND / 10 ? null : i;
        }).limit(BOUND).toArray(Long[]::new);
    }

    @Override
    Column<Long> getColumn() {
        return new RealtimeLongColumn(new ResourceLocation(TestResource.getRunPath(getClass()) + "/cubes/seg0/column0", StoreType.MEMORY));
    }
}