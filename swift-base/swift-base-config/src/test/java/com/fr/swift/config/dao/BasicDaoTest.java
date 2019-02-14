package com.fr.swift.config.dao;

import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Order;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.oper.impl.OrderImpl;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018-11-28
 */
public class BasicDaoTest {

    private BasicDao mockBasicDao;

    @Before
    public void before() {
        mockBasicDao = new BasicDao(TestEntity.class);
    }

    @Test
    public void saveOrUpdate() throws SQLException {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        EasyMock.expect(mockConfigSession.merge(EasyMock.notNull(TestEntity.class))).andReturn(new TestEntity()).anyTimes();
        EasyMock.expect(mockConfigSession.merge(null)).andThrow(new SQLException("Just Test Exception")).anyTimes();
        TestBean mockTestBean = PowerMock.createMock(TestBean.class);
        EasyMock.expect(mockTestBean.convert()).andReturn(null).anyTimes();

        PowerMock.replayAll();

        mockBasicDao.saveOrUpdate(mockConfigSession, new TestBean());
        boolean exception = false;
        try {
            mockBasicDao.saveOrUpdate(mockConfigSession, mockTestBean);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void persist() {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        mockConfigSession.save(EasyMock.notNull(TestEntity.class));
        mockConfigSession.save(EasyMock.isNull(TestEntity.class));
        EasyMock.expectLastCall().andThrow(new RuntimeException("Just Test Exception"));
        TestBean mockTestBean = PowerMock.createMock(TestBean.class);
        EasyMock.expect(mockTestBean.convert()).andReturn(null).anyTimes();
        PowerMock.replayAll();
        mockBasicDao.persist(mockConfigSession, new TestBean());
        boolean exception = false;
        try {
            mockBasicDao.persist(mockConfigSession, mockTestBean);
        } catch (Exception e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void select() throws SQLException {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(TestEntity.class), EasyMock.notNull(Serializable.class))).andReturn(new TestEntity()).anyTimes();
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(TestEntity.class), EasyMock.isNull(Serializable.class))).andThrow(new RuntimeException("Just Test Exception")).anyTimes();
        PowerMock.replayAll();
        assertNotNull(mockBasicDao.select(mockConfigSession, ""));
        boolean exception = false;
        try {
            mockBasicDao.select(mockConfigSession, null);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void findWithOrder() {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(new TestEntity())).once();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Collections.emptyList()).once();
        mockConfigQuery.orderBy(EasyMock.anyObject(Order.class));
        EasyMock.expectLastCall().anyTimes();
        mockConfigQuery.where(EasyMock.anyObject(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(TestEntity.class))).andReturn(mockConfigQuery).anyTimes();
        PowerMock.replayAll();
        assertFalse(mockBasicDao.find(mockConfigSession, new Order[]{OrderImpl.asc("")}).list().isEmpty());
        assertTrue(mockBasicDao.find(mockConfigSession, new Order[]{OrderImpl.asc("")}, ConfigWhereImpl.eq("", "")).isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void find1() {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(new TestEntity())).once();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Collections.emptyList()).once();
        mockConfigQuery.where(EasyMock.anyObject(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(TestEntity.class))).andReturn(mockConfigQuery).anyTimes();
        PowerMock.replayAll();
        assertFalse(mockBasicDao.find(mockConfigSession).list().isEmpty());
        assertTrue(mockBasicDao.find(mockConfigSession, ConfigWhereImpl.eq("", "")).isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void deleteById() throws SQLException {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(TestEntity.class), EasyMock.notNull(Serializable.class))).andReturn(new TestEntity()).anyTimes();
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(TestEntity.class), EasyMock.isNull(Serializable.class))).andThrow(new RuntimeException("Just Test Exception")).anyTimes();
        mockConfigSession.delete(EasyMock.anyObject());
        PowerMock.replayAll();
        assertTrue(mockBasicDao.deleteById(mockConfigSession, ""));
        boolean exception = false;
        try {
            mockBasicDao.deleteById(mockConfigSession, null);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void delete() throws SQLException {
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        mockConfigSession.delete(EasyMock.notNull());
        mockConfigSession.delete(EasyMock.isNull());
        EasyMock.expectLastCall().andThrow(new RuntimeException("Just Test Exception")).anyTimes();
        TestBean mockTestBean = PowerMock.createMock(TestBean.class);
        EasyMock.expect(mockTestBean.convert()).andReturn(null).anyTimes();
        PowerMock.replayAll();

        PowerMock.replayAll();
        assertTrue(mockBasicDao.delete(mockConfigSession, new TestBean()));
        boolean exception = false;
        try {
            mockBasicDao.delete(mockConfigSession, mockTestBean);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }
}