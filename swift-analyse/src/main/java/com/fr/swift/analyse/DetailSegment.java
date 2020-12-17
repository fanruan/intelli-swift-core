package com.fr.swift.analyse;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.limit.Limit;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/15
 */
public class DetailSegment extends AbstractDetailSegment {

    public DetailSegment(List<Segment> totalSegments, List<Dimension> dimensions, List<FilterInfo> filters, Limit limit, SwiftMetaData metaData, SwiftMetaData queriedMetadata) {
        super(totalSegments, dimensions, filters, limit, metaData, queriedMetadata);
    }

    @Override
    public Row getRow() {
        try {
            List<Object> values = new ArrayList<>();
            Integer next = currentRowItr.next();
            Segment curSeg = currentSeg.getKey();
            for (ColumnKey columnKey : columnKeys) {
                Column<Object> column = curSeg.getColumn(columnKey);
                values.add(column.getDetailColumn().get(next));
            }
            return new ListBasedRow(values);
        } catch (Exception warn) {
            currentSeg = filteredList.get(++segIndex);
            currentRowItr = currentSeg.getValue().intIterator();
            return getRow();
        }
    }

    @Override
    public Row getRow(int curs) {
        // TODO: 2020/12/16
        return null;
    }
}
