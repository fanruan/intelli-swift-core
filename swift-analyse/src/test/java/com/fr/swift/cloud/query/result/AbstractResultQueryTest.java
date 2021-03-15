package com.fr.swift.cloud.query.result;

import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import java.util.Collections;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/7/3
 */
public class AbstractResultQueryTest {

    @Test
    public void getQueryResult() throws Exception {
        AbstractResultQuery resultQuery = mock(AbstractResultQuery.class, Mockito.CALLS_REAL_METHODS);
        Whitebox.setInternalState(resultQuery, "fetchSize", 1);
        Query<QueryResultSet<?>> query = mock(Query.class);
        Whitebox.setInternalState(resultQuery, "queries", Collections.singletonList(query));

        QueryResultSet resultSet = mock(QueryResultSet.class);
        when(query.getQueryResult()).thenReturn(resultSet);

        QueryResultSet mergeResultSet = mock(QueryResultSet.class);
        PowerMockito.when(resultQuery.merge(Collections.singletonList(resultSet))).thenReturn(mergeResultSet).thenThrow(new RuntimeException());

        Assertions.assertThat(resultQuery.getQueryResult()).isEqualTo(mergeResultSet);

        try {
            resultQuery.getQueryResult();
            fail();
        } catch (Exception ignore) {
        }

        verify(resultSet).close();
    }
}