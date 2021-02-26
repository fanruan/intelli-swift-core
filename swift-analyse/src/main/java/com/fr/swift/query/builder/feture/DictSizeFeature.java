package com.fr.swift.query.builder.feture;

import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

/**
 * @author xiqiu
 * @date 2021/1/27
 * @description
 * @since swift-1.2.0
 */
public class DictSizeFeature extends BaseFeature<Integer> {

    public DictSizeFeature(Segment segment, SwiftDetailFilterInfo detailFilterInfo) {
        super(segment, detailFilterInfo);
    }

    @Override
    public void doSetValue() {
        DictionaryEncodedColumn dictionaryEncodedColumn = segment.getColumn(detailFilterInfo.getColumnKey()).getDictionaryEncodedColumn();
        this.t = dictionaryEncodedColumn.size();
    }
}
