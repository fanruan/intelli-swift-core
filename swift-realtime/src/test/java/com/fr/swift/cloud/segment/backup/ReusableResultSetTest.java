package com.fr.swift.cloud.segment.backup;

import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.source.Row;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/3/13
 */
public class ReusableResultSetTest {

    @Test
    public void reuse() throws SQLException {
        SwiftResultSet origin = mock(SwiftResultSet.class, Mockito.RETURNS_DEEP_STUBS);
        when(origin.getFetchSize()).thenReturn(1);

        when(origin.hasNext()).thenReturn(true, true, false);

        Row row0 = mock(Row.class), row1 = mock(Row.class);
        when(origin.getNextRow()).thenReturn(row0, row1);

        SwiftResultSet resultSet = new ReusableResultSet(origin);
        verify(origin).close();

        assertEquals(origin.getFetchSize(), resultSet.getFetchSize());
        assertEquals(origin.getMetaData(), resultSet.getMetaData());

        assertTrue(resultSet.hasNext());
        assertEquals(row0, resultSet.getNextRow());
        assertTrue(resultSet.hasNext());
        assertEquals(row1, resultSet.getNextRow());
        assertFalse(resultSet.hasNext());

        resultSet = ((ReusableResultSet) resultSet).reuse();

        assertEquals(origin.getFetchSize(), resultSet.getFetchSize());
        assertEquals(origin.getMetaData(), resultSet.getMetaData());

        assertTrue(resultSet.hasNext());
        assertEquals(row0, resultSet.getNextRow());
        assertTrue(resultSet.hasNext());
        assertEquals(row1, resultSet.getNextRow());
        assertFalse(resultSet.hasNext());
    }
}