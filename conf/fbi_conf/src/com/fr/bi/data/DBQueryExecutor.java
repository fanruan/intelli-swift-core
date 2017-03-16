package com.fr.bi.data;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cluster.retry.RetryLoop;
import com.fr.bi.cluster.retry.RetryNTimes;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.SQLStatement;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * This class created on 2016/8/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class DBQueryExecutor {
    private static Map<String, DBExtractor> DRIEVER_EXTRACOTR = new HashMap<String, DBExtractor>();
    private DBExtractor defaultExtractor;

    private static DBQueryExecutor instance;

    private DBQueryExecutor() {
        defaultExtractor = new NormalExtractor();
        initialExtractor();
    }

    private void initialExtractor() {
        DRIEVER_EXTRACOTR.put("org.apache.hive.jdbc.HiveDriver", new HiveExtractor());
    }

    public static DBQueryExecutor getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (DBQueryExecutor.class) {
                if (instance == null) {
                    instance = new DBQueryExecutor();
                }
                return instance;
            }
        }
    }


    public long runSQL(SQLStatement sql, ICubeFieldSource[] columns, Traversal<BIDataValue> traversal) {
        return runSQL(sql, columns, traversal, 0);
    }

    public long runSQL(final SQLStatement sql, final ICubeFieldSource[] columns, final Traversal<BIDataValue> traversal, final int row) {
        if (DRIEVER_EXTRACOTR.containsKey(sql.getConn().getDriver())) {
            return DRIEVER_EXTRACOTR.get(sql.getConn().getDriver()).runSQL(sql, columns, traversal, row);
        } else {

            Callable task = new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    try {
                        if (sql.getSqlConn().isClosed()) {
                            sql.createConn();
                        }
                    } catch (Exception e) {
                        BILoggerFactory.getLogger(DBQueryExecutor.class).error(e.getMessage(), e);
                    }
                    return defaultExtractor.runSQL(sql, columns, traversal, row);
                }
            };
            RetryLoop retryLoop = new RetryLoop();
            retryLoop.initial(new RetryNTimes(PerformancePlugManager.getInstance().getRetryMaxTimes(), PerformancePlugManager.getInstance().getRetryMaxSleepTime()));
            try {
                return ((Number) RetryLoop.retry(task, retryLoop)).longValue();
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e);
            }
        }
    }

    public boolean testSQL(SQLStatement sql) {
        if (DRIEVER_EXTRACOTR.containsKey(sql.getConn().getDriver())) {
            return true;
        } else {
            return defaultExtractor.testSQL(sql);
        }
    }
}
