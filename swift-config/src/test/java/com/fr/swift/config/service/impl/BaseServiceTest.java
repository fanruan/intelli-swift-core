package com.fr.swift.config.service.impl;

import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigTransaction;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Order;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.powermock.api.easymock.PowerMock;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author yee
 * @date 2019-01-04
 */
public class BaseServiceTest {

    ConfigSession mockSession(Class entityClass, Object... objs) throws SQLException {
        // Generate by Mock Plugin
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        mockConfigSession.delete(EasyMock.anyObject());
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                return null;
            }
        }).anyTimes();
        mockConfigSession.save(EasyMock.anyObject());
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                return null;
            }
        }).anyTimes();
        mockConfigSession.close();
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                return null;
            }
        }).anyTimes();
        EasyMock.expect(mockConfigSession.merge(EasyMock.notNull(entityClass))).andReturn(null).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(entityClass))).andReturn(mockQuery(objs)).anyTimes();
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(entityClass), EasyMock.notNull(Serializable.class))).andReturn(null).anyTimes();
        EasyMock.expect(mockConfigSession.beginTransaction()).andReturn(mockTransaction()).anyTimes();
        PowerMock.replay(mockConfigSession);
        return mockConfigSession;
    }

    ConfigTransaction mockTransaction() {
        // Generate by Mock Plugin
        ConfigTransaction mockConfigTransaction = PowerMock.createMock(ConfigTransaction.class);
        mockConfigTransaction.rollback();
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                return null;
            }
        }).anyTimes();
        mockConfigTransaction.commit();
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                return null;
            }
        }).anyTimes();
        PowerMock.replay(mockConfigTransaction);

        return mockConfigTransaction;
    }

    ConfigQuery mockQuery(Object... objs) {
        // Generate by Mock Plugin
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class), EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        mockConfigQuery.orderBy(EasyMock.notNull(Order.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(objs)).anyTimes();
        PowerMock.replay(mockConfigQuery);
        return mockConfigQuery;
    }

}
