package com.fr.swift.config.dao.impl;

import com.fr.swift.base.meta.SwiftMetaDataEntity;
import com.fr.swift.config.dao.SwiftMetaDataDao;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.easymock.PowerMock;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(SwiftMetaDataEntity.class), EasyMock.notNull(Serializable.class))).andReturn(new SwiftMetaDataEntity()).anyTimes();
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(SwiftMetaDataEntity.class), EasyMock.isNull(Serializable.class))).andThrow(new RuntimeException("Just Test Exception")).anyTimes();
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
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Collections.singletonList(new SwiftMetaDataEntity())).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SwiftMetaDataEntity.class))).andReturn(mockConfigQuery).anyTimes();
        PowerMock.replayAll();
        assertNotNull(mockSwiftMetaDataDaoImpl.findByTableName(mockConfigSession, ""));
        PowerMock.verifyAll();
    }

    @Test
    public void addOrUpdateSwiftMetaData() throws SQLException {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        SwiftMetaDataEntity mockEntity = PowerMock.createMock(SwiftMetaDataEntity.class);
        EasyMock.expect(mockConfigSession.merge(EasyMock.notNull(SwiftMetaDataEntity.class))).andReturn(mockEntity).anyTimes();
        EasyMock.expect(mockConfigSession.merge(null)).andThrow(new SQLException("Just Test Exception")).anyTimes();
        SwiftMetaDataEntity mockTestBean = PowerMock.createMock(SwiftMetaDataEntity.class);

        PowerMock.replayAll();

        mockSwiftMetaDataDaoImpl.addOrUpdateSwiftMetaData(mockConfigSession, new SwiftMetaDataEntity());
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
        SwiftMetaDataEntity mockEntity = PowerMock.createMock(SwiftMetaDataEntity.class);
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(SwiftMetaDataEntity.class), EasyMock.notNull(Serializable.class))).andReturn(mockEntity).anyTimes();
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(SwiftMetaDataEntity.class), EasyMock.isNull(Serializable.class))).andThrow(new RuntimeException("Just Test Exception")).anyTimes();
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
        SwiftMetaDataEntity mockEntity = PowerMock.createMock(SwiftMetaDataEntity.class);
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(mockEntity)).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SwiftMetaDataEntity.class))).andReturn(mockConfigQuery).anyTimes();
        PowerMock.replayAll();
        assertFalse(mockSwiftMetaDataDaoImpl.findAll(mockConfigSession).isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void testFuzzyFind() {
        ConfigSession configSession = Mockito.mock(ConfigSession.class);
        SwiftMetaDataEntity mockEntity = Mockito.mock(SwiftMetaDataEntity.class);
        ConfigQuery configQuery = Mockito.mock(ConfigQuery.class);
        Mockito.when(configSession.createEntityQuery(Mockito.any(Class.class))).thenReturn(configQuery);
        Mockito.when(configQuery.executeQuery()).thenReturn(Arrays.asList(mockEntity));

        List<SwiftMetaDataEntity> result = mockSwiftMetaDataDaoImpl.fuzzyFind(configSession, "a");
        assertFalse(result.isEmpty());
        Mockito.verify(configQuery).where(Mockito.any(ConfigWhere.class));
    }
}