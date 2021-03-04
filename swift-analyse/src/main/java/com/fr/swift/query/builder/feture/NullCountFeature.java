package com.fr.swift.query.builder.feture;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;

/**
 * @author xiqiu
 * @date 2021/1/27
 * @description
 * @since swift-1.2.0
 */
public class NullCountFeature extends BaseFeature<Integer> {

    public NullCountFeature(Segment segment, SwiftDetailFilterInfo detailFilterInfo) {
        super(segment, detailFilterInfo);
    }

    @Override
    public void doSetValue() {
        Column column = segment.getColumn(detailFilterInfo.getColumnKey());
        ImmutableBitMap nullIndex = column.getBitmapIndex().getNullIndex();
        this.t = nullIndex.getCardinality();
    }
}
