package com.fr.swift.cloud.segment.operator.collate.segment;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.bitmap.traversal.TraversalAction;
import com.fr.swift.cloud.segment.column.DetailColumn;

import java.util.List;

/**
 * Created by lyon on 2019/2/21.
 */
class DetailBuilder implements Builder {

    private DetailColumn column;
    private List<DetailColumn> subColumns;
    private List<ImmutableBitMap> allShows;

    public DetailBuilder(DetailColumn column, List<DetailColumn> subColumns, List<ImmutableBitMap> allShows) {
        this.column = column;
        this.subColumns = subColumns;
        this.allShows = allShows;
    }

    @Override
    public void build() {
        final int[] pos = new int[1];
        for (int i = 0; i < subColumns.size(); i++) {
            final DetailColumn detail = subColumns.get(i);
            allShows.get(i).traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    column.put(pos[0]++, detail.get(row));
                }
            });
        }
    }
}
