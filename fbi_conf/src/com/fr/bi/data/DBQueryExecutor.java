package com.fr.bi.data;

import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.SQLStatement;

import java.util.HashMap;
import java.util.Map;

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

    public long runSQL(SQLStatement sql, ICubeFieldSource[] columns, Traversal<BIDataValue> traversal, int row) {
        if (DRIEVER_EXTRACOTR.containsKey(sql.getConn().getDriver())) {
            return DRIEVER_EXTRACOTR.get(sql.getConn().getDriver()).runSQL(sql, columns, traversal, row);
        } else {
            return defaultExtractor.runSQL(sql, columns, traversal, row);
        }
    }
}
