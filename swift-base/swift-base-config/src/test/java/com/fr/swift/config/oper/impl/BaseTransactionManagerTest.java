package com.fr.swift.config.oper.impl;

import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigTransaction;
import com.fr.swift.config.oper.TransactionManager.TransactionWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018-11-30
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TransactionWorker.class)
public class BaseTransactionManagerTest {

    @Test
    public void doTransactionIfNeed() throws SQLException {

        final ConfigSession mockConfigSession = PowerMockito.mock(ConfigSession.class);
        ConfigTransaction mockConfigTransaction = PowerMockito.mock(ConfigTransaction.class);
        mockConfigTransaction.rollback();
        mockConfigTransaction.commit();

        Mockito.when(mockConfigSession.beginTransaction()).thenReturn(mockConfigTransaction);


        mockConfigSession.close();
        PowerMockito.mockStatic(TransactionWorker.class);
        TransactionWorker normal = PowerMockito.mock(TransactionWorker.class);
        Mockito.when(normal.work(mockConfigSession)).thenReturn(null);
        Mockito.when(normal.needTransaction()).thenReturn(true);

        TransactionWorker exception = PowerMockito.mock(TransactionWorker.class);
        Mockito.when(exception.work(mockConfigSession)).thenThrow(new SQLException("Just Test Exception"));
        Mockito.when(exception.needTransaction()).thenReturn(true);

        // Generate by Mock Plugin
        TransactionWorker noTransaction = PowerMockito.mock(TransactionWorker.class);
        Mockito.when(noTransaction.work(mockConfigSession)).thenReturn(null);
        Mockito.when(noTransaction.needTransaction()).thenReturn(false);
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