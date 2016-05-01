package com.finebi.cube.tools;

import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.stable.data.source.ITableSource;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public class BITableSourceTestTool {
    public static ITableSource getDBTableSourceA() {
        return new DBTableSource("DB_A", "Table_A");
    }

    public static ITableSource getDBTableSourceB() {
        return new DBTableSource("DB_A", "Table_B");
    }

    public static ITableSource getDBTableSourceC() {
        return new DBTableSource("DB_A", "Table_C");
    }

    public static ITableSource getDBTableSourceD() {
        return new DBTableSource("DB_D", "Table_D");
    }
}
