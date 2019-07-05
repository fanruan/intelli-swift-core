package com.fr.swift.query.result.group;

import com.fr.swift.result.node.resultset.MergeGroupQueryResultSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Create by lifan on 2019-07-01 18:25
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GroupResultQuery.class})
public class GroupResultQueryTest {


    // TODO: 2019-07-03  
    @Test
    public void merge() throws Exception {
        List queries = mock(List.class);
        List aggs = mock(List.class);
        List cmps = new ArrayList();
        boolean[] isGlobalIndexed = new boolean[0];

        MergeGroupQueryResultSet mergeGroupQueryResultSet = mock(MergeGroupQueryResultSet.class);
        whenNew(MergeGroupQueryResultSet.class).withArguments(1, isGlobalIndexed, queries, aggs, cmps).thenReturn(mergeGroupQueryResultSet);

        assertEquals(mergeGroupQueryResultSet, new GroupResultQuery(1, queries, aggs, cmps, isGlobalIndexed).merge(queries));
    }
}