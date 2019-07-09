package com.fr.swift.query.result.group;

import com.fr.swift.result.funnel.MergeFunnelQueryResultSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Create by lifan on 2019-07-02 18:10
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({FunnelResultQuery.class})
public class FunnelResultQueryTest {

    @Test
    public void merge() throws Exception {
        int numberOfSteps = mock(Integer.class);
        List queries = mock(List.class);
        List resultSets = new ArrayList();
        MergeFunnelQueryResultSet mergeFunnelQueryResultSet = mock(MergeFunnelQueryResultSet.class);

        whenNew(MergeFunnelQueryResultSet.class).withArguments(resultSets, numberOfSteps).thenReturn(mergeFunnelQueryResultSet);

        Assert.assertEquals(mergeFunnelQueryResultSet, new FunnelResultQuery(numberOfSteps, queries).merge(resultSets));


    }
}