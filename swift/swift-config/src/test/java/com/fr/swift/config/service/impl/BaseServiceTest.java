package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigTransaction;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * @author yee
 * @date 2019-01-04
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SwiftContext.class)
public class BaseServiceTest {

    protected Class entityClass;

    public BaseServiceTest(Class entityClass) {
        this.entityClass = entityClass;
    }

    protected ConfigSession mockSession() throws SQLException {
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
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(entityClass))).andReturn(null).anyTimes();
        EasyMock.expect(mockConfigSession.get(EasyMock.eq(entityClass), EasyMock.notNull(Serializable.class))).andReturn(null).anyTimes();
        EasyMock.expect(mockConfigSession.beginTransaction()).andReturn(mockTransaction()).anyTimes();
        PowerMock.replay(mockConfigSession);
        return mockConfigSession;
    }

    protected ConfigTransaction mockTransaction() {
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
}
