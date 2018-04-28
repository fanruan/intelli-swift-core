package com.fr.swift.segment.relation.column;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.source.RelationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author yee
 * @date 2018/4/3
 */
public class RelationColumn {
    private RelationIndex relationIndex;
    private Segment[] segments;
    private ColumnKey columnKey;
    private DictionaryEncodedColumn[] columns;
    private BitmapIndexedColumn[] bitmapIndexedColumns;
    private RelationSource relationSource;
    private int reverseCount;

    /**
     * @param relationIndex 关联索引操作器
     * @param segments      主表的所有segment
     * @param columnKey     主表的column
     */
    public RelationColumn(RelationIndex relationIndex, Segment[] segments, ColumnKey columnKey) {
        this.relationIndex = relationIndex;
        this.segments = segments;
        this.columnKey = columnKey;
        this.columns = new DictionaryEncodedColumn[segments.length];
        this.reverseCount = relationIndex.getReverseCount();
        this.relationSource = columnKey.getRelation();
        this.bitmapIndexedColumns = new BitmapIndexedColumn[segments.length];
    }

    public RelationColumn(RelationIndex relationIndex, List<Segment> segments, ColumnKey columnKey) {
        this.segments = new Segment[segments.size()];
        this.segments = segments.toArray(this.segments);
        this.relationIndex = relationIndex;
        this.columnKey = columnKey;
        this.columns = new DictionaryEncodedColumn[segments.size()];
        this.reverseCount = relationIndex.getReverseCount();
        this.relationSource = columnKey.getRelation();
        this.bitmapIndexedColumns = new BitmapIndexedColumn[segments.size()];
    }

    public RelationColumn(RelationIndex relationIndex, Segment[] segments, RelationSource relationSource) {
        this.relationIndex = relationIndex;
        this.segments = segments;
        this.relationSource = relationSource;
    }

    public RelationColumn(RelationIndex relationIndex, List<Segment> segments, RelationSource relationSource) {
        this.relationIndex = relationIndex;
        this.segments = new Segment[segments.size()];
        this.segments = segments.toArray(this.segments);
        this.relationSource = relationSource;
    }

    /**
     * 通过子表行号取值
     *
     * @param row
     * @return
     */
    public Object getPrimaryValue(int row) {
        int[] result = getPrimarySegAndRow(row);
        if (null != result) {
            if (columns[result[0]] == null) {
                columns[result[0]] = segments[result[0]].getColumn(columnKey).getDictionaryEncodedColumn();
            }
            return columns[result[0]].getValue(columns[result[0]].getIndexByRow(result[1]));
        }
        return null;
    }

    public List<KeyRow> getRows(Set<String> values) {
        final List<String> primaryFields = relationSource.getPrimaryFields();

        final List<KeyRow> result = new ArrayList<KeyRow>();
        for (int i = 0; i < segments.length; i++) {
            for (String value : values) {
                if (columns[i] == null) {
                    columns[i] = segments[i].getColumn(columnKey).getDictionaryEncodedColumn();
                }
                if (bitmapIndexedColumns[i] == null) {
                    bitmapIndexedColumns[i] = segments[i].getColumn(columnKey).getBitmapIndex();
                }
                int index = columns[i].getIndex(value);
                if (0 != index) {
                    ImmutableBitMap bitMap = bitmapIndexedColumns[i].getBitMapIndex(index);
                    final int finalI = i;
                    bitMap.traversal(new TraversalAction() {
                        @Override
                        public void actionPerformed(int row) {
                            Object[] data = new Object[primaryFields.size()];
                            for (int f = 0; f < primaryFields.size(); f++) {
                                String field = primaryFields.get(f);
                                Column primaryColumn = segments[finalI].getColumn(new ColumnKey(field));
                                DictionaryEncodedColumn dicColumn = primaryColumn.getDictionaryEncodedColumn();
                                data[f] = dicColumn.getValue(dicColumn.getIndexByRow(row));
                                dicColumn.release();
                            }
                            KeyRow rowData = new KeyRow(data);
                            if (!result.contains(rowData)) {
                                result.add(rowData);
                            }
                        }
                    });

                }
            }
        }
        return result;
    }

    public KeyRow getPrimaryRows(int row) {
        List<String> primaryFields = relationSource.getPrimaryFields();
        int[] segAndRow = getPrimarySegAndRow(row);
        if (null != segAndRow) {
            Object[] data = new Object[primaryFields.size()];
            for (int i = 0; i < primaryFields.size(); i++) {
                String field = primaryFields.get(i);
                Column primaryColumn = segments[i].getColumn(new ColumnKey(field));
                DictionaryEncodedColumn dicColumn = primaryColumn.getDictionaryEncodedColumn();
                data[i] = dicColumn.getValue(dicColumn.getIndexByRow(segAndRow[1]));
            }
            return new KeyRow(data);
        }
        return null;
    }

    public int[] getPrimarySegAndRow(int row) {
        if (row < reverseCount) {
            long reverse = relationIndex.getReverseIndex(row);
            if (reverse != NIOConstant.LONG.NULL_VALUE) {
                return reverse2SegAndRow(reverse);
            }
        }
        return null;
    }

    public void release() {
        if (null != columns) {
            for (DictionaryEncodedColumn column : columns) {
                column.release();
            }
        }
    }

    /**
     * 根据long来获取segment序号和segment行号
     *
     * @param reverse
     * @return
     */
    private int[] reverse2SegAndRow(long reverse) {
        int[] result = new int[2];
        result[0] = (int) ((reverse & 0xFFFFFFFF00000000L) >> 32);
        result[1] = (int) (0xFFFFFFFFL & reverse);
        return result;
    }

    public class KeyRow {
        private Object[] data;

        public KeyRow(Object[] data) {
            this.data = data;
        }

        public Object[] getData() {
            return data;
        }

        public void setData(Object[] data) {
            this.data = data;
        }

        public int getColumnCount() {
            return null == data ? 0 : data.length;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            KeyRow keyRow = (KeyRow) o;

            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(data, keyRow.data);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(data);
        }
    }
}
