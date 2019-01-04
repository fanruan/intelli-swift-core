package com.fr.swift.source.alloter;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import org.junit.Before;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yee
 * @date 2017/12/19
 */
public class LineSegmentAlloterTest {
    SwiftResultSet resultSet;
    int count;

    @Before
    public void setUp() throws Exception {
//        Preparer.prepareCubeBuild(getClass());

        final List<Row> datas = new ArrayList<Row>();
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
            public void close() {

            }

            @Override
            public boolean hasNext() {
                return position < datas.size();
            }

            @Override
            public int getFetchSize() {
                return 0;
            }

            @Override
            public SwiftMetaData getMetaData() {
                return new SwiftMetaDataBean("A",
                        Arrays.<SwiftMetaDataColumn>asList(new MetaDataColumnBean("long", Types.BIGINT)));
            }

            @Override
            public Row getNextRow() {
                return datas.get(position++);
            }
        };

    }

//    @Test
//    public void testAlloc() throws Exception {
//        final SourceKey sourceKey = new SourceKey("A");
//
//        DataSource dataSource = new DataSource() {
//            @Override
//            public SwiftMetaData getMetadata() {
//                try {
//                    return resultSet.getMetaData();
//                } catch (Exception e) {
//                    return null;
//                }
//            }
//
//            @Override
//            public SourceKey getSourceKey() {
//                return sourceKey;
//            }
//
//            @Override
//            public Core fetchObjectCore() {
//                return null;
//            }
//        };
//
//        Inserter inserter = (Inserter) SwiftContext.get().getBean("historyBlockInserter", dataSource);
//        inserter.insertData(resultSet);
//        SwiftSourceAlloter alloter = SwiftSourceAlloterFactory.createLineSourceAlloter(sourceKey, sourceKey.getId());
//        int lastIndex = -1;
//        Segment segment = null;
//        DetailColumn column = null;
//        for (int i = 0; i < count; i++) {
//            int index = alloter.allot(new LineRowInfo(i)).getOrder();
//            if (lastIndex != index || null == segment) {
//                lastIndex = index;
//                ResourceLocation location = new ResourceLocation(
//                        CubeUtil.getHistorySegPath(dataSource, CubeUtil.getCurrentDir(dataSource.getSourceKey()), index));
//                segment = new HistorySegmentImpl(location, resultSet.getMetaData());
//                column = segment.getColumn(new ColumnKey("long")).getDetailColumn();
//            }
//            Assert.assertEquals(column.getLong(i % ((LineAllotRule) alloter.getAllotRule()).getStep()), (long) i);
//        }
//    }
}
