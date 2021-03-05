package com.fr.swift.cloud.segment.operator.collate.segment;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.bitmap.impl.FasterAggregation;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.segment.operator.collate.segment.bitmap.BitMapShifter;
import com.fr.swift.cloud.segment.operator.collate.segment.bitmap.BitMapShifterFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyon on 2019/2/21.
 */
public class SegmentBuilder implements Builder {

    private Segment segment;
    private List<String> fields;
    private List<Segment> subSegments;
    private List<ImmutableBitMap> allShows;
    private List<BitMapShifter> shifters;

    public SegmentBuilder(Segment segment, List<String> fields, List<Segment> subSegments, List<ImmutableBitMap> allShows) {
        this.segment = segment;
        this.fields = fields;
        this.subSegments = subSegments;
        this.allShows = allShows;
        init();
    }

    private void init() {
        shifters = new ArrayList<BitMapShifter>();
        int start = 0;
        for (int i = 0; i < subSegments.size(); i++) {
            shifters.add(BitMapShifterFactory.create(start, subSegments.get(i).getRowCount(), allShows.get(i)));
            start += allShows.get(i).getCardinality();
        }
    }

    @Override
    public void build() {
        int rowCount = getRowCount();
        segment.putRowCount(rowCount);
        segment.putAllShowIndex(getAllShow());
        for (String field : fields) {
            Column column = segment.getColumn(new ColumnKey(field));
            ColumnBuilder builder = new ColumnBuilder(rowCount, column, field, subSegments, allShows, shifters);
            builder.build();
        }
    }

    private ImmutableBitMap getAllShow() {
        List<ImmutableBitMap> bitMaps = new ArrayList<ImmutableBitMap>();
        for (int i = 0; i < allShows.size(); i++) {
            bitMaps.add(shifters.get(i).shift(allShows.get(i)));
        }
        return FasterAggregation.or(bitMaps);
    }

    private int getRowCount() {
        int count = 0;
        for (ImmutableBitMap bitMap : allShows) {
            count += bitMap.getCardinality();
        }
        return count;
    }
}
