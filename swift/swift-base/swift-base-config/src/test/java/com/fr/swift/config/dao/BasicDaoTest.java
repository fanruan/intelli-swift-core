package com.fr.swift.config.dao;

import com.fr.swift.config.oper.ConfigCriteria;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.RestrictionFactory;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.io.Serializable;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018-11-28
 */
public class BasicDaoTest {

    private BasicDao<TestBean> mockBasicDao;

    @Before
    public void before() {
        RestrictionFactory mockRestrictionFactory = PowerMock.createMock(RestrictionFactory.class);
        mockBasicDao = PowerMock.createMock(BasicDao.class, TestEntity.class, mockRestrictionFactory);
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
        mockConfigSession.persist(EasyMock.notNull(TestEntity.class));
        mockConfigSession.persist(EasyMock.isNull(TestEntity.class));
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
        ConfigCriteria mockConfigCriteria = PowerMock.createMock(ConfigCriteria.class);
        EasyMock.expect(mockConfigCriteria.list()).andReturn(null).anyTimes();
        mockConfigCriteria.add(EasyMock.anyObject());
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                return null;
            }
        }).anyTimes();
        mockConfigCriteria.addOrder(EasyMock.anyObject());
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                return null;
            }
        }).anyTimes();
        EasyMock.expect(mockConfigSession.createCriteria(EasyMock.eq(TestEntity.class))).andReturn(mockConfigCriteria).anyTimes();
        PowerMock.replayAll();


    }

    @Test
    public void find1() {
    }

    @Test
    public void deleteById() {
    }

    @Test
    public void delete() {
    }
}