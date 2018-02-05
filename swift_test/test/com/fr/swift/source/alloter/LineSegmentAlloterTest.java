package com.fr.swift.source.alloter;

import com.fr.stable.StringUtils;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.ISegmentOperator;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataImpl;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceAlloter;
import com.fr.swift.source.SwiftSourceAlloterFactory;
import junit.framework.TestCase;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yee
 * @date 2017/12/19
 */
public class LineSegmentAlloterTest extends TestCase {
    SwiftResultSet resultSet;
    List<Segment> segments;
    int count;

    @Override
    protected void setUp() throws Exception {
        List<Row> datas = new ArrayList<>();
        count = (int) (Math.random() * 1000000);
        System.err.println(count);
        for (int i = 0; i < count; i++) {
            List data = new ArrayList<Long>();
            data.add((long) i);
            datas.add(new ListBasedRow(data));
        }
        List data = new ArrayList<Long>();
        data.add(null);
        datas.add(new ListBasedRow(data));
        resultSet = new SwiftResultSet() {
            int position = 0;

            @Override
            public void close() throws SQLException {

            }

            @Override
            public boolean next() throws SQLException {
                return position < datas.size();
            }

            @Override
            public SwiftMetaData getMetaData() throws SQLException {
                return new SwiftMetaDataImpl("A",
                        Arrays.asList(new MetaDataColumn("long", Types.BIGINT)));
            }

            @Override
            public Row getRowData() throws SQLException {
                return datas.get(position++);
            }
        };

    }

    public void testAlloc() throws Exception {
        String key = String.valueOf(System.currentTimeMillis());
        SourceKey sourceKey = new SourceKey(key);
        ISegmentOperator operator = LocalSegmentProvider.getInstance().getIndexSegmentOperator(sourceKey, resultSet.getMetaData());
        operator.transport(resultSet);
        operator.finishTransport();

        segments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ResourceLocation location = new ResourceLocation(System.getProperty("user.dir") + "/cubes/" + sourceKey.getId() + "/" + resultSet.getMetaData().getTableName() + "/seg" + i);
            Segment segment = new HistorySegmentImpl(location, resultSet.getMetaData());
            segments.add(segment);
        }
//
        SwiftSourceAlloter alloter = SwiftSourceAlloterFactory.createSourceAlloter(sourceKey);
        int lastIndex = -1;
        for (int i = 0; i < count; i++) {
            int index = alloter.allot(i, StringUtils.EMPTY, null);
            if (lastIndex != index) {
                lastIndex = index;
                System.err.println("index: " + index);
                System.err.println("rowCount: " + segments.get(index).getRowCount());
            }
            DetailColumn column = segments.get(index).getColumn(new ColumnKey("long")).getDetailColumn();
//            System.out.println(index);
            assertEquals(column.getLong(i), (long) i);
        }
    }
}
