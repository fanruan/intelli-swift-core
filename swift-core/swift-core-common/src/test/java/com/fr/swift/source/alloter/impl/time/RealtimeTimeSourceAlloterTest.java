package com.fr.swift.source.alloter.impl.time;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.cube.io.Types;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;
import com.fr.swift.source.alloter.impl.hash.function.HashFunction;
import com.fr.swift.source.alloter.impl.hash.function.HashType;
import com.fr.swift.source.alloter.impl.time.function.TimePartitionsType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Calendar;

/**
 * @author Marvin
 * @date 7/22/2019
 * @description
 * @since swift 1.1
 */
public class RealtimeTimeSourceAlloterTest extends HistoryTimeSourceAlloterTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    public class QuarterBPartitionsFunction implements HashFunction {
        @JsonProperty("partitionstype")
        TimePartitionsType partitionsType;

        private QuarterBPartitionsFunction() {
        }

        public QuarterBPartitionsFunction(TimePartitionsType type) {
            this.partitionsType = type;
        }

        @Override
        public int indexOf(Object key) {
            long time = (key == null) ? 0 : Long.valueOf(String.valueOf(key)).longValue();
            if (time <= 0 || time > System.currentTimeMillis()) {
                time = 0;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            int index = 0;
            switch (partitionsType) {
                case YEAR:
                    index = calendar.get(Calendar.YEAR);
                    break;
                case QUARTER:
                    index = calendar.get(Calendar.MONTH) / 3;
                    break;
                case MONTH:
                    index = calendar.get(Calendar.MONTH);
                    break;

            }
            return index;
        }

        @Override
        public HashType getType() {
            return HashType.TIME;
        }
    }

    @Test
    public void testAllot() {
        RealtimeTimeSourceAlloter alloter1 = new RealtimeTimeSourceAlloter(new SourceKey("rty"), new TimeAllotRule());
        RealtimeTimeSourceAlloter alloter2 = new RealtimeTimeSourceAlloter(new SourceKey("rtq"), new TimeAllotRule(0, TimePartitionsType.QUARTER));
        RealtimeTimeSourceAlloter alloter3 = new RealtimeTimeSourceAlloter(new SourceKey("rtm"), new TimeAllotRule(0, TimePartitionsType.MONTH));

        HashFunction QuarterBPartitionsFunction = new QuarterBPartitionsFunction(TimePartitionsType.QUARTER);
        RealtimeTimeSourceAlloter alloter4 = new RealtimeTimeSourceAlloter(new SourceKey("rtqb"), new TimeAllotRule(0, QuarterBPartitionsFunction));

        TimeRowInfo timeRowInfoY1 = new TimeRowInfo(new ListBasedRow(0));
        TimeRowInfo timeRowInfoY2 = new TimeRowInfo(new ListBasedRow(1262275200000L));
        TimeRowInfo timeRowInfoY3 = new TimeRowInfo(new ListBasedRow(1433088000000L));
        TimeRowInfo timeRowInfoY4 = new TimeRowInfo(new ListBasedRow(5514304000000L));

        TimeRowInfo timeRowInfoQ1 = new TimeRowInfo(new ListBasedRow(1389283200000L));
        TimeRowInfo timeRowInfoQ2 = new TimeRowInfo(new ListBasedRow(1270224000000L));
        TimeRowInfo timeRowInfoQ3 = new TimeRowInfo(new ListBasedRow(1279296000000L));
        TimeRowInfo timeRowInfoQ4 = new TimeRowInfo(new ListBasedRow(1289059200000L));

        TimeRowInfo timeRowInfoM1 = new TimeRowInfo(new ListBasedRow(1389283200000L));
        TimeRowInfo timeRowInfoM2 = new TimeRowInfo(new ListBasedRow(1359907200000L));
        TimeRowInfo timeRowInfoM3 = new TimeRowInfo(new ListBasedRow(1362067200000L));
        TimeRowInfo timeRowInfoM4 = new TimeRowInfo(new ListBasedRow(1365091200000L));
        TimeRowInfo timeRowInfoM5 = new TimeRowInfo(new ListBasedRow(1368892800000L));
        TimeRowInfo timeRowInfoM6 = new TimeRowInfo(new ListBasedRow(1370188800000L));
        TimeRowInfo timeRowInfoM7 = new TimeRowInfo(new ListBasedRow(1373558400000L));
        TimeRowInfo timeRowInfoM8 = new TimeRowInfo(new ListBasedRow(1376064000000L));
        TimeRowInfo timeRowInfoM9 = new TimeRowInfo(new ListBasedRow(1380038400000L));
        TimeRowInfo timeRowInfoM10 = new TimeRowInfo(new ListBasedRow(1380816000000L));
        TimeRowInfo timeRowInfoM11 = new TimeRowInfo(new ListBasedRow(1384272000000L));
        TimeRowInfo timeRowInfoM12 = new TimeRowInfo(new ListBasedRow(1387728000000L));

        Assert.assertEquals(newSegInfo(0), alloter1.allot(timeRowInfoY1));
        Assert.assertEquals(newSegInfo(1), alloter1.allot(timeRowInfoY2));
        Assert.assertEquals(newSegInfo(2), alloter1.allot(timeRowInfoY3));
        Assert.assertEquals(newSegInfo(0), alloter1.allot(timeRowInfoY4));

        Assert.assertEquals(newSegInfo(3), alloter2.allot(timeRowInfoQ1));
        Assert.assertEquals(newSegInfo(4), alloter2.allot(timeRowInfoQ2));
        Assert.assertEquals(newSegInfo(5), alloter2.allot(timeRowInfoQ3));
        Assert.assertEquals(newSegInfo(6), alloter2.allot(timeRowInfoQ4));

        Assert.assertEquals(newSegInfo(7), alloter3.allot(timeRowInfoM1));
        Assert.assertEquals(newSegInfo(8), alloter3.allot(timeRowInfoM2));
        Assert.assertEquals(newSegInfo(9), alloter3.allot(timeRowInfoM3));
        Assert.assertEquals(newSegInfo(10), alloter3.allot(timeRowInfoM4));
        Assert.assertEquals(newSegInfo(11), alloter3.allot(timeRowInfoM5));
        Assert.assertEquals(newSegInfo(12), alloter3.allot(timeRowInfoM6));
        Assert.assertEquals(newSegInfo(13), alloter3.allot(timeRowInfoM7));
        Assert.assertEquals(newSegInfo(14), alloter3.allot(timeRowInfoM8));
        Assert.assertEquals(newSegInfo(15), alloter3.allot(timeRowInfoM9));
        Assert.assertEquals(newSegInfo(16), alloter3.allot(timeRowInfoM10));
        Assert.assertEquals(newSegInfo(17), alloter3.allot(timeRowInfoM11));
        Assert.assertEquals(newSegInfo(18), alloter3.allot(timeRowInfoM12));

        Assert.assertEquals(newSegInfo(19), alloter4.allot(timeRowInfoQ1));
        Assert.assertEquals(newSegInfo(20), alloter4.allot(timeRowInfoQ2));
        Assert.assertEquals(newSegInfo(21), alloter4.allot(timeRowInfoQ3));
        Assert.assertEquals(newSegInfo(22), alloter4.allot(timeRowInfoQ4));

        Mockito.verify(bucketService, Mockito.times(23)).saveElement(Mockito.any(SwiftSegmentBucketElement.class));

    }

    protected SegmentInfo newSegInfo(int order) {
        return new SwiftSegmentInfo(order, Types.StoreType.FINE_IO);
    }
}