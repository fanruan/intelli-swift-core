package com.fr.swift.source.etl.join;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.utils.GVIAndSegment;
import com.fr.swift.source.etl.utils.SwiftValueIterator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Handsome on 2018/1/16 0016 14:23
 */
public class JoinOperatorResultSet implements SwiftResultSet {
    private ColumnKey[] lKey;
    private ColumnKey[] rKey;
    private List<JoinColumn> columns = new ArrayList<JoinColumn>();
    private boolean nullContinue;
    private boolean writeLeft;
    private SwiftMetaData metaData;
    private Segment[] lSegments;
    private Segment[] rSegments;
    final ListRow listRow = new ListRow();
    private SwiftValueIterator lValueIterator;
    private SwiftValueIterator rValueIterator;
    private Comparator[] comparators;
    private GVIAndSegment lValuesAndGVI;
    private GVIAndSegment rValuesAndGVI;
    private boolean isLeftNeedNext = true;
    private boolean isRightNeedNext = true;
    private boolean isLeftMatched = false;
    private boolean isRightMatched = false;
    private boolean isLeftEnd = false;
    private boolean isFirstTraverse = true;
    private boolean[] isMatched;
    private boolean isFirstInit = true;
    private int rightCursor;
    private int arrayIndex = -1;


    public JoinOperatorResultSet(List<JoinColumn> columns, ColumnKey[] lKey, SwiftMetaData metaData,
                                 ColumnKey[] rKey, Segment[] lSegments, Segment[] rSegments,
                                 boolean nullContinue, boolean writeLeft) {
        this.columns = columns;
        this.lKey = lKey;
        this.rKey = rKey;
        this.lSegments = lSegments;
        this.rSegments = rSegments;
        this.metaData = metaData;
        this.nullContinue = nullContinue;
        this.writeLeft = writeLeft;
        init();
    }

    private void init() {
        this.lValueIterator = new SwiftValueIterator(lSegments, lKey);
        this.rValueIterator = new SwiftValueIterator(rSegments, rKey);
        comparators = new Comparator[lKey.length];
        for (int i = 0; i < comparators.length; i++) {
            comparators[i] = lSegments[0].getColumn(lKey[i]).getDictionaryEncodedColumn().getComparator();
        }
    }

    @Override
    public void close() {

    }

    private boolean leftJoin() {
        if (!lValueIterator.hasNext() && !rValueIterator.hasNext()) {
            return false;
        }
        if (lValueIterator.hasNext() && isLeftNeedNext) {
            this.lValuesAndGVI = lValueIterator.next();
            this.rValueIterator = new SwiftValueIterator(rSegments, rKey);
            isLeftNeedNext = false;
            isLeftMatched = false;
        }
        while (rValueIterator.hasNext()) {
            this.rValuesAndGVI = rValueIterator.next();
            int result = lValuesAndGVI.compareTo(rValuesAndGVI, comparators);
            isLeftNeedNext = !rValueIterator.hasNext();
            if (result == 0) {
                writeOneGroup(lSegments, rSegments, lValuesAndGVI, rValuesAndGVI);
                isLeftMatched = true;
                return true;
            }
        }
        if (!isLeftMatched) {
            writeOneGroup(lSegments, rSegments, lValuesAndGVI, null);
        } else {
            if (!rValueIterator.hasNext()) {
                return leftJoin();
            }
        }
        return true;
    }

