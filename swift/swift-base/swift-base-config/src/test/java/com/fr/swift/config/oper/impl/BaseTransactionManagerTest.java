package com.fr.swift.config.oper.impl;

import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigTransaction;
import com.fr.swift.config.oper.TransactionManager.TransactionWorker;
import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018-11-30
 */
public class BaseTransactionManagerTest {

    @Test
    public void doTransactionIfNeed() throws SQLException {
        // Generate by Mock Plugin
        final ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ConfigTransaction mockConfigTransaction = PowerMock.createMock(ConfigTransaction.class);
//        mockConfigTransaction.begin();
//        EasyMock.expectLastCall().times(2);
        mockConfigTransaction.rollback();
        mockConfigTransaction.commit();
        EasyMock.expect(mockConfigSession.beginTransaction()).andReturn(mockConfigTransaction).anyTimes();
        mockConfigSession.close();
        EasyMock.expectLastCall().times(3);
        // Generate by Mock Plugin
        TransactionWorker normal = PowerMock.createMock(TransactionWorker.class);
        EasyMock.expect(normal.needTransaction()).andReturn(true).anyTimes();
        EasyMock.expect(normal.work(EasyMock.anyObject(ConfigSession.class))).andReturn(null).anyTimes();
        TransactionWorker exception = PowerMock.createMock(TransactionWorker.class);
        EasyMock.expect(exception.needTransaction()).andReturn(true).anyTimes();
        EasyMock.expect(exception.work(EasyMock.anyObject(ConfigSession.class))).andThrow(new SQLException("Just Test Exception")).anyTimes();
        TransactionWorker noTransaction = PowerMock.createMock(TransactionWorker.class);
        EasyMock.expect(noTransaction.needTransaction()).andReturn(false).anyTimes();
        EasyMock.expect(noTransaction.work(EasyMock.anyObject(ConfigSession.class))).andReturn(null).anyTimes();
        PowerMock.replayAll();
        BaseTransactionManager manager = new BaseTransactionManager() {
            @Override
            protected ConfigSession createSession() {
                return mockConfigSession;
            }
        };
        manager.doTransactionIfNeed(normal);
        boolean exp = false;
        try {
            manager.doTransactionIfNeed(exception);
        } catch (SQLException e) {
            exp = true;
        }
        assertTrue(exp);
        manager.doTransactionIfNeed(noTransaction);
        PowerMock.verifyAll();

    }
}