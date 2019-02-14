package com.fr.swift.config.dao.impl;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
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
public class SwiftSegmentDaoImplTest {

    private SwiftSegmentDao dao;

    @Before
    public void before() {
        dao = new SwiftSegmentDaoImpl();
    }

    @Test
    public void addOrUpdateSwiftSegment() throws SQLException {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ObjectConverter<SegmentKeyBean> mockEntity = (ObjectConverter<SegmentKeyBean>) PowerMock.createMock(SegmentKeyBean.TYPE);
        EasyMock.expect(mockEntity.convert()).andReturn(new SegmentKeyBean()).anyTimes();
        EasyMock.expect(mockConfigSession.merge(EasyMock.notNull(SegmentKeyBean.TYPE))).andReturn(mockEntity).anyTimes();
        EasyMock.expect(mockConfigSession.merge(null)).andThrow(new SQLException("Just Test Exception")).anyTimes();
        SegmentKeyBean testException = PowerMock.createMock(SegmentKeyBean.class);
        EasyMock.expect(testException.convert()).andReturn(null).anyTimes();
        EasyMock.expect(testException.getStoreType()).andReturn(Types.StoreType.MEMORY).anyTimes();
        EasyMock.expect(testException.getSwiftSchema()).andReturn(SwiftDatabase.CUBE).anyTimes();
        EasyMock.expect(testException.getId()).andReturn("keyId").anyTimes();
        EasyMock.expect(testException.getOrder()).andReturn(0).anyTimes();
        EasyMock.expect(testException.getTable()).andReturn(new SourceKey("table")).anyTimes();

        SegmentKey mockSegmentKey = PowerMock.createMock(SegmentKey.class);
        EasyMock.expect(mockSegmentKey.getStoreType()).andReturn(Types.StoreType.MEMORY).anyTimes();
        EasyMock.expect(mockSegmentKey.getSwiftSchema()).andReturn(SwiftDatabase.CUBE).anyTimes();
        EasyMock.expect(mockSegmentKey.getId()).andReturn("keyId").anyTimes();
        EasyMock.expect(mockSegmentKey.getOrder()).andReturn(0).anyTimes();
        EasyMock.expect(mockSegmentKey.getTable()).andReturn(new SourceKey("table")).anyTimes();
        EasyMock.expect(mockSegmentKey.convert()).andReturn(mockEntity).anyTimes();

        PowerMock.replayAll();

        dao.addOrUpdateSwiftSegment(mockConfigSession, mockSegmentKey);
        boolean exception = false;
        try {
            dao.addOrUpdateSwiftSegment(mockConfigSession, testException);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void findBeanByStoreType() throws Exception {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        final SegmentKey segmentKey = new SegmentKeyBean("sourceKey", 0, Types.StoreType.MEMORY, SwiftDatabase.CUBE);
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class), EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(segmentKey.convert())).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SegmentKeyBean.TYPE))).andReturn(mockConfigQuery).anyTimes();
        PowerMock.replayAll();
        assertFalse(dao.findBeanByStoreType(mockConfigSession, "sourceKey", Types.StoreType.MEMORY).isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void deleteBySourceKey() throws SQLException {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        final SegmentKey segmentKey = new SegmentKeyBean("sourceKey", 0, Types.StoreType.MEMORY, SwiftDatabase.CUBE);
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(segmentKey.convert())).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SegmentKeyBean.TYPE))).andReturn(mockConfigQuery).anyTimes();
        mockConfigSession.delete(EasyMock.anyObject(SegmentKeyBean.TYPE));
        EasyMock.expectLastCall().anyTimes();
        PowerMock.replayAll();
        assertTrue(dao.deleteBySourceKey(mockConfigSession, "sourceKey"));
        PowerMock.verifyAll();
    }

    @Test
    public void findAll() {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ObjectConverter<SegmentKeyBean> mockEntity = (ObjectConverter<SegmentKeyBean>) PowerMock.createMock(SegmentKeyBean.TYPE);
        EasyMock.expect(mockEntity.convert()).andReturn(new SegmentKeyBean()).anyTimes();
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(mockEntity)).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SegmentKeyBean.TYPE))).andReturn(mockConfigQuery).anyTimes();
        PowerMock.replayAll();
        assertFalse(dao.findAll(mockConfigSession).list().isEmpty());
        PowerMock.verifyAll();
    }
}