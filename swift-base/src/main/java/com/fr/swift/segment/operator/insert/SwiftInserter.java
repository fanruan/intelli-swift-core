package com.fr.swift.segment.operator.insert;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.utils.InserterUtils;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.structure.ListResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description 指定数据和块insert
 * todo 支持断点输入流式数据，也支持实时导入数据、实时查询数据功能。
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftInserter implements Inserter {
    private Segment segment;

    private List<String> fields;

    private Map<Integer, MutableBitMap> nullIndices = new HashMap<Integer, MutableBitMap>();

    private List<Column> columns = new ArrayList<Column>();

    private List<ClassType> classTypes = new ArrayList<ClassType>();

    public SwiftInserter(Segment segment) {
        this(segment, segment.getMetaData().getFieldNames());
    }

    public SwiftInserter(Segment segment, List<String> fields) {
        this.fields = fields;
        this.segment = segment;
        try {
            init();
        } catch (SwiftMetaDataException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private void init() throws SwiftMetaDataException {
        SwiftMetaData metaData = segment.getMetaData();

        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            ClassType clazz = ColumnTypeUtils.getClassType(metaData.getColumn(field));
            Column column = segment.getColumn(new ColumnKey(field));
            columns.add(column);
            classTypes.add(clazz);
            nullIndices.put(i, BitMaps.newRoaringMutable());
        }
    }

    @Override
    public List<Segment> insertData(List<Row> rowList) throws SQLException {
        return insertData(new ListResultSet(segment.getMetaData(), rowList));
    }

    @Override
    public List<Segment> insertData(SwiftResultSet swiftResultSet) throws SQLException {
        return insert(swiftResultSet);
    }

    private List<Segment> insert(SwiftResultSet resultSet) throws SQLException {
        // fixme 要从配置里判断，这里有可能读的recorder备份的数据
        boolean readable = CubeUtil.isReadable(segment);
        int lastCursor = readable ? segment.getRowCount() : 0,
                cursor = lastCursor;

        while (resultSet.next()) {
            Row rowData = resultSet.getRowData();
            putRow(cursor, rowData);
            cursor++;
        }

        putNullIndex(readable);

        putSegmentInfo(readable, lastCursor, cursor);

        release();

        return Collections.singletonList(segment);
    }

    private void putSegmentInfo(boolean readable, int lastCursor, int cursor) {
        ImmutableBitMap allShowIndex = readable ? segment.getAllShowIndex() : BitMaps.newRoaringMutable();
        allShowIndex = allShowIndex.getOr(new RangeBitmap(lastCursor, cursor));
        segment.putRowCount(cursor);
        segment.putAllShowIndex(allShowIndex);
    }

    private void putNullIndex(boolean readable) {
        for (int i = 0; i < columns.size(); i++) {
            BitmapIndexedColumn bitmapIndex = columns.get(i).getBitmapIndex();
            ImmutableBitMap nullIndex = readable ? bitmapIndex.getNullIndex() : BitMaps.newRoaringMutable();
            bitmapIndex.putNullIndex(nullIndex.getOr(nullIndices.get(i)));
        }
    }

    protected void putRow(int cursor, Row rowData) {
        for (int i = 0; i < fields.size(); i++) {
            DetailColumn detail = columns.get(i).getDetailColumn();
            if (InserterUtils.isBusinessNullValue(rowData.getValue(i))) {
                detail.put(cursor, InserterUtils.getNullValue(classTypes.get(i)));
                nullIndices.get(i).add(cursor);
            } else {
                detail.put(cursor, rowData.getValue(i));
            }
        }
    }

    private void release() {
        if (segment.getLocation().getStoreType() == StoreType.FINE_IO) {
            for (Column column : columns) {
                column.getDetailColumn().release();
                column.getBitmapIndex().release();
            }

            segment.release();
        }
    }

    @Override
    public List<String> getFields() {
        return fields;
    }
}