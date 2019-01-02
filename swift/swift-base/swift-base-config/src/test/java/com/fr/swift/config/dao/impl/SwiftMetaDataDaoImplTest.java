package com.fr.swift.config.dao.impl;

import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.dao.SwiftMetaDataDao;
import com.fr.swift.config.oper.ConfigCriteria;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.converter.ObjectConverter;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
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
        mockSwiftMetaDataDaoImpl = PowerMock.createMock(SwiftMetaDataDaoImpl.class);
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
        SwiftMetaDataDaoImpl mockSwiftMetaDataDaoImpl = PowerMock.createMock(SwiftMetaDataDaoImpl.class);
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ConfigCriteria mockConfigCriteria = PowerMock.createMock(ConfigCriteria.class);
        EasyMock.expect(mockConfigSession.createCriteria(EasyMock.eq(SwiftMetaDataBean.TYPE))).andReturn(mockConfigCriteria).anyTimes();
        mockConfigCriteria.add(EasyMock.anyObject());
        mockConfigCriteria.add(EasyMock.anyObject());
        mockConfigCriteria.add(EasyMock.anyObject());
        EasyMock.expect(mockConfigCriteria.list()).andReturn(new ArrayList<Object>()).once();
        EasyMock.expect(mockConfigCriteria.list()).andReturn(Arrays.asList(SwiftMetaDataBean.TYPE.newInstance(), SwiftMetaDataBean.TYPE.newInstance())).once();
        EasyMock.expect(mockConfigCriteria.list()).andReturn(Arrays.asList(SwiftMetaDataBean.TYPE.newInstance())).once();
        PowerMock.replayAll();
        boolean exception = false;
        try {
            mockSwiftMetaDataDaoImpl.findByTableName(mockConfigSession, "");
        } catch (Exception e) {
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        try {
            mockSwiftMetaDataDaoImpl.findByTableName(mockConfigSession, "");
        } catch (Exception e) {
            exception = true;
        }
        assertTrue(exception);
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
        ConfigCriteria mockConfigCriteria = PowerMock.createMock(ConfigCriteria.class);
        ObjectConverter<SwiftMetaDataBean> mockEntity = (ObjectConverter<SwiftMetaDataBean>) PowerMock.createMock(SwiftMetaDataBean.TYPE);
        EasyMock.expect(mockEntity.convert()).andReturn(new SwiftMetaDataBean()).anyTimes();
        EasyMock.expect(mockConfigSession.createCriteria(EasyMock.eq(SwiftMetaDataBean.TYPE))).andReturn(mockConfigCriteria).anyTimes();
        EasyMock.expect(mockConfigCriteria.list()).andReturn(Arrays.<Object>asList(mockEntity)).anyTimes();
        PowerMock.replayAll();
        assertFalse(mockSwiftMetaDataDaoImpl.findAll(mockConfigSession).list().isEmpty());
        PowerMock.verifyAll();
    }
}