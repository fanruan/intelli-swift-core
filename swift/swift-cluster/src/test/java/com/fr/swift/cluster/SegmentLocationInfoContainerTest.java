package com.fr.swift.cluster;

import com.fr.swift.cluster.service.SegmentLocationInfoContainer;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.structure.Pair;
import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * This class created on 2019/1/3
 *
 * @author Lucifer
 * @description
 */
public class SegmentLocationInfoContainerTest extends TestCase {

    public void testSegmentLocationInfoContainer() {
        SegmentLocationInfo segmentLocationInfo = EasyMock.createMock(SegmentLocationInfo.class);
        EasyMock.replay(segmentLocationInfo);

        assertNotNull(SegmentLocationInfoContainer.getContainer());
        SegmentLocationInfoContainer.getContainer().add(Pair.of(SegmentLocationInfo.UpdateType.ALL, segmentLocationInfo));
        assertEquals(SegmentLocationInfoContainer.getContainer().getLocationInfo().size(), 1);
        SegmentLocationInfoContainer.getContainer().clean();
        assertTrue(SegmentLocationInfoContainer.getContainer().getLocationInfo().isEmpty());

    }
}
