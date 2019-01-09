package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.source.SourceKey;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2019-01-09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {SwiftContext.class, SwiftRepositoryManager.class})
public class SegmentHelperTest {

    @Before
    public void setUp() throws Exception {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftRepositoryManager.class);
        SwiftRepositoryManager mockSwiftRepositoryManager = PowerMock.createMock(SwiftRepositoryManager.class);
        SwiftRepository mockSwiftRepository = PowerMock.createMock(SwiftRepository.class);
        EasyMock.expect(mockSwiftRepository.copyFromRemote(EasyMock.anyString(), EasyMock.anyString())).andReturn(null).anyTimes();
        EasyMock.expect(mockSwiftRepository.copyToRemote(EasyMock.anyString(), EasyMock.anyString())).andReturn(false).anyTimes();
        EasyMock.expect(mockSwiftRepository.zipToRemote(EasyMock.anyString(), EasyMock.anyString())).andReturn(false).anyTimes();
        EasyMock.expect(mockSwiftRepository.exists(EasyMock.anyString())).andReturn(true).anyTimes();
        EasyMock.expect(mockSwiftRepositoryManager.currentRepo()).andReturn(mockSwiftRepository).anyTimes();
        EasyMock.expect(SwiftRepositoryManager.getManager()).andReturn(mockSwiftRepositoryManager).anyTimes();
        PowerMock.replay(SwiftRepositoryManager.class, mockSwiftRepositoryManager);
    }

    @Test
    public void checkSegmentExists() {
        // Generate by Mock Plugin
        SwiftSegmentService mockSwiftSegmentService = PowerMock.createMock(SwiftSegmentService.class);
        Map<SourceKey, List<SegmentKey>> keys = new HashMap<SourceKey, List<SegmentKey>>();
        keys.put(new SourceKey("table"), Arrays.<SegmentKey>asList(new SegmentKeyBean(new SourceKey("table"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE),
                new SegmentKeyBean(new SourceKey("table"), 1, Types.StoreType.FINE_IO, SwiftDatabase.CUBE)));
        EasyMock.expect(mockSwiftSegmentService.getOwnSegments()).andReturn(keys).anyTimes();
        EasyMock.expect(mockSwiftSegmentService.removeSegments(EasyMock.anyObject(List.class))).andReturn(true).anyTimes();

        // Generate by Mock Plugin
        SwiftSegmentManager mockSwiftSegmentManager = PowerMock.createMock(SwiftSegmentManager.class);
        // Generate by Mock Plugin
        Segment mockSegment = PowerMock.createMock(Segment.class);
        EasyMock.expect(mockSegment.isReadable()).andReturn(false).once();
        EasyMock.expect(mockSegment.isReadable()).andReturn(true).once();

        EasyMock.expect(mockSwiftSegmentManager.getSegment(EasyMock.anyObject(SegmentKey.class))).andReturn(mockSegment).anyTimes();

        // Generate by Mock Plugin
        SwiftCubePathService mockSwiftCubePathService = PowerMock.createMock(SwiftCubePathService.class);
        EasyMock.expect(mockSwiftCubePathService.getSwiftPath()).andReturn(System.getProperty("user.dir")).anyTimes();


        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftCubePathService.class))).andReturn(mockSwiftCubePathService).anyTimes();
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);
        PowerMock.replayAll();

        SegmentHelper.checkSegmentExists(mockSwiftSegmentService, mockSwiftSegmentManager);

        PowerMock.verifyAll();
    }

    @Test
    public void download() {
    }

    @Test
    public void uploadTable() {
    }

    @Test
    public void uploadRelation() {
    }
}