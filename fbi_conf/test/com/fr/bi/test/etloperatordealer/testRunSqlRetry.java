package com.fr.bi.test.etloperatordealer;

import com.fr.bi.cluster.retry.RetryLoop;
import com.fr.bi.cluster.retry.RetryNTimes;
import com.fr.bi.common.inter.Traversal;
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

    public static SQLStatement testSqlStatement;

    public static int count = 0;

    /**
     * sql reconnect times test
     */
    public void test() {
        //success connection
        Connection connectionS = new JDBCDatabaseConnection("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/foodmart", "lucifer", "root123");
        final SQLStatement sqlStatementS = new SQLStatement(connectionS, "", "", "");

        //fail connection
        Connection connectionF = new JDBCDatabaseConnection("", "", "", "");
        final SQLStatement sqlStatementF = new SQLStatement(connectionF, "", "", "");

        testSqlStatement = sqlStatementF;

        Callable task = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                try {
                    if (testSqlStatement.getSqlConn().isClosed()) {
                        testSqlStatement.createConn();
                    }
                } catch (Exception e) {
                    count++;
                    if (count >= 3) {
                        testSqlStatement = sqlStatementS;
                    }
                    throw new Exception();
                }
                return 1;
            }
        };

        RetryLoop retryLoop = new RetryLoop();
        retryLoop.initial(new RetryNTimes(PerformancePlugManager.getInstance().getRetryMaxTimes(), PerformancePlugManager.getInstance().getRetryMaxSleepTime()));
        try {
            long a = ((Number) RetryLoop.retry(task, retryLoop)).longValue();
        } catch (Exception e) {
        } finally {
            assertEquals(3, count);
        }
    }
}

