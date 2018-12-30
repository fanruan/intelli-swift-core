package com.fr.swift.config.dao.impl;

import com.fr.swift.config.bean.SegLocationBean;
import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.oper.ConfigCriteria;
import com.fr.swift.config.oper.ConfigSession;
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
        swiftSegmentLocationDao = PowerMock.createMock(SwiftSegmentLocationDaoImpl.class);
    }

    @Test
    public void deleteBySourceKey() throws SQLException {
        SwiftSegmentLocationDao mockSwiftMetaDataDaoImpl = PowerMock.createMock(SwiftSegmentLocationDaoImpl.class);
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ConfigCriteria mockConfigCriteria = PowerMock.createMock(ConfigCriteria.class);
        final SegLocationBean segmentKey = new SegLocationBean("clusterId", "segmentKey", "sourceKey");

        EasyMock.expect(mockConfigCriteria.list()).andReturn(Arrays.asList(segmentKey.convert())).anyTimes();
        mockConfigCriteria.add(EasyMock.notNull());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigSession.createCriteria(EasyMock.eq(SegLocationBean.TYPE))).andReturn(mockConfigCriteria).anyTimes();
        mockConfigSession.delete(EasyMock.anyObject(SegLocationBean.TYPE));
        EasyMock.expectLastCall().anyTimes();
        PowerMock.replayAll();
        assertTrue(mockSwiftMetaDataDaoImpl.deleteBySourceKey(mockConfigSession, "sourceKey"));
        boolean exception = false;
        try {
            mockSwiftMetaDataDaoImpl.deleteBySourceKey(mockConfigSession, null);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void findByClusterId() {
        SwiftSegmentLocationDao mockSwiftMetaDataDaoImpl = PowerMock.createMock(SwiftSegmentLocationDaoImpl.class);
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ConfigCriteria mockConfigCriteria = PowerMock.createMock(ConfigCriteria.class);
        final SegLocationBean segmentKey = new SegLocationBean("clusterId", "segmentKey", "sourceKey");
        EasyMock.expect(mockConfigCriteria.list()).andReturn(Arrays.asList(segmentKey.convert())).anyTimes();
        mockConfigCriteria.add(EasyMock.notNull());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigSession.createCriteria(EasyMock.eq(SegLocationBean.TYPE))).andReturn(mockConfigCriteria).anyTimes();
        PowerMock.replayAll();
        assertFalse(mockSwiftMetaDataDaoImpl.findByClusterId(mockConfigSession, "clusterId").list().isEmpty());
        boolean exception = false;
        try {
            mockSwiftMetaDataDaoImpl.findByClusterId(mockConfigSession, null);
        } catch (Exception e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void findBySegmentId() {
        SwiftSegmentLocationDao mockSwiftMetaDataDaoImpl = PowerMock.createMock(SwiftSegmentLocationDaoImpl.class);
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ConfigCriteria mockConfigCriteria = PowerMock.createMock(ConfigCriteria.class);
        final SegLocationBean segmentKey = new SegLocationBean("clusterId", "segmentKey", "sourceKey");
        EasyMock.expect(mockConfigCriteria.list()).andReturn(Arrays.asList(segmentKey.convert())).anyTimes();
        mockConfigCriteria.add(EasyMock.notNull());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigSession.createCriteria(EasyMock.eq(SegLocationBean.TYPE))).andReturn(mockConfigCriteria).anyTimes();
        PowerMock.replayAll();
        assertFalse(mockSwiftMetaDataDaoImpl.findBySegmentId(mockConfigSession, "segmentId").list().isEmpty());
        boolean exception = false;
        try {
            mockSwiftMetaDataDaoImpl.findBySegmentId(mockConfigSession, null);
        } catch (Exception e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void findAll() {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ConfigCriteria mockConfigCriteria = PowerMock.createMock(ConfigCriteria.class);
        ObjectConverter<SegLocationBean> mockEntity = (ObjectConverter<SegLocationBean>) PowerMock.createMock(SegLocationBean.TYPE);
        EasyMock.expect(mockEntity.convert()).andReturn(new SegLocationBean()).anyTimes();
        EasyMock.expect(mockConfigSession.createCriteria(EasyMock.eq(SegLocationBean.TYPE))).andReturn(mockConfigCriteria).anyTimes();
        EasyMock.expect(mockConfigCriteria.list()).andReturn(Arrays.<Object>asList(mockEntity)).anyTimes();
        PowerMock.replayAll();
        assertFalse(swiftSegmentLocationDao.findAll(mockConfigSession).list().isEmpty());
        PowerMock.verifyAll();
    }
}