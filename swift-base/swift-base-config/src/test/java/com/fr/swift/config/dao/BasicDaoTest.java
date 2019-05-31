package com.fr.swift.config.dao;

import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Order;
import com.fr.swift.config.oper.Page;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.oper.impl.OrderImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        ConfigSession mockSession = Mockito.mock(ConfigSession.class);
        Mockito.when(mockSession.merge(Mockito.notNull(TestEntity.class))).thenReturn(new TestEntity());
        Mockito.when(mockSession.merge(null)).thenThrow(new SQLException("just test exception"));
        TestBean mockBean = Mockito.mock(TestBean.class);
        Mockito.when(mockBean.convert()).thenReturn(null);

        mockBasicDao.saveOrUpdate(mockSession, new TestBean());
        Mockito.verify(mockSession).merge(Mockito.notNull(TestEntity.class));

        boolean exception = false;
        try {
            mockBasicDao.saveOrUpdate(mockSession, mockBean);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        Mockito.verify(mockSession).merge(null);
        Mockito.verify(mockBean).convert();
    }

    @Test
    public void persist() {
        ConfigSession mockSession = Mockito.mock(ConfigSession.class);
        Mockito.doThrow(new RuntimeException("Just Test Exception")).when(mockSession).save(null);
        Mockito.doNothing().when(mockSession).save(Mockito.notNull(TestEntity.class));
        TestBean mockTestBean = Mockito.mock(TestBean.class);
        Mockito.when(mockTestBean.convert()).thenReturn(null);
        mockBasicDao.persist(mockSession, new TestBean());
        Mockito.verify(mockSession).save(Mockito.notNull(TestEntity.class));
        boolean exception = false;
        try {
            mockBasicDao.persist(mockSession, mockTestBean);
        } catch (Exception e) {
            exception = true;
        }
        assertTrue(exception);
        Mockito.verify(mockSession).save(null);
        Mockito.verify(mockTestBean).convert();
    }

    @Test
    public void select() throws SQLException {
        ConfigSession mockConfigSession = Mockito.mock(ConfigSession.class);
        Mockito.when(mockConfigSession.get(Mockito.eq(TestEntity.class), Mockito.notNull(Serializable.class))).thenReturn(new TestEntity());
        Mockito.when(mockConfigSession.get(TestEntity.class, null)).thenThrow(new RuntimeException("Just Test Exception"));
        assertNotNull(mockBasicDao.select(mockConfigSession, ""));
        Mockito.verify(mockConfigSession).get(Mockito.eq(TestEntity.class), Mockito.notNull(Serializable.class));
        boolean exception = false;
        try {
            mockBasicDao.select(mockConfigSession, null);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        Mockito.verify(mockConfigSession).get(TestEntity.class, null);
    }

    @Test
    public void findWithOrder() {
        ConfigSession mockConfigSession = Mockito.mock(ConfigSession.class);
        ConfigQuery mockConfigQuery = Mockito.mock(ConfigQuery.class);
        Mockito.when(mockConfigQuery.executeQuery()).then(new Answer<List>() {
            AtomicInteger time = new AtomicInteger(0);

            @Override
            public List answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (time.getAndIncrement() < 1) {
                    return Arrays.asList(new TestEntity());
                }
                return Collections.emptyList();
            }
        });
        Mockito.doNothing().when(mockConfigQuery).orderBy(Mockito.any(Order.class));
        Mockito.doNothing().when(mockConfigQuery).where(Mockito.any(ConfigWhere.class));
        Mockito.when(mockConfigSession.createEntityQuery(TestEntity.class)).thenReturn(mockConfigQuery);
        assertFalse(mockBasicDao.find(mockConfigSession, new Order[]{OrderImpl.asc("")}).list().isEmpty());
        assertTrue(mockBasicDao.find(mockConfigSession, new Order[]{OrderImpl.asc("")}, ConfigWhereImpl.eq("", "")).isEmpty());
        Mockito.verify(mockConfigQuery, Mockito.atLeastOnce()).orderBy(Mockito.any(Order.class));
        Mockito.verify(mockConfigQuery, Mockito.atLeastOnce()).where(Mockito.any(ConfigWhere.class));
        Mockito.verify(mockConfigSession, Mockito.atLeastOnce()).createEntityQuery(TestEntity.class);
        Mockito.verify(mockConfigQuery, Mockito.times(2)).executeQuery();
    }

    @Test
    public void find1() {
        ConfigSession mockConfigSession = Mockito.mock(ConfigSession.class);
        ConfigQuery mockConfigQuery = Mockito.mock(ConfigQuery.class);
        Mockito.when(mockConfigQuery.executeQuery()).then(new Answer<List>() {
            AtomicInteger time = new AtomicInteger(0);

            @Override
            public List answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (time.getAndIncrement() < 1) {
                    return Arrays.asList(new TestEntity());
                }
                return Collections.emptyList();
            }
        });
        Mockito.doNothing().when(mockConfigQuery).where(Mockito.any(ConfigWhere.class));
        Mockito.when(mockConfigSession.createEntityQuery(TestEntity.class)).thenReturn(mockConfigQuery);
        assertFalse(mockBasicDao.find(mockConfigSession).list().isEmpty());
        assertTrue(mockBasicDao.find(mockConfigSession, ConfigWhereImpl.eq("", "")).isEmpty());
        Mockito.verify(mockConfigQuery, Mockito.times(2)).executeQuery();
        Mockito.verify(mockConfigQuery, Mockito.atLeastOnce()).where(Mockito.any(ConfigWhere.class));
    }

    @Test
    public void deleteById() throws SQLException {
        ConfigSession mockConfigSession = Mockito.mock(ConfigSession.class);
        Mockito.when(mockConfigSession.get(Mockito.eq(TestEntity.class), Mockito.notNull(Serializable.class))).thenReturn(new TestEntity());
        Mockito.when(mockConfigSession.get(TestEntity.class, null)).thenThrow(new RuntimeException("Just Test Exception"));
        Mockito.doNothing().when(mockConfigSession).delete(Mockito.any());
        assertTrue(mockBasicDao.deleteById(mockConfigSession, ""));
        Mockito.verify(mockConfigSession).get(Mockito.eq(TestEntity.class), Mockito.notNull(Serializable.class));
        boolean exception = false;
        try {
            mockBasicDao.deleteById(mockConfigSession, null);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        Mockito.verify(mockConfigSession).get(TestEntity.class, null);
    }

    @Test
    public void delete() throws SQLException {
        ConfigSession mockConfigSession = Mockito.mock(ConfigSession.class);
        Mockito.doNothing().when(mockConfigSession).delete(Mockito.notNull());
        Mockito.doThrow(new RuntimeException("Just Test Exception")).when(mockConfigSession).delete(null);
        TestBean mockTestBean = Mockito.mock(TestBean.class);
        Mockito.when(mockTestBean.convert()).thenReturn(null);
        assertTrue(mockBasicDao.delete(mockConfigSession, new TestBean()));
        Mockito.verify(mockConfigSession).delete(Mockito.notNull());
        boolean exception = false;
        try {
            mockBasicDao.delete(mockConfigSession, mockTestBean);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        Mockito.verify(mockConfigSession).delete(null);
    }

    @Test
    public void findPage() {
        ConfigSession mockConfigSession = Mockito.mock(ConfigSession.class);
        ConfigQuery mockConfigQuery = Mockito.mock(ConfigQuery.class);
        Mockito.when(mockConfigQuery.executeQuery(Mockito.anyInt(), Mockito.anyInt())).then(new Answer<Page>() {
            AtomicInteger time = new AtomicInteger(0);

            @Override
            public Page answer(InvocationOnMock invocationOnMock) throws Throwable {
                Page page = new Page();

                page.setCurrentPage(1);
                page.setPageSize(10);
                if (time.getAndIncrement() < 1) {
                    page.setData(Arrays.asList(new TestEntity()));
                    page.setTotal(1);
                } else {
                    page.setData(Collections.emptyList());
                    page.setTotal(0);
                }
                return page;
            }
        });
        Mockito.doNothing().when(mockConfigQuery).where(Mockito.any(ConfigWhere.class));
        Mockito.when(mockConfigSession.createEntityQuery(TestEntity.class)).thenReturn(mockConfigQuery);
        Page page1 = mockBasicDao.findPage(mockConfigSession, 1, 10);
        assertFalse(page1.getData().isEmpty());
        Page page2 = mockBasicDao.findPage(mockConfigSession, 1, 10, ConfigWhereImpl.eq("", ""));
        assertTrue(page2.getData().isEmpty());
        Mockito.verify(mockConfigQuery, Mockito.times(2)).executeQuery(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(mockConfigQuery, Mockito.atLeastOnce()).where(Mockito.any(ConfigWhere.class));
    }
}