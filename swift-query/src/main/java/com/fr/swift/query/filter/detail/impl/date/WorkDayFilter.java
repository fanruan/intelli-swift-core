package com.fr.swift.query.filter.detail.impl.date;

import com.fr.swift.query.filter.detail.impl.AbstractDetailFilter;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Calendar;

/**
 * @author yee
 * @date 2019-07-01
 */
public class WorkDayFilter extends AbstractDetailFilter<Long> {
    private Calendar calendar = Calendar.getInstance();

    public WorkDayFilter(Column<Long> column) {
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn<Long> dict) {
        IntList intList = IntListFactory.createIntList();
        for (int i = 1; i < dict.size(); i++) {
            Long timestamp = dict.getValue(i);
            if (matches(timestamp)) {
                intList.add(i);
            }
        }
        return new IntListRowTraversal(intList);
    }

    /**
     * TODO 2019/07/01 matches
     *
     * @param node
     * @param targetIndex
     * @param converter
     * @return
     */
    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        Object data = node.getData();
        return null != data && matches((Long) data);
    }

    private boolean matches(long timestamp) {
        calendar.setTimeInMillis(timestamp);
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        return i > Calendar.SUNDAY && i < Calendar.SATURDAY;
    }
}
