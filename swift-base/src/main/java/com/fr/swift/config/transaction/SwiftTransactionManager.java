package com.fr.swift.config.transaction;

import com.fr.config.BaseDBEnv;
import com.fr.config.utils.SyncUtils;
import com.fr.swift.config.dao.SwiftMetaDataDao;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;
import com.fr.transaction.ConnectionHolder;
import com.fr.transaction.SyncManager;
import com.fr.transaction.TransactionManager;
import com.fr.transaction.TransactionManagerFactory;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/5/25
 */
@Service
public class SwiftTransactionManager {
    @Autowired
    private SwiftMetaDataDao swiftMetaDataDao;
    @Autowired
    private SwiftSegmentDao swiftSegmentDao;
    @Autowired
    private SwiftServiceInfoDao swiftServiceInfoDao;

    public Object doTransactionIfNeed(TransactionWorker worker) throws SQLException {
        if (!SyncManager.isTransactionOpen(BaseDBEnv.getDBContext()) && worker.needTransaction()) {
            TransactionManager transactionManager = TransactionManagerFactory.getTransactionManager();
            ConnectionHolder connectionHolder = transactionManager.getConnectionHolder();
            try {
                SyncUtils.sync();
                transactionManager.begin(connectionHolder);
                Object result = worker.work();
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
            return worker.work();
        }
    }
}
