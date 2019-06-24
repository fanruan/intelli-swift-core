package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.aggregator.AggregatorValueRow;
import com.fr.swift.query.aggregator.AggregatorValueSet;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by Lyon on 2017/12/5.
 */
public class NullFilter extends AbstractDetailFilter {

    public NullFilter(Column column) {
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn dict) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
        return column.getBitmapIndex().getNullIndex();
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        if (targetIndex == -1) {
            return node.getData() == null;
        }
        boolean matches = false;
        AggregatorValueSet set = node.getAggregatorValue();
        while (set.hasNext()) {
            AggregatorValueRow next = set.next();
            boolean match = next.getValue(targetIndex).calculateValue() == null;
            matches |= match;
            next.setValid(match);
        }
        set.reset();
        return matches;
    }
}
