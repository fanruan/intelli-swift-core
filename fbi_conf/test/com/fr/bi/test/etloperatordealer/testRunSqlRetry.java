package com.fr.bi.test.etloperatordealer;

import com.fr.bi.cluster.retry.RetryLoop;
import com.fr.bi.cluster.retry.RetryNTimes;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.data.DBExtractor;
import com.fr.bi.data.NormalExtractor;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.data.db.SQLStatement;
import com.fr.data.impl.Connection;
import com.fr.data.impl.JDBCDatabaseConnection;
import junit.framework.TestCase;

import java.util.concurrent.Callable;

/**
 * Created by Lucifer on 2016/12/28.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class testRunSqlRetry extends TestCase {

    /**
     * sql reconnect times test
     */
    public void test() {

        Connection connection = new JDBCDatabaseConnection();
        final SQLStatement sqlStatement = new SQLStatement(connection, "", "", "");
        final DBExtractor defaultExtractor = new NormalExtractor();
        final Traversal traversal = new RetryTestTraversal();

        Callable task = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                try {
                    if (sqlStatement.getSqlConn().isClosed()) {
                        sqlStatement.createConn();
                    }
                } catch (Exception e) {
                    traversal.actionPerformed(null);
                }
                return defaultExtractor.runSQL(sqlStatement, null, traversal, 0);
            }
        };
        RetryLoop retryLoop = new RetryLoop();
        retryLoop.initial(new RetryNTimes(PerformancePlugManager.getInstance().getRetryMaxTimes(), PerformancePlugManager.getInstance().getRetryMaxSleepTime()));
        try {
            long a = ((Number) RetryLoop.retry(task, retryLoop)).longValue();
        } catch (Exception e) {

        } finally {
            assertEquals(PerformancePlugManager.getInstance().getRetryMaxTimes() + 1, RetryTestTraversal.count);
        }

    }
}

class RetryTestTraversal<BIDataValue> implements Traversal<BIDataValue> {

    public static int count = 0;

    @Override
    public void actionPerformed(BIDataValue v) {
        count++;
    }
}

