package com.fr.swift.cloud.query.builder.feture;

import com.fr.swift.cloud.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.util.Strings;

/**
 * @author xiqiu
 * @date 2021/1/27
 * @description
 * @since swift-1.2.0
 */
public class MinFeature extends BaseFeature<Object> {


    public MinFeature(Segment segment, SwiftDetailFilterInfo detailFilterInfo) {
        super(segment, detailFilterInfo);
    }

    @Override
    public void doSetValue() {
        DictionaryEncodedColumn dictionaryEncodedColumn = segment.getColumn(detailFilterInfo.getColumnKey()).getDictionaryEncodedColumn();
        Object filterValue = detailFilterInfo.getFilterValue();
        this.t = (filterValue == null || Strings.isBlank(filterValue.toString())) ? 0 : dictionaryEncodedColumn.getValue(1);
    }
}
