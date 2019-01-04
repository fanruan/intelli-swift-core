package com.fr.swift.config.dao.impl;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.config.dao.SwiftMetaDataDao;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.converter.ObjectConverter;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018-11-29
 */
public class SwiftMetaDataDaoImplTest {

    private SwiftMetaDataDao mockSwiftMetaDataDaoImpl;

    @Before
    public void before() {
        mockSwiftMetaDataDaoImpl = new SwiftMetaDataDaoImpl();
    }

    @Test
    public void findBySourceKey() throws Exception {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(SwiftMetaDataBean.TYPE), EasyMock.notNull(Serializable.class))).andReturn(SwiftMetaDataBean.TYPE.newInstance()).anyTimes();
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(SwiftMetaDataBean.TYPE), EasyMock.isNull(Serializable.class))).andThrow(new RuntimeException("Just Test Exception")).anyTimes();
        PowerMock.replayAll();
        mockSwiftMetaDataDaoImpl.findBySourceKey(mockConfigSession, "");
        boolean exception = false;
        try {
            mockSwiftMetaDataDaoImpl.findBySourceKey(mockConfigSession, null);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void findByTableName() throws Exception {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(SwiftMetaDataBean.TYPE.newInstance())).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SwiftMetaDataBean.TYPE))).andReturn(mockConfigQuery).anyTimes();
        PowerMock.replayAll();
        assertNotNull(mockSwiftMetaDataDaoImpl.findByTableName(mockConfigSession, ""));
        PowerMock.verifyAll();
    }

    @Test
    public void addOrUpdateSwiftMetaData() throws SQLException {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ObjectConverter<SwiftMetaDataBean> mockEntity = (ObjectConverter<SwiftMetaDataBean>) PowerMock.createMock(SwiftMetaDataBean.TYPE);
        EasyMock.expect(mockEntity.convert()).andReturn(new SwiftMetaDataBean()).anyTimes();
        EasyMock.expect(mockConfigSession.merge(EasyMock.notNull(SwiftMetaDataBean.TYPE))).andReturn(mockEntity).anyTimes();
        EasyMock.expect(mockConfigSession.merge(null)).andThrow(new SQLException("Just Test Exception")).anyTimes();
        SwiftMetaDataBean mockTestBean = PowerMock.createMock(SwiftMetaDataBean.class);
        EasyMock.expect(mockTestBean.convert()).andReturn(null).anyTimes();

        PowerMock.replayAll();

        mockSwiftMetaDataDaoImpl.addOrUpdateSwiftMetaData(mockConfigSession, new SwiftMetaDataBean());
        boolean exception = false;
        try {
            mockSwiftMetaDataDaoImpl.addOrUpdateSwiftMetaData(mockConfigSession, mockTestBean);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void deleteSwiftMetaDataBean() throws SQLException {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ObjectConverter<SwiftMetaDataBean> mockEntity = (ObjectConverter<SwiftMetaDataBean>) PowerMock.createMock(SwiftMetaDataBean.TYPE);
        EasyMock.expect(mockEntity.convert()).andReturn(new SwiftMetaDataBean()).anyTimes();
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(SwiftMetaDataBean.TYPE), EasyMock.notNull(Serializable.class))).andReturn(mockEntity).anyTimes();
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(SwiftMetaDataBean.TYPE), EasyMock.isNull(Serializable.class))).andThrow(new RuntimeException("Just Test Exception")).anyTimes();
        mockConfigSession.delete(EasyMock.anyObject());
        PowerMock.replayAll();
        assertTrue(mockSwiftMetaDataDaoImpl.deleteSwiftMetaDataBean(mockConfigSession, ""));
        boolean exception = false;
        try {
            mockSwiftMetaDataDaoImpl.deleteSwiftMetaDataBean(mockConfigSession, null);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void findAll() {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ObjectConverter<SwiftMetaDataBean> mockEntity = (ObjectConverter<SwiftMetaDataBean>) PowerMock.createMock(SwiftMetaDataBean.TYPE);
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(mockEntity)).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SwiftMetaDataBean.TYPE))).andReturn(mockConfigQuery).anyTimes();
        EasyMock.expect(mockEntity.convert()).andReturn(new SwiftMetaDataBean()).anyTimes();
        PowerMock.replayAll();
        assertFalse(mockSwiftMetaDataDaoImpl.findAll(mockConfigSession).list().isEmpty());
        PowerMock.verifyAll();
    }
}