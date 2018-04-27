package com.fr.swift.query.group.by2;

import com.fr.swift.query.adapter.dimension.Cursor;
import com.fr.swift.query.group.by.GroupBy;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.function.Function;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/23.
 */
public class ItCreator implements IteratorCreator<GroupByEntry> {

    // 根据字典位置0的空值是否有对应的指标来决定空值是否要参与groupBy，这个判断在底层组件GroupBy里面做了，这里设置为0
    private static final int START_INDEX = 0;

    private boolean[] asc;
    private int[] cursor;
    private List<Column> dimensions;
    private RowTraversal filter;

    public ItCreator(GroupByInfo groupByInfo) {
        this.dimensions = groupByInfo.getDimensions();
        this.filter = groupByInfo.getDetailFilter().createFilterIndex();
        this.asc = getSorts(groupByInfo.getSorts(), dimensions.size());
        this.cursor = new int[dimensions.size()];
    }

    public ItCreator(GroupByInfo groupByInfo, Cursor cursor) {
        this(groupByInfo);
        this.cursor = cursor.createCursorIndex(IteratorUtils.iterator2List(new MapperIterator<Column, DictionaryEncodedColumn>(dimensions.iterator(), new Function<Column, DictionaryEncodedColumn>() {
            @Override
            public DictionaryEncodedColumn apply(Column p) {
                return p.getDictionaryEncodedColumn();
            }
        })));
    }

    private static boolean[] getSorts(List<Sort> sorts, int dimensionSize) {
        boolean[] asc = new boolean[dimensionSize];
        // 默认为字典的升序
        Arrays.fill(asc, true);
        for (int i = 0; i < sorts.size(); i++) {
            if (sorts.get(i).getSortType() == SortType.DESC) {
                asc[sorts.get(i).getTargetIndex()] = false;
            }
        }
        return asc;
    }

    private int getStartIndex(int dimensionIndex) {
        int index = cursor[dimensionIndex];
        if (index != START_INDEX) {
            // 分组定位的游标只能用一次
            cursor[dimensionIndex] = START_INDEX;
        }
        return index;
    }

    @Override
    public Iterator<GroupByEntry> createIterator(int stackSize, GroupByEntry groupByEntry) {
        if (groupByEntry == null && stackSize == 0) {
            return GroupBy.createGroupByResult(dimensions.get(0), filter, getStartIndex(0), asc[0]);
        }
        return GroupBy.createGroupByResult(dimensions.get(stackSize), groupByEntry.getTraversal(),
                getStartIndex(stackSize), asc[stackSize]);
    }
}
