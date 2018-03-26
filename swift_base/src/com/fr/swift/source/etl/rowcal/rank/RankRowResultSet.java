package com.fr.swift.source.etl.rowcal.rank;

import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.compare.Comparators;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.utils.ETLConstant;
import com.fr.swift.source.etl.utils.FinalInt;
import com.fr.swift.source.etl.utils.GroupValueIterator;
import com.fr.swift.source.etl.utils.SwiftValuesAndGVI;
import com.fr.swift.structure.iterator.RowTraversal;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by Handsome on 2018/2/28 0028 11:06
 */
public class RankRowResultSet implements SwiftResultSet {

    private int type;
    private ColumnKey columnKey;
    private Segment[] segments;
    private ColumnKey[] dimension;
    private int segCursor, rowCursor, rowCount;
    private TreeMap<Number, FinalInt> tree;
    private HashMap<Number, Long> map;
    private TempValue tempValue;
    private SwiftMetaData metaData;
    private GroupValueIterator iterator;
    private boolean needNext;
    private SwiftValuesAndGVI valuesAndGVI;
    private Segment[] tempSegment;
    private RowTraversal[] traversal;

    public RankRowResultSet(int type, ColumnKey columnKey, Segment[] segments, SwiftMetaData metaData, ColumnKey[] dimension) {
        this.type = type;
        this.columnKey = columnKey;
        this.segments = segments;
        this.metaData = metaData;
        this.dimension = dimension;
        init();
    }

    private void init() {
        segCursor = 0;
        rowCursor = 0;
        rowCount = segments[0].getRowCount();
        needNext = true;
        tempValue = new TempValue();
        //TODO group type
        if (dimension != null) {
            iterator = new GroupValueIterator(segments, dimension, null);
            tempSegment = new Segment[0];
            traversal = new RowTraversal[0];
        } else {
            RowTraversal[] traversals = new RowTraversal[segments.length];
            for (int i = 0; i < segments.length; i++) {
                traversals[i] = segments[i].getAllShowIndex();
            }
            tree = createSortedTree(segments, traversals);
            map = buildRankMap(tree);
        }
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean next() throws SQLException {
        if (null != dimension) {
            if (iterator.hasNext() || (segCursor < tempSegment.length && rowCursor < rowCount)) {
                if (needNext) {
                    valuesAndGVI = iterator.next();
                    traversal = new RowTraversal[valuesAndGVI.getAggreator().size()];
                    tempSegment = new Segment[valuesAndGVI.getAggreator().size()];
                    for (int i = 0; i < valuesAndGVI.getAggreator().size(); i++) {
                        traversal[i] = valuesAndGVI.getAggreator().get(i).getBitMap();
                        tempSegment[i] = valuesAndGVI.getAggreator().get(i).getSegment();
                    }
                    tree = createSortedTree(tempSegment, traversal);
                    map = buildRankMap(tree);
                    rowCount = traversal[0].getCardinality();
                    segCursor = 0;
                    rowCursor = 0;
                    needNext = false;
                }
                if (segCursor == tempSegment.length - 1 && rowCursor >= rowCount - 1) {
                    needNext = true;
                }
                nextValueForDimension();
                return true;
            }
            return false;
        } else {
            return nextValue();
        }
    }

    private boolean nextValueForDimension() throws SQLException {
        if (segCursor < tempSegment.length && rowCursor < rowCount) {
            rowCount = traversal[segCursor].getCardinality();
            final Index index = new Index();
            traversal[segCursor].breakableTraversal(new BreakTraversalAction() {
                int cursor = 0;

                @Override
                public boolean actionPerformed(int row) {
                    if (cursor == rowCursor) {
                        index.setIndex(row);
                        return true;
                    }
                    cursor++;
                    return false;
                }
            });
            DictionaryEncodedColumn getter = tempSegment[segCursor].getColumn(columnKey).getDictionaryEncodedColumn();
            Number v = (Number) getter.getValue(getter.getIndexByRow(index.getIndex()));
            Double rank = Double.parseDouble(map.get(v) + "");
            List dataList = new ArrayList();
            dataList.add(rank);
            tempValue.setRow(new ListBasedRow(dataList));
            if (rowCursor < rowCount - 1) {
                rowCursor++;
            } else {
                if (segCursor < segments.length) {
                    rowCursor = 0;
                    segCursor++;
                } else {
                    return false;
                }
            }
            return true;
        } else {
            List list = new ArrayList();
            list.add(null);
            tempValue.setRow(new ListBasedRow(list));
        }
        return false;
    }

    private boolean nextValue() throws SQLException {
        if (segCursor < segments.length && rowCursor < rowCount) {
            rowCount = segments[segCursor].getRowCount();
            DictionaryEncodedColumn getter = segments[segCursor].getColumn(columnKey).getDictionaryEncodedColumn();
            Number v = (Number) getter.getValue(getter.getIndexByRow(rowCursor));
            long rank = map.get(v);
            List dataList = new ArrayList();
            dataList.add(rank);
            tempValue.setRow(new ListBasedRow(dataList));
            if (rowCursor < rowCount - 1) {
                rowCursor++;
            } else {
                if (segCursor < segments.length) {
                    rowCursor = 0;
                    segCursor++;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getRowData() throws SQLException {
        return tempValue.getRow();
    }

    private HashMap<Number, Long> buildRankMap(TreeMap<Number, FinalInt> tree) {
        long rank = 1L;
        HashMap<Number, Long> rankMap = new HashMap<Number, Long>();
        Iterator<Map.Entry<Number, FinalInt>> iter = tree.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Number, FinalInt> entry = iter.next();
            rankMap.put(entry.getKey(), rank);
            rank += entry.getValue().value;
        }
        return rankMap;
    }

    private TreeMap<Number, FinalInt> createSortedTree(Segment[] segments, RowTraversal[] traversals) {
        Comparator comparator = null;
        if (type == ETLConstant.CONF.ADD_COLUMN.RANKING.ASC) {
            comparator = Comparators.<Double>asc();
        } else {
            comparator = Comparators.<Double>desc();
        }
        final TreeMap<Number, FinalInt> tree = new TreeMap<Number, FinalInt>(comparator);
        for (int i = 0; i < segments.length; i++) {
            final DictionaryEncodedColumn getter = segments[i].getColumn(columnKey).getDictionaryEncodedColumn();
            traversals[i].traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    Number v = (Number) getter.getValue(getter.getIndexByRow(row));
                    FinalInt count = tree.get(v);
                    if (null == count) {
                        count = new FinalInt();
                        tree.put(v, count);
                    }
                    count.value++;
                }
            });
        }
        return tree;
    }

    private class TempValue {

        public ListBasedRow getRow() {
            return row;
        }

        public void setRow(ListBasedRow row) {
            this.row = row;
        }

        private ListBasedRow row;

    }

    private class Index {
        private int index = 0;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

}
