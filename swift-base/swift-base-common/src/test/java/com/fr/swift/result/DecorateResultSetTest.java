package com.fr.swift.result;

import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * @author anchore
 * @date 2019/3/13
 */
public class DecorateResultSetTest {

    @Test
    public void testAll() throws SQLException {
        SwiftResultSet resultSet = mock(SwiftResultSet.class, Mockito.RETURNS_DEEP_STUBS);
        DecorateResultSet decorateResultSet = new DecorateResultSet(resultSet);

        assertEquals(resultSet.getFetchSize(), decorateResultSet.getFetchSize());
        assertEquals(resultSet.getMetaData(), decorateResultSet.getMetaData());
        assertEquals(resultSet.hasNext(), decorateResultSet.hasNext());
        assertEquals(resultSet.getNextRow(), decorateResultSet.getNextRow());

        decorateResultSet.close();
        verify(resultSet).close();
    }
}