package com.fr.swift.cloud.analyse;

import com.fr.swift.cloud.query.filter.info.FilterInfo;
import com.fr.swift.cloud.query.info.element.dimension.Dimension;
import com.fr.swift.cloud.query.limit.Limit;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.source.ListBasedRow;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.SwiftMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/15
 */
public class DetailSegment extends AbstractDetailSegment {

    public DetailSegment(int fetchSize, List<Segment> totalSegments, List<Dimension> dimensions, List<FilterInfo> filters, Limit limit, SwiftMetaData metaData, SwiftMetaData queriedMetadata) {
        super(fetchSize, new DetailQuerySegmentComponent(totalSegments), dimensions, filters, limit, metaData, queriedMetadata);
    }

    /**
     * 这里只负责拿数据，需要配合hasNext 进行使用。只使用这个函数的话最多拿一个批次的数据
     * 如果在这里进行更新的话极端情况下比较难处理
     * 保证一般流程（先判断有没有，再去获取）的正常运行。
     *
     * @return 抽取的数据
     */
    @Override
    public Row getNextRow() {
        List<Object> values = new ArrayList<>();
        Integer next = currentRowItr.next();
        for (Column<?> column : currentColumns) {
            DetailColumn<?> detailColumn = column.getDetailColumn();
            Object val = detailColumn.get(next);
            values.add(val);
        }
        return new ListBasedRow(values);
    }


    @Override
    public List<Row> getPage() {
        if (!hasNextPage()) {
            return new ArrayList<Row>(0);
        }
        int count = fetchSize;
        List<Row> result = new ArrayList<>();
        while (hasNext() && count-- > 0) {
            result.add(getNextRow());
        }
        return result;
    }
}
