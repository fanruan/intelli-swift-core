package com.fr.swift.cloud.query.segment.detail;


import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.result.detail.SegmentDetailResultSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Create by lifan on 2019-07-02 10:23
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({DetailSegmentQuery.class})
public class DetailSegmentQueryTest {


    @Test
    public void getQueryResult() throws Exception {
        int fetchSize = mock(Integer.class);
        List colunmnList = new ArrayList();
        DetailFilter filter = mock(DetailFilter.class);
        ImmutableBitMap filterIndex = mock(ImmutableBitMap.class);
        when(filter.createFilterIndex()).thenReturn(filterIndex);

        SegmentDetailResultSet segmentDetailResultSet = mock(SegmentDetailResultSet.class);
        whenNew(SegmentDetailResultSet.class).withArguments(fetchSize, colunmnList, filter).thenReturn(segmentDetailResultSet);

        Assert.assertEquals(segmentDetailResultSet, new DetailSegmentQuery(fetchSize, colunmnList, filter,null).getQueryResult());
        verifyNew(SegmentDetailResultSet.class).withArguments(fetchSize, colunmnList, filter);

    }

}