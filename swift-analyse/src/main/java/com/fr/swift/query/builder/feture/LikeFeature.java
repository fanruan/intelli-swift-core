package com.fr.swift.query.builder.feture;

import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiqiu
 * @date 2021/1/27
 * @description
 * @since swift-1.2.0
 */
public class LikeFeature extends BaseFeature<List<String>> {

    public LikeFeature(Segment segment, SwiftDetailFilterInfo detailFilterInfo) {
        super(segment, detailFilterInfo);
    }

    @Override
    public void doSetValue() {
        t = new ArrayList<>();
        DictionaryEncodedColumn dictionaryEncodedColumn = segment.getColumn(detailFilterInfo.getColumnKey()).getDictionaryEncodedColumn();
        for (int i = 1; i < dictionaryEncodedColumn.size(); i++) {
            t.add((String) dictionaryEncodedColumn.getValue(i));
        }
    }
}
