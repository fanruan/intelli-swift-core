package com.fr.swift.source.etl.detail;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2018/1/15.
 */
public class DetailTransferOperatorTest extends TestCase {

    public void testCreateResultSet() throws SQLException {
        ColumnKey columnKey = new ColumnKey("SALES_NAME");
        ColumnKey[] columnKeys = new ColumnKey[]{};
        List<ColumnKey[]> fields = new ArrayList<>();
        fields.add(columnKeys);
        DetailTransferOperator operator = new DetailTransferOperator(fields);
        IMocksControl control = EasyMock.createControl();
        Segment sg1 = control.createMock(Segment.class);
        Column column1 = control.createMock(Column.class);
        DictionaryEncodedColumn dic1 = control.createMock(DictionaryEncodedColumn.class);
        EasyMock.expect(sg1.getRowCount()).andReturn(2).anyTimes();
        EasyMock.expect(sg1.getColumn(EasyMock.anyObject())).andReturn(column1).anyTimes();
        EasyMock.expect(column1.getDictionaryEncodedColumn()).andReturn(dic1).anyTimes();
        EasyMock.expect(dic1.getIndexByRow(EasyMock.anyInt())).andReturn(1).anyTimes();
        EasyMock.expect(dic1.getValue(EasyMock.anyInt())).andReturn(1).anyTimes();
        Segment sgEmpty = control.createMock(Segment.class);
        EasyMock.expect(sgEmpty.getRowCount()).andReturn(0).anyTimes();
        EasyMock.expect(sgEmpty.getColumn(EasyMock.anyObject())).andReturn(column1).anyTimes();
        Segment sg2 = control.createMock(Segment.class);
        Column column2 = control.createMock(Column.class);
        DictionaryEncodedColumn dic2 = control.createMock(DictionaryEncodedColumn.class);
        EasyMock.expect(sg2.getRowCount()).andReturn(3).anyTimes();
        EasyMock.expect(sg2.getColumn(EasyMock.anyObject())).andReturn(column2).anyTimes();
        EasyMock.expect(column2.getDictionaryEncodedColumn()).andReturn(dic2).anyTimes();
        EasyMock.expect(dic2.getIndexByRow(EasyMock.anyInt())).andReturn(1).anyTimes();
        EasyMock.expect(dic2.getValue(EasyMock.anyInt())).andReturn(2).anyTimes();
        control.replay();
        List<Segment[]> segments = new ArrayList<>();
        Segment[] sgs = new Segment[]{sg1, sgEmpty, sg2};
        segments.add(sgs);
        SwiftResultSet resultSet = operator.createResultSet(null, null, segments);
        List list = new ArrayList();
        while (resultSet.hasNext()) {
            list.add(resultSet.getNextRow());
        }
        assertEquals(list.size(), 2);
    }
}