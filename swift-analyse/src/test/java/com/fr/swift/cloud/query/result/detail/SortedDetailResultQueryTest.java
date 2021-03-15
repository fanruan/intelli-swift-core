package com.fr.swift.cloud.query.result.detail;

import com.fr.swift.cloud.result.detail.MergeSortedDetailQueryResultSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Comparator;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Create by lifan on 2019-07-02 16:29
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SortedDetailResultQuery.class})
public class SortedDetailResultQueryTest {

    @Test
    public void merge() throws Exception {
        int fetchSize = mock(Integer.class);
        List queries = mock(List.class);
        List comparators = mock(List.class);
        List queryResultSets = mock(List.class);
        Comparator comparator = mock(Comparator.class);

        mockStatic(SortedDetailResultQuery.class);
        doReturn(comparator).when(SortedDetailResultQuery.class);
        Whitebox.invokeMethod(SortedDetailResultQuery.class, "createRowComparator", comparators);

        MergeSortedDetailQueryResultSet mergeDetailQueryResultSet = mock(MergeSortedDetailQueryResultSet.class);
        whenNew(MergeSortedDetailQueryResultSet.class).withArguments(fetchSize, comparator, queryResultSets).thenReturn(mergeDetailQueryResultSet);

        Assert.assertEquals(mergeDetailQueryResultSet, new SortedDetailResultQuery(fetchSize, queries, comparators).merge(queryResultSets));

    }


}