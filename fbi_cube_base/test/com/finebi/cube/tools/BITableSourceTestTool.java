package com.finebi.cube.tools;

import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.stable.data.source.CubeTableSource;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public class BITableSourceTestTool {
    public static CubeTableSource getDBTableSourceA() {
        return new DBTableSource("DB_A", "Table_A");
    }

    public static CubeTableSource getDBTableSourceB() {
        return new DBTableSource("DB_A", "Table_B");
    }

    public static CubeTableSource getDBTableSourceC() {
        return new DBTableSource("DB_A", "Table_C");
    }

    public static CubeTableSource getDBTableSourceD() {
        return new DBTableSource("DB_D", "Table_D");
    }
    public static CubeTableSource getDBTableSourcePerson() {
        return new DBTableSource("DB_Person", "Person");
    }
}
