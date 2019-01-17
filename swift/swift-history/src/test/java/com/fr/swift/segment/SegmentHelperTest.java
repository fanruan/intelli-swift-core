package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.bean.SwiftTablePathBean;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2019-01-09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {SwiftContext.class, SwiftRepositoryManager.class, ProxySelector.class})
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
        EasyMock.expect(mockSwiftRepository.delete(EasyMock.anyString())).andReturn(true).anyTimes();
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
        // Generate by Mock Plugin
        SwiftCubePathService mockSwiftCubePathService = PowerMock.createMock(SwiftCubePathService.class);
        EasyMock.expect(mockSwiftCubePathService.getSwiftPath()).andReturn(System.getProperty("user.dir")).anyTimes();

        // Generate by Mock Plugin
        SwiftTablePathService mockSwiftTablePathService = PowerMock.createMock(SwiftTablePathService.class);
        EasyMock.expect(mockSwiftTablePathService.get(EasyMock.anyObject(String.class))).andReturn(null).once();
        SwiftTablePathBean bean = new SwiftTablePathBean();
        bean.setTmpDir(0);
        bean.setTablePath(0);
        bean.setLastPath(0);
        EasyMock.expect(mockSwiftTablePathService.get(EasyMock.anyObject(String.class))).andReturn(bean).times(3);
        EasyMock.expect(mockSwiftTablePathService.saveOrUpdate(EasyMock.anyObject(SwiftTablePathBean.class))).andReturn(true).anyTimes();

        // Generate by Mock Plugin
        SwiftMetaDataService mockSwiftMetaDataService = PowerMock.createMock(SwiftMetaDataService.class);
        SwiftMetaData mockSwiftMetaData = PowerMock.createMock(SwiftMetaData.class);
        EasyMock.expect(mockSwiftMetaData.getSwiftDatabase()).andReturn(SwiftDatabase.CUBE).anyTimes();
        EasyMock.expect(mockSwiftMetaDataService.getMetaDataByKey(EasyMock.anyString())).andReturn(mockSwiftMetaData).anyTimes();

        // Generate by Mock Plugin
        SwiftSegmentManager mockSwiftSegmentManager = PowerMock.createMock(SwiftSegmentManager.class);
        EasyMock.expect(mockSwiftSegmentManager.getSegment(EasyMock.anyObject(SourceKey.class))).andReturn(null).anyTimes();

        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftCubePathService.class))).andReturn(mockSwiftCubePathService).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftTablePathService.class))).andReturn(mockSwiftTablePathService).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftMetaDataService.class))).andReturn(mockSwiftMetaDataService).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq("localSegmentProvider"), EasyMock.eq(SwiftSegmentManager.class))).andReturn(mockSwiftSegmentManager).anyTimes();
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);
        PowerMock.replayAll();
        SegmentHelper.download("table", Collections.singleton("seg0"), false);
        SegmentHelper.download("table", Collections.singleton("seg0"), true);

        PowerMock.verifyAll();
    }

    @Test
    public void uploadTable() throws Exception {
        // Generate by Mock Plugin
        SwiftCubePathService mockSwiftCubePathService = PowerMock.createMock(SwiftCubePathService.class);
        EasyMock.expect(mockSwiftCubePathService.getSwiftPath()).andReturn(System.getProperty("user.dir")).anyTimes();

        // Generate by Mock Plugin
        SwiftTablePathService mockSwiftTablePathService = PowerMock.createMock(SwiftTablePathService.class);
        SwiftTablePathBean bean = new SwiftTablePathBean();
        bean.setTmpDir(0);
        bean.setTablePath(0);
        bean.setLastPath(0);
        EasyMock.expect(mockSwiftTablePathService.get(EasyMock.anyObject(String.class))).andReturn(bean).anyTimes();
        EasyMock.expect(mockSwiftTablePathService.saveOrUpdate(EasyMock.anyObject(SwiftTablePathBean.class))).andReturn(true).anyTimes();

        // Generate by Mock Plugin
        SwiftSegmentLocationService mockSwiftMetaDataService = PowerMock.createMock(SwiftSegmentLocationService.class);
        EasyMock.expect(mockSwiftMetaDataService.delete(EasyMock.anyString(), EasyMock.anyString())).andReturn(true).anyTimes();

        // Generate by Mock Plugin
        SwiftSegmentService mockSwiftSegmentService = PowerMock.createMock(SwiftSegmentService.class);
        EasyMock.expect(mockSwiftSegmentService.getSegmentByKey(EasyMock.anyString()))
                .andReturn(
                        Arrays.<SegmentKey>asList(new SegmentKeyBean(new SourceKey("table"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE))
                ).anyTimes();

        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftCubePathService.class))).andReturn(mockSwiftCubePathService).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftTablePathService.class))).andReturn(mockSwiftTablePathService).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftSegmentLocationService.class))).andReturn(mockSwiftMetaDataService).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq("segmentServiceProvider"), EasyMock.eq(SwiftSegmentService.class))).andReturn(mockSwiftSegmentService).anyTimes();
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();


        // Generate by Mock Plugin
        DataSource mockDataSource = PowerMock.createMock(DataSource.class);
        SwiftMetaData mockSwiftMetaData = PowerMock.createMock(SwiftMetaData.class);
        EasyMock.expect(mockSwiftMetaData.getSwiftDatabase()).andReturn(SwiftDatabase.CUBE).anyTimes();
        EasyMock.expect(mockDataSource.getMetadata()).andReturn(mockSwiftMetaData).anyTimes();
        EasyMock.expect(mockDataSource.getSourceKey()).andReturn(new SourceKey("table")).anyTimes();


        // Generate by Mock Plugin
        SwiftSegmentManager mockSwiftSegmentManager = PowerMock.createMock(SwiftSegmentManager.class);
        mockSwiftSegmentManager.remove(EasyMock.anyObject(SourceKey.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockSwiftSegmentManager.getSegment(EasyMock.anyObject(SourceKey.class))).andReturn(null).anyTimes();


        // Generate by Mock Plugin
        PowerMock.mockStatic(ProxySelector.class);
        ProxySelector mockProxySelector = PowerMock.createMock(ProxySelector.class);
        ProxyFactory mockProxyFactory = PowerMock.createMock(ProxyFactory.class);

        // Generate by Mock Plugin
        RemoteSender mockRemoteSender = PowerMock.createMock(RemoteSender.class);
        EasyMock.expect(mockRemoteSender.trigger(EasyMock.anyObject(SwiftRpcEvent.class))).andReturn(null).anyTimes();

        EasyMock.expect(mockProxyFactory.getProxy(EasyMock.eq(RemoteSender.class))).andReturn(mockRemoteSender).anyTimes();
        EasyMock.expect(mockProxySelector.getFactory()).andReturn(mockProxyFactory).anyTimes();
        EasyMock.expect(ProxySelector.getInstance()).andReturn(mockProxySelector).anyTimes();


        PowerMock.replay(SwiftContext.class, ProxySelector.class);
        PowerMock.replayAll();

        SegmentHelper.uploadTable(mockSwiftSegmentManager, mockDataSource, "LOCAL");

        PowerMock.verifyAll();
    }

    @Test
    public void uploadRelation() {
        // Generate by Mock Plugin
        SwiftCubePathService mockSwiftCubePathService = PowerMock.createMock(SwiftCubePathService.class);
        EasyMock.expect(mockSwiftCubePathService.getSwiftPath()).andReturn(System.getProperty("user.dir")).anyTimes();

        // Generate by Mock Plugin
        SwiftTablePathService mockSwiftTablePathService = PowerMock.createMock(SwiftTablePathService.class);
        SwiftTablePathBean bean = new SwiftTablePathBean();
        bean.setTmpDir(0);
        bean.setTablePath(0);
        bean.setLastPath(0);
        EasyMock.expect(mockSwiftTablePathService.get(EasyMock.anyObject(String.class))).andReturn(bean).anyTimes();
        EasyMock.expect(mockSwiftTablePathService.saveOrUpdate(EasyMock.anyObject(SwiftTablePathBean.class))).andReturn(true).anyTimes();

        // Generate by Mock Plugin
        SwiftSegmentLocationService mockSwiftMetaDataService = PowerMock.createMock(SwiftSegmentLocationService.class);
        EasyMock.expect(mockSwiftMetaDataService.delete(EasyMock.anyString(), EasyMock.anyString())).andReturn(true).anyTimes();

        // Generate by Mock Plugin
        SwiftSegmentService mockSwiftSegmentService = PowerMock.createMock(SwiftSegmentService.class);
        EasyMock.expect(mockSwiftSegmentService.getSegmentByKey(EasyMock.anyString()))
                .andReturn(
                        Arrays.<SegmentKey>asList(new SegmentKeyBean(new SourceKey("table"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE))
                ).anyTimes();

        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftCubePathService.class))).andReturn(mockSwiftCubePathService).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftTablePathService.class))).andReturn(mockSwiftTablePathService).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftSegmentLocationService.class))).andReturn(mockSwiftMetaDataService).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq("segmentServiceProvider"), EasyMock.eq(SwiftSegmentService.class))).andReturn(mockSwiftSegmentService).anyTimes();
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();


        // Generate by Mock Plugin
        PowerMock.mockStatic(ProxySelector.class);
        ProxySelector mockProxySelector = PowerMock.createMock(ProxySelector.class);
        ProxyFactory mockProxyFactory = PowerMock.createMock(ProxyFactory.class);

        // Generate by Mock Plugin
        RemoteSender mockRemoteSender = PowerMock.createMock(RemoteSender.class);
        EasyMock.expect(mockRemoteSender.trigger(EasyMock.anyObject(SwiftRpcEvent.class))).andReturn(null).anyTimes();

        EasyMock.expect(mockProxyFactory.getProxy(EasyMock.eq(RemoteSender.class))).andReturn(mockRemoteSender).anyTimes();
        EasyMock.expect(mockProxySelector.getFactory()).andReturn(mockProxyFactory).anyTimes();
        EasyMock.expect(ProxySelector.getInstance()).andReturn(mockProxySelector).anyTimes();


        PowerMock.replay(SwiftContext.class, ProxySelector.class);


        // Generate by Mock Plugin
        RelationSource mockRelationSource = PowerMock.createMock(RelationSource.class);
        EasyMock.expect(mockRelationSource.getForeignSource()).andReturn(new SourceKey("to")).anyTimes();
        EasyMock.expect(mockRelationSource.getPrimarySource()).andReturn(new SourceKey("from")).anyTimes();
        EasyMock.expect(mockRelationSource.getRelationType()).andReturn(RelationSourceType.FIELD_RELATION).once();
        EasyMock.expect(mockRelationSource.getRelationType()).andReturn(RelationSourceType.RELATION).once();


        PowerMock.replayAll();

        SegmentHelper.uploadRelation(mockRelationSource, "LOCAL");
        SegmentHelper.uploadRelation(mockRelationSource, "LOCAL");

        PowerMock.verifyAll();
    }
}