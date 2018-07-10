package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.RoaringImmutableBitMap;
import com.fr.swift.query.filter.match.ToStringConverter;
import com.fr.swift.segment.Segment;
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