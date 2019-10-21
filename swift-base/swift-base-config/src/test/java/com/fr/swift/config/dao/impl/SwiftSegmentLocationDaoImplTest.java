package com.fr.swift.config.dao.impl;

import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018-11-29
 */
public class SwiftSegmentLocationDaoImplTest {

    private SwiftSegmentLocationDao swiftSegmentLocationDao;

    @Before
    public void before() {
        swiftSegmentLocationDao = new SwiftSegmentLocationDaoImpl();
    }

    @Test
    public void deleteBySourceKey() throws SQLException {
        final SwiftSegmentLocationEntity segmentKey = new SwiftSegmentLocationEntity("clusterId", "segmentKey", "sourceKey");
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        // Generate by Mock Plugin
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Collections.singletonList(segmentKey)).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SwiftSegmentLocationEntity.class))).andReturn(mockConfigQuery).anyTimes();
        mockConfigSession.delete(EasyMock.anyObject(SwiftSegmentLocationEntity.class));
        EasyMock.expectLastCall().anyTimes();
        PowerMock.replayAll();
        assertTrue(swiftSegmentLocationDao.deleteBySourceKey(mockConfigSession, "sourceKey"));
        PowerMock.verifyAll();
    }

    @Test
    public void findByClusterId() {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        final SwiftSegmentLocationEntity segmentKey = new SwiftSegmentLocationEntity("clusterId", "segmentKey", "sourceKey");
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(segmentKey)).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SwiftSegmentLocationEntity.class))).andReturn(mockConfigQuery).anyTimes();
        PowerMock.replayAll();
        assertFalse(swiftSegmentLocationDao.findByClusterId(mockConfigSession, "clusterId").isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void findBySegmentId() {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        final SwiftSegmentLocationEntity segmentKey = new SwiftSegmentLocationEntity("clusterId", "segmentKey", "sourceKey");
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(segmentKey)).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SwiftSegmentLocationEntity.class))).andReturn(mockConfigQuery).anyTimes();
        PowerMock.replayAll();
        assertFalse(swiftSegmentLocationDao.findBySegmentId(mockConfigSession, "segmentId").isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void findAll() {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        SwiftSegmentLocationEntity mockEntity = PowerMock.createMock(SwiftSegmentLocationEntity.class);
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(mockEntity)).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SwiftSegmentLocationEntity.class))).andReturn(mockConfigQuery).anyTimes();
        PowerMock.replayAll();
        assertFalse(swiftSegmentLocationDao.findAll(mockConfigSession).isEmpty());
        PowerMock.verifyAll();
    }
}