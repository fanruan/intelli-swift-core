package com.finebi.cube.tools;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.DBField;

/**
 * This class created on 2016/3/30.
 *
 * @author Connery
 * @since 4.0
 */
public class DBFieldTestTool {
    public static DBField generateDATE() {
        return new DBField("tableA", "DATE", DBConstant.CLASS.DATE, 2);
    }

    public static DBField generateBOOLEAN() {
        return new DBField("tableA", "BOOLEAN", DBConstant.CLASS.BOOLEAN, 2);
    }

    public static DBField generateDECIMAL() {
        return new DBField("tableA", "DECIMAL", DBConstant.CLASS.DECIMAL, 2);
    }

    public static DBField generateDOUBLE() {
        return new DBField("tableA", "DOUBLE", DBConstant.CLASS.DOUBLE, 2);
    }
    public static DBField generateDOUBLE(String fieldName) {
        return new DBField("tableA",fieldName, DBConstant.CLASS.DOUBLE, 2);
    }
    public static DBField generateFLOAT() {
        return new DBField("tableA", "FLOAT", DBConstant.CLASS.FLOAT, 2);
    }

    public static DBField generateINTEGER() {
        return new DBField("tableA", "INTEGER", DBConstant.CLASS.INTEGER, 2);
    }

    public static DBField generateLONG() {
        return new DBField("tableA", "LONG", DBConstant.CLASS.LONG, 2);
    }

    public static DBField generateTIME() {
        return new DBField("tableA", "TIME", DBConstant.CLASS.TIME, 2);
    }

    public static DBField generateTIMESTAMP() {
        return new DBField("tableA", "TIMESTAMP", DBConstant.CLASS.TIMESTAMP, 2);
    }

    public static DBField generateSTRING() {
        return new DBField("tableA", "STRING", DBConstant.CLASS.STRING, 2);
    }

    public static DBField generateSTRINGA() {
        return new DBField("A", "a", DBConstant.CLASS.STRING, 2);
    }

}