    private boolean outterJoin() {
        if (isFirstTraverse) {
            this.rValueIterator = new SwiftValueIterator(rSegments, rKey);
            int size = 0;
            while (rValueIterator.hasNext()) {
                this.rValueIterator.next();
                size++;
            }
            isMatched = new boolean[size];
            isFirstTraverse = false;
        }
        if (!isLeftEnd) {
            if (lValueIterator.hasNext() && isLeftNeedNext) {
                this.lValuesAndGVI = lValueIterator.next();
                rightCursor = -1;
                this.rValueIterator = new SwiftValueIterator(rSegments, rKey);
                isLeftNeedNext = false;
                isLeftMatched = false;
            }
            while (rValueIterator.hasNext()) {
                this.rValuesAndGVI = rValueIterator.next();
                rightCursor++;
                int result = lValuesAndGVI.compareTo(rValuesAndGVI, comparators);
                isLeftNeedNext = !rValueIterator.hasNext();
                if (!lValueIterator.hasNext() && !rValueIterator.hasNext()) {
                    isLeftEnd = true;
                }
                if (result == 0) {
                    writeOneGroup(lSegments, rSegments, lValuesAndGVI, rValuesAndGVI);
                    isMatched[rightCursor] = true;
                    isLeftMatched = true;
                    return true;
                }
            }
            if (!isLeftMatched) {
                writeOneGroup(lSegments, rSegments, lValuesAndGVI, null);
                if (!lValueIterator.hasNext() && !rValueIterator.hasNext()) {
                    isLeftEnd = true;
                }
            } else {
                if (!rValueIterator.hasNext()) {
                    return outterJoin();
                }
            }
            return true;
        } else {
            if (isFirstInit) {
                this.rValueIterator = new SwiftValueIterator(rSegments, rKey);
                isFirstInit = false;
            }
            while (rValueIterator.hasNext()) {
                this.rValuesAndGVI = this.rValueIterator.next();
                arrayIndex++;
                if (arrayIndex < isMatched.length) {
                    if (!isMatched[arrayIndex]) {
                        writeOneGroup(lSegments, rSegments, null, rValuesAndGVI);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private boolean innerJoin() {
        if (!lValueIterator.hasNext() && !rValueIterator.hasNext()) {
            return false;
        }
        if (lValueIterator.hasNext() && isLeftNeedNext) {
            this.lValuesAndGVI = lValueIterator.next();
            this.rValueIterator = new SwiftValueIterator(rSegments, rKey);
            isLeftNeedNext = false;
            isLeftMatched = false;
        }
        while (rValueIterator.hasNext()) {
            this.rValuesAndGVI = rValueIterator.next();
            int result = lValuesAndGVI.compareTo(rValuesAndGVI, comparators);
            isLeftNeedNext = !rValueIterator.hasNext();
            if (result == 0) {
                writeOneGroup(lSegments, rSegments, lValuesAndGVI, rValuesAndGVI);
                isLeftMatched = true;
                return true;
            }
        }
        /*if (isLeftMatched) {
            return innerJoin();
        }
        return false;*/


        return innerJoin();
    }

    public boolean rightJoin() {
        if (!lValueIterator.hasNext() && !rValueIterator.hasNext()) {
            return false;
        }
        if (rValueIterator.hasNext() && isRightNeedNext) {
            this.rValuesAndGVI = rValueIterator.next();
            this.lValueIterator = new SwiftValueIterator(lSegments, lKey);
            isRightNeedNext = false;
            isRightMatched = false;
        }
        while (lValueIterator.hasNext()) {
            this.lValuesAndGVI = lValueIterator.next();
            int result = rValuesAndGVI.compareTo(lValuesAndGVI, comparators);
            isRightNeedNext = !lValueIterator.hasNext();
            if (result == 0) {
                writeOneGroup(lSegments, rSegments, lValuesAndGVI, rValuesAndGVI);
                isRightMatched = true;
                return true;
            }
        }
        if (!isRightMatched) {
            writeOneGroup(lSegments, rSegments, null, rValuesAndGVI);
        } else {
            if (!lValueIterator.hasNext()) {
                return rightJoin();
            }
        }
        return true;
    }

    @Override
    public boolean next() {
        if (nullContinue == false && writeLeft == false) {
            return leftJoin();
        } else if (nullContinue == false && writeLeft == true) {
            return outterJoin();
        } else if (nullContinue == true && writeLeft == false) {
            return innerJoin();
        } else {
            return rightJoin();
        }
    }

    @Override
    public SwiftMetaData getMetaData() {
        return null;
    }

    @Override
    public Row getRowData() {
        return listRow.getBasedRow();
    }

    private void writeOneGroup(Segment[] lSegment, Segment[] rSegment, GVIAndSegment lGroup, GVIAndSegment rGroup) {
        List valueList = new ArrayList();
        for (JoinColumn c : columns) {
            if (c.isLeft()) {
                if (lGroup != null) {
                    DictionaryEncodedColumn getter = lGroup.getSegment().getColumn(new ColumnKey(c.getColumnName())).getDictionaryEncodedColumn();
                    int indexByRow = getter.getIndexByRow(lGroup.getRow());
                    valueList.add(getter.getValue(indexByRow));
                } else {
                    valueList.add(null);
                }
            } else {
                if (rGroup != null) {
                    DictionaryEncodedColumn getter = rGroup.getSegment().getColumn(new ColumnKey(c.getColumnName())).getDictionaryEncodedColumn();
                    int indexByRow = getter.getIndexByRow(rGroup.getRow());
                    valueList.add(getter.getValue(indexByRow));
                } else {
                    valueList.add(null);
                }
            }
        }
        ListBasedRow basedRow = new ListBasedRow(valueList);
        listRow.setBasedRow(basedRow);
    }

    private class ListRow {
        ListBasedRow basedRow;

        public ListBasedRow getBasedRow() {
            return basedRow;
        }

        public void setBasedRow(ListBasedRow basedRow) {
            this.basedRow = basedRow;
        }

    }
}

