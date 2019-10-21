package com.fr.swift.query.result.detail;

import com.fr.swift.result.detail.MergeDetailQueryResultSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Create by lifan on 2019-07-02 16:17
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({DetailResultQuery.class})
public class DetailResultQueryTest {

    @Test
    public void merge() throws Exception {
        List queries = mock(List.class);
        List resultsets = mock(List.class);
        int fetchSize = mock(Integer.class);

        MergeDetailQueryResultSet mergeDetailQueryResultSet = mock(MergeDetailQueryResultSet.class);
        whenNew(MergeDetailQueryResultSet.class).withArguments(fetchSize, resultsets).thenReturn(mergeDetailQueryResultSet);

        assertEquals(mergeDetailQueryResultSet, new DetailResultQuery(fetchSize, queries).merge(resultsets));
    }

}