package com.fr.swift.segment;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.FasterAggregation;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.multi.ReadonlyMultiColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2019/7/11
 */
public class ReadonlyMultiSegment implements Segment {
    private List<Segment> segs;

    private int[] offsets;

    public ReadonlyMultiSegment(List<Segment> segs) {
        Assert.notEmpty(segs);
        Assert.isTrue(segs.size() > 1);
        this.segs = segs;
        init(segs);
    }

    private void init(List<Segment> segs) {
        offsets = new int[segs.size() + 1];
        int i = 0;
        for (; i < segs.size(); i++) {
            offsets[i + 1] = offsets[i] + segs.get(i).getRowCount();
        }
    }

    @Override
    public int getRowCount() {
        return offsets[offsets.length - 1];
    }

    @Override
    public <T> Column<T> getColumn(ColumnKey key) {
        List<Column<T>> columns = new ArrayList<Column<T>>();
        for (Segment seg : segs) {
            columns.add(seg.<T>getColumn(key));
        }
        return new ReadonlyMultiColumn<T>(columns, offsets);
    }

    @Override
    public ImmutableBitMap getAllShowIndex() {
        List<ImmutableBitMap> bitmaps = new ArrayList<ImmutableBitMap>();
        for (Segment seg : segs) {
            bitmaps.add(seg.getAllShowIndex());
        }
        return FasterAggregation.compose(bitmaps, offsets);
    }

    @Override
    public SwiftMetaData getMetaData() {
        return segs.get(0).getMetaData();
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public boolean isHistory() {
        // 主要是为了能调到释放
        return true;
    }

    @Override
    public void release() {
        SegmentUtil.releaseHisSeg(segs);
    }

    @Override
    public void putRowCount(int rowCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAllShowIndex(ImmutableBitMap bitMap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IResourceLocation getLocation() {
        throw new UnsupportedOperationException();
    }
}