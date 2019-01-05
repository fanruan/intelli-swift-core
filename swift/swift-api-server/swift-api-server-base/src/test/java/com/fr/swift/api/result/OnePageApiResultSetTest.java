package com.fr.swift.api.result;

import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author yee
 * @date 2019-01-03
 */
public class OnePageApiResultSetTest {

    @Test
    public void getPage() throws SQLException {
        // Generate by Mock Plugin
        final SwiftMetaData mockSwiftMetaData = PowerMock.createMock(SwiftMetaData.class);
        final List<Row> rows = Arrays.<Row>asList(new ListBasedRow(1L), new ListBasedRow(2L), new ListBasedRow(3L));
        // Generate by Mock Plugin
        OnePageApiResultSet mockOnePageApiResultSet = PowerMock.createMock(OnePageApiResultSet.class, null, mockSwiftMetaData, rows, rows.size(), true);
        SwiftApiResultSet nextPage = PowerMock.createMock(OnePageApiResultSet.class, null, mockSwiftMetaData, rows, rows.size(), false);
        EasyMock.expect(mockOnePageApiResultSet.queryNextPage(EasyMock.anyObject())).andReturn(nextPage).once();
        PowerMock.replayAll();
        int count = 0;
        while (mockOnePageApiResultSet.hasNext()) {
            Row row = mockOnePageApiResultSet.getNextRow();
            assertNotNull(row);
            count++;
        }

        assertEquals(2 * rows.size(), count++);
        PowerMock.verifyAll();
    }
}