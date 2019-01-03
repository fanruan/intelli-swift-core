package com.fr.swift.config.dao.impl;

import com.fr.swift.config.bean.SegLocationBean;
import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.converter.ObjectConverter;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.sql.SQLException;
import java.util.Arrays;

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
        final SegLocationBean segmentKey = new SegLocationBean("clusterId", "segmentKey", "sourceKey");
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        // Generate by Mock Plugin
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(segmentKey.convert())).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SegLocationBean.TYPE))).andReturn(mockConfigQuery).anyTimes();
        mockConfigSession.delete(EasyMock.anyObject(SegLocationBean.TYPE));
        EasyMock.expectLastCall().anyTimes();
        PowerMock.replayAll();
        assertTrue(swiftSegmentLocationDao.deleteBySourceKey(mockConfigSession, "sourceKey"));
        PowerMock.verifyAll();
    }

    @Test
    public void findByClusterId() {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        final SegLocationBean segmentKey = new SegLocationBean("clusterId", "segmentKey", "sourceKey");
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(segmentKey.convert())).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SegLocationBean.TYPE))).andReturn(mockConfigQuery).anyTimes();
        PowerMock.replayAll();
        assertFalse(swiftSegmentLocationDao.findByClusterId(mockConfigSession, "clusterId").list().isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void findBySegmentId() {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        final SegLocationBean segmentKey = new SegLocationBean("clusterId", "segmentKey", "sourceKey");
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(segmentKey.convert())).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SegLocationBean.TYPE))).andReturn(mockConfigQuery).anyTimes();
        PowerMock.replayAll();
        assertFalse(swiftSegmentLocationDao.findBySegmentId(mockConfigSession, "segmentId").list().isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void findAll() {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ObjectConverter<SegLocationBean> mockEntity = (ObjectConverter<SegLocationBean>) PowerMock.createMock(SegLocationBean.TYPE);
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(mockEntity)).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SegLocationBean.TYPE))).andReturn(mockConfigQuery).anyTimes();
        EasyMock.expect(mockEntity.convert()).andReturn(new SegLocationBean()).anyTimes();
        PowerMock.replayAll();
        assertFalse(swiftSegmentLocationDao.findAll(mockConfigSession).list().isEmpty());
        PowerMock.verifyAll();
    }
}