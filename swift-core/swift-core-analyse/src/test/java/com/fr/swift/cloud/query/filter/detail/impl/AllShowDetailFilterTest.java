package com.fr.swift.cloud.query.filter.detail.impl;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.bitmap.impl.RoaringImmutableBitMap;
import com.fr.swift.cloud.query.filter.match.ToStringConverter;
import com.fr.swift.cloud.segment.Segment;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

/**
 * Created by pony on 2018/3/23.
 */
public class AllShowDetailFilterTest extends TestCase {

    private AllShowDetailFilter filter;
    private ImmutableBitMap allShow;

    public void setUp() {
        IMocksControl control = EasyMock.createControl();
        Segment segment = control.createMock(Segment.class);
        allShow = RoaringImmutableBitMap.of();
        filter = new AllShowDetailFilter(segment);
        EasyMock.expect(segment.getAllShowIndex()).andReturn(allShow).anyTimes();
        control.replay();
    }

    public void testCreateFilterIndex() {
        assertEquals(filter.createFilterIndex(), allShow);
    }

    public void testMatches() {
        assertTrue(filter.matches(null, 0, new ToStringConverter()));
    }
}