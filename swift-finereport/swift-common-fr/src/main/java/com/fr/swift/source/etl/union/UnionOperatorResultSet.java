package com.fr.swift.source.etl.union;

import com.fr.stable.StringUtils;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/17 0017 09:54
 */
public class UnionOperatorResultSet implements SwiftResultSet {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(UnionOperatorResultSet.class);
    private List<List<String>> unionColumns;
    private List<List<Segment>> segments;
    //当前segment序号
    private int currentSegmentIndex = 0;
    //当前表的序号
    private int currentTableIndex = 0;
    //当前的列
    private ColumnDetailValueConverter[] currentColumns = null;
    private AllShowIterator iterator = null;
    private SwiftMetaData metaData = null;

    public UnionOperatorResultSet(List<List<String>> unionColumns, List<List<Segment>> segments, SwiftMetaData metaData) {
        this.unionColumns = unionColumns;
        this.segments = segments;
        this.metaData = metaData;
        move2NextSegment();
    }

    private void move2NextSegment() {
        currentColumns = new ColumnDetailValueConverter[this.unionColumns.size()];
        Segment segment = getNextSegment();
        if (segment != null) {
            iterator = new AllShowIterator(segment.getAllShowIndex(), segment.getRowCount());
            for (int i = 0; i < currentColumns.length; i++) {
                String name = this.unionColumns.get(i).get(currentTableIndex + 1);
                //没设置就全部返回null
                if (StringUtils.isEmpty(name)) {
                    currentColumns[i] = new NullDetailValueConverter();
                    continue;
                }
                Column column = segment.getColumn(new ColumnKey(name));
                try {
                    SwiftMetaData segMeta = segment.getMetaData();
                    //BI-17588 这边要用union的名字，不能用之前的名字
                    String currentName = this.unionColumns.get(i).get(0);
                    SwiftMetaDataColumn currentMetaDataColumn = metaData.getColumn(currentName);
                    SwiftMetaDataColumn segColumn = segMeta.getColumn(name);
                    ColumnTypeConstants.ClassType currentType = ColumnTypeUtils.getClassType(currentMetaDataColumn);
                    ColumnTypeConstants.ClassType segType = ColumnTypeUtils.getClassType(segColumn);
                    //根据字段类型，决定类型转化
                    if (currentType == segType) {
                        currentColumns[i] = new OriginColumnDetailValueConverter(column.getDictionaryEncodedColumn());
                    } else if (currentType == ColumnTypeConstants.ClassType.LONG) {
                        currentColumns[i] = new LongDetailValueConverter(column.getDictionaryEncodedColumn());
                    } else {
                        currentColumns[i] = new DoubleDetailValueConverter(column.getDictionaryEncodedColumn());
                    }
                } catch (SwiftMetaDataException e) {
                    LOGGER.error(e);
                    currentColumns[i] = new NullDetailValueConverter();
                }
            }
        } else {
            //取不到segment说明结束了，置空结束循环
            iterator = null;
        }
    }

    @Override
    public void close() {

    }

    @Override
    public boolean hasNext() {
        while (iterator != null && !iterator.hasNext()) {
            move2NextSegment();
        }
        return iterator != null && iterator.hasNext();
    }


    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return metaData;
    }

    @Override
    public Row getNextRow() {
        List list = new ArrayList();
        int row = iterator.next();
        for (int i = 0; i < currentColumns.length; i++) {
            list.add(currentColumns[i].convertValue(row));
        }
        return new ListBasedRow(list);
    }

    public Segment getNextSegment() {
        while (currentTableIndex < segments.size()) {
            List<Segment> currentSegments = segments.get(currentTableIndex);
            if (currentSegments.size() > currentSegmentIndex) {
                return currentSegments.get(currentSegmentIndex++);
            }
            currentTableIndex++;
            currentSegmentIndex = 0;
        }
        return null;
    }

    //把删除掉的行去掉的迭代器
    private class AllShowIterator {
        private int index = 0;
        private ImmutableBitMap bitMap;
        private int row;

        public AllShowIterator(ImmutableBitMap bitMap, int row) {
            this.bitMap = bitMap;
            this.row = row;
        }

        public boolean hasNext() {
            while (!bitMap.contains(index) && index < row) {
                index++;
            }
            return index < row;
        }

        public int next() {
            return index++;
        }
    }


}
