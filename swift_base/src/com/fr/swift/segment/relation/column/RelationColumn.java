package com.fr.swift.segment.relation.column;

import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.relation.RelationIndex;

import java.util.List;

/**
 * @author yee
 * @date 2018/4/3
 */
public class RelationColumn {
    private RelationIndex relationIndex;
    private Segment[] segments;
    private ColumnKey columnKey;
    private DictionaryEncodedColumn[] columns;
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
    }

    public RelationColumn(RelationIndex relationIndex, List<Segment> segments, ColumnKey columnKey) {
        this.segments = new Segment[segments.size()];
        this.segments = segments.toArray(this.segments);
        this.relationIndex = relationIndex;
        this.columnKey = columnKey;
        this.columns = new DictionaryEncodedColumn[segments.size()];
        this.reverseCount = relationIndex.getReverseCount();
    }

    /**
     * 通过子表行号取值
     *
     * @param row
     * @return
     */
    public Object getValue(int row) {
        int[] result = getPrimarySegAndRow(row);
        if (null != result) {
            if (columns[result[0]] == null) {
                columns[result[0]] = segments[result[0]].getColumn(columnKey).getDictionaryEncodedColumn();
            }
            return columns[result[0]].getValue(columns[result[0]].getIndexByRow(result[1]));
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
}
