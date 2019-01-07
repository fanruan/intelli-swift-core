package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.SegLocationBean;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.dao.impl.SwiftSegmentDaoImpl;
import com.fr.swift.config.dao.impl.SwiftSegmentLocationDaoImpl;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.impl.BaseTransactionManager;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2019-01-04
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SwiftContext.class)
public class SwiftSegmentServiceImplTest extends BaseServiceTest {

    private SwiftClusterSegmentService service;

    @Before
    public void setUp() throws Exception {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);


        // Generate by Mock Plugin
        final ConfigSession mockConfigSession = mockSession();

        BaseTransactionManager mockBaseTransactionManager = new BaseTransactionManager() {
            @Override
            protected ConfigSession createSession() {
                return mockConfigSession;
            }
        };
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(TransactionManager.class))).andReturn(mockBaseTransactionManager).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftSegmentLocationDao.class))).andReturn(new SwiftSegmentLocationDaoImpl()).anyTimes();
        // Generate by Mock Plugin
        SwiftMetaDataService mockSwiftMetaDataService = PowerMock.createMock(SwiftMetaDataService.class);
        EasyMock.expect(mockSwiftMetaDataService.containsMeta(EasyMock.notNull(SourceKey.class))).andReturn(true).anyTimes();
        // Generate by Mock Plugin
        SwiftMetaData mockSwiftMetaData = PowerMock.createMock(SwiftMetaData.class);
        EasyMock.expect(mockSwiftMetaData.getSwiftDatabase()).andReturn(SwiftDatabase.CUBE).anyTimes();
        EasyMock.expect(mockSwiftMetaDataService.getMetaDataByKey(EasyMock.notNull(String.class))).andReturn(mockSwiftMetaData).anyTimes();

        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftMetaDataService.class))).andReturn(mockSwiftMetaDataService).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftSegmentDao.class))).andReturn(new SwiftSegmentDaoImpl()).anyTimes();
        PowerMock.replay(mockSwiftContext, mockSwiftMetaDataService, mockSwiftMetaData);
        service = new SwiftSegmentServiceImpl();
    }

    @Test
    public void getAllSegments() {
        assertFalse(service.getAllSegments().isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void saveOrUpdate() {
        // Generate by Mock Plugin
        ObjectConverter tablePathA = PowerMock.createMock(ObjectConverter.class);
        SegmentKeyBean mockSwiftTablePathBean = PowerMock.createMock(SegmentKeyBean.class);
        EasyMock.expect(mockSwiftTablePathBean.convert()).andReturn(tablePathA).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean.getTable()).andReturn(new SourceKey("tableA")).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean.getId()).andReturn("tableA").anyTimes();

        EasyMock.expect(tablePathA.convert()).andReturn(mockSwiftTablePathBean).anyTimes();
        PowerMock.replayAll();
        assertTrue(service.saveOrUpdate(mockSwiftTablePathBean));

// do test
        PowerMock.verifyAll();

    }

    @Test
    public void addSegments() {
        List<SegmentKey> list = new ArrayList<SegmentKey>();
        ObjectConverter[] converters = mockData();
        for (ObjectConverter converter : converters) {
            list.add((SegmentKey) converter.convert());
        }
        assertTrue(service.addSegments(list));
        PowerMock.verifyAll();
    }

    @Test
    public void removeSegments() {
        assertTrue(service.removeSegments("tableA", "tableB"));
        PowerMock.verifyAll();
    }

    @Test
    public void removeSegments1() {
        List<SegmentKey> list = new ArrayList<SegmentKey>();
        ObjectConverter[] converters = mockData();
        for (ObjectConverter converter : converters) {
            list.add((SegmentKey) converter.convert());
        }
        assertTrue(service.removeSegments(list));
        PowerMock.verifyAll();
    }

    @Test
    public void updateSegments() {
        List<SegmentKey> list = new ArrayList<SegmentKey>();
        ObjectConverter[] converters = mockData();
        for (ObjectConverter converter : converters) {
            list.add((SegmentKey) converter.convert());
        }
        assertTrue(service.updateSegments("source", list));
        PowerMock.verifyAll();
    }

    @Test
    public void getSegmentByKey() {
        assertTrue(service.getSegmentByKey("sourceKey").isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void containsSegment() {
        ObjectConverter tablePathA = PowerMock.createMock(ObjectConverter.class);
        SegmentKeyBean mockSwiftTablePathBean = PowerMock.createMock(SegmentKeyBean.class);
        EasyMock.expect(mockSwiftTablePathBean.convert()).andReturn(tablePathA).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean.getTable()).andReturn(new SourceKey("tableA")).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean.getId()).andReturn("tableA").anyTimes();

        EasyMock.expect(tablePathA.convert()).andReturn(mockSwiftTablePathBean).anyTimes();
        PowerMock.replayAll();
        assertFalse(service.containsSegment(mockSwiftTablePathBean));
        PowerMock.verifyAll();
    }

    @Test
    public void tryAppendSegment() {
        assertNotNull(service.tryAppendSegment(new SourceKey("source"), Types.StoreType.MEMORY));
        PowerMock.verifyAll();
    }

    @Test
    public void getOwnSegments() {
        assertTrue(service.getOwnSegments().isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void updateSegmentTable() {
        Set<SegmentKey> list = new HashSet<SegmentKey>();
        ObjectConverter[] converters = mockData();
        for (ObjectConverter converter : converters) {
            list.add((SegmentKey) converter.convert());
        }
        Map<String, Set<SegmentKey>> segments = new HashMap<String, Set<SegmentKey>>();
        segments.put("source", list);
        assertTrue(service.updateSegmentTable(segments));
        PowerMock.verifyAll();
    }

    @Test
    public void find() {

        assertFalse(service.find().isEmpty());
        PowerMock.verifyAll();
    }

    private ObjectConverter[] mockData() {
        // Generate by Mock Plugin
        ObjectConverter tablePathA = PowerMock.createMock(ObjectConverter.class);
        // Generate by Mock Plugin
        SegmentKeyBean mockSwiftTablePathBean = PowerMock.createMock(SegmentKeyBean.class);
        EasyMock.expect(mockSwiftTablePathBean.convert()).andReturn(tablePathA).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean.getTable()).andReturn(new SourceKey("tableA")).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean.getId()).andReturn("tableA").anyTimes();
        EasyMock.expect(mockSwiftTablePathBean.getOrder()).andReturn(100).anyTimes();

        EasyMock.expect(tablePathA.convert()).andReturn(mockSwiftTablePathBean).anyTimes();
        ObjectConverter tablePathB = PowerMock.createMock(ObjectConverter.class);
        SegmentKeyBean mockSwiftTablePathBean1 = PowerMock.createMock(SegmentKeyBean.class);
        EasyMock.expect(mockSwiftTablePathBean1.convert()).andReturn(tablePathB).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean1.getTable()).andReturn(new SourceKey("tableB")).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean1.getId()).andReturn("tableB").anyTimes();
        EasyMock.expect(mockSwiftTablePathBean1.getOrder()).andReturn(101).anyTimes();
        EasyMock.expect(tablePathB.convert()).andReturn(mockSwiftTablePathBean1).anyTimes();
        PowerMock.replay(tablePathA, tablePathB, mockSwiftTablePathBean, mockSwiftTablePathBean1);
        return new ObjectConverter[]{
                tablePathA, tablePathB
        };
    }

    ConfigSession mockSession() throws SQLException {
        // Generate by Mock Plugin
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        mockConfigSession.delete(EasyMock.anyObject());
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                return null;
            }
        }).anyTimes();
        mockConfigSession.save(EasyMock.anyObject());
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                return null;
            }
        }).anyTimes();
        mockConfigSession.close();
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                return null;
            }
        }).anyTimes();
        EasyMock.expect(mockConfigSession.merge(EasyMock.notNull(SegmentKeyBean.TYPE))).andReturn(null).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SegmentKeyBean.TYPE))).andReturn(mockQuery(mockData())).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SegLocationBean.TYPE))).andReturn(mockQuery(mockSegLocationData())).anyTimes();
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(SegmentKeyBean.TYPE), EasyMock.notNull(Serializable.class))).andReturn(null).anyTimes();
        EasyMock.expect(mockConfigSession.beginTransaction()).andReturn(mockTransaction()).anyTimes();
        PowerMock.replay(mockConfigSession);
        return mockConfigSession;
    }

    private ObjectConverter[] mockSegLocationData() {
        // Generate by Mock Plugin
        ObjectConverter tablePathA = PowerMock.createMock(ObjectConverter.class);
        // Generate by Mock Plugin
        SegLocationBean mockSwiftTablePathBean = PowerMock.createMock(SegLocationBean.class);
        EasyMock.expect(mockSwiftTablePathBean.convert()).andReturn(tablePathA).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean.getSourceKey()).andReturn("sourceKeyA").anyTimes();
        EasyMock.expect(mockSwiftTablePathBean.getSegmentId()).andReturn("segId").anyTimes();

        EasyMock.expect(tablePathA.convert()).andReturn(mockSwiftTablePathBean).anyTimes();
        ObjectConverter tablePathB = PowerMock.createMock(ObjectConverter.class);
        SegLocationBean mockSwiftTablePathBean1 = PowerMock.createMock(SegLocationBean.class);
        EasyMock.expect(mockSwiftTablePathBean1.convert()).andReturn(tablePathB).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean1.getSourceKey()).andReturn("sourceKeyB").anyTimes();
        EasyMock.expect(mockSwiftTablePathBean1.getSegmentId()).andReturn("segId1").anyTimes();
        EasyMock.expect(tablePathB.convert()).andReturn(mockSwiftTablePathBean1).anyTimes();
        PowerMock.replay(tablePathA, tablePathB, mockSwiftTablePathBean, mockSwiftTablePathBean1);
        return new ObjectConverter[]{
                tablePathA, tablePathB
        };
    }
}