package com.fr.swift.segment;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.test.Preparer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class IncrementerTest {
    private static DataSource dataSource;

    private static SwiftSourceTransfer transfer;

    @Before
    public void beforeClass() throws Exception {
        Preparer.prepareCubeBuild(getClass());
        dataSource = new QueryDBSource("select 客户状态 from DEMO_CUSTOMER", IncrementerTest.class.getSimpleName());
        transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);

        if (!SwiftDatabase.getInstance().existsTable(dataSource.getSourceKey())) {
            SwiftDatabase.getInstance().createTable(dataSource.getSourceKey(), dataSource.getMetadata());
        }
    }

    @Test
    public void increment() throws Exception {
        Incrementer incrementer = new Incrementer(dataSource);
        incrementer.insertData(transfer.createResultSet());
        incrementer.insertData(transfer.createResultSet());
        Segment seg = SwiftContext.get().getBean(LocalSegmentProvider.class).getSegment(dataSource.getSourceKey()).get(0);

        int rowCount = seg.getRowCount();
        seg.getAllShowIndex();
        Column<Object> column = seg.getColumn(new ColumnKey("客户状态"));
        DetailColumn<Object> detail = column.getDetailColumn();
        for (int i = 0; i < rowCount / 2; i++) {
            Assert.assertEquals(detail.get(i), detail.get(i + rowCount / 2));
        }
        column.getBitmapIndex().getNullIndex();
    }
}