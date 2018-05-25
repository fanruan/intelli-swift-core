package com.fr.swift.config.transaction;

import com.fr.config.BaseDBEnv;
import com.fr.config.utils.SyncUtils;
import com.fr.swift.config.context.SwiftConfigContext;
import com.fr.transaction.ConnectionHolder;
import com.fr.transaction.SyncManager;
import com.fr.transaction.TransactionManager;
import com.fr.transaction.TransactionManagerFactory;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/5/25
 */
public class SwiftTransactionManager {
    public static Object doTransactionIfNeed(TransactionWorker worker) throws SQLException {
        if (!SyncManager.isTransactionOpen(BaseDBEnv.getDBContext()) && worker.needTransaction()) {
            TransactionManager transactionManager = TransactionManagerFactory.getTransactionManager();
            ConnectionHolder connectionHolder = transactionManager.getConnectionHolder();
            try {
                SyncUtils.sync();
                transactionManager.begin(connectionHolder);
                Object result = work(worker);
                transactionManager.commit(connectionHolder);
                return result;
            } catch (Throwable throwable) {
                transactionManager.rollback(connectionHolder);
                throw new SQLException(throwable);
            } finally {
                transactionManager.close(connectionHolder);
                SyncUtils.release();
            }
        } else {
            return work(worker);
        }
    }

    private static Object work(TransactionWorker worker) throws SQLException {
        switch (worker.type()) {
            case META:
                return worker.work(SwiftConfigContext.getInstance().getSwiftMetaDataDAO());
            case SEGMENT:
                return worker.work(SwiftConfigContext.getInstance().getSwiftSegmentDAO());
            default:
                return null;
        }
    }
}
