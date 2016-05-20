package com.finebi.cube.tools;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BICubeFieldSource;

/**
 * This class created on 2016/3/30.
 *
 * @author Connery
 * @since 4.0
 */
public class DBFieldTestTool {
    public static BICubeFieldSource generateDATE() {
        return new BICubeFieldSource("tableA", "DATE", DBConstant.CLASS.DATE, 2);
    }

    public static BICubeFieldSource generateBOOLEAN() {
        return new BICubeFieldSource("tableA", "BOOLEAN", DBConstant.CLASS.BOOLEAN, 2);
    }

    public static BICubeFieldSource generateDECIMAL() {
        return new BICubeFieldSource("tableA", "DECIMAL", DBConstant.CLASS.DECIMAL, 2);
    }

    public static BICubeFieldSource generateDOUBLE() {
        return new BICubeFieldSource("tableA", "DOUBLE", DBConstant.CLASS.DOUBLE, 2);
    }
    public static BICubeFieldSource generateDOUBLE(String fieldName) {
        return new BICubeFieldSource("tableA",fieldName, DBConstant.CLASS.DOUBLE, 2);
    }
    public static BICubeFieldSource generateFLOAT() {
        return new BICubeFieldSource("tableA", "FLOAT", DBConstant.CLASS.FLOAT, 2);
    }

    public static BICubeFieldSource generateINTEGER() {
        return new BICubeFieldSource("tableA", "INTEGER", DBConstant.CLASS.INTEGER, 2);
    }

    public static BICubeFieldSource generateLONG() {
        return new BICubeFieldSource("tableA", "LONG", DBConstant.CLASS.LONG, 2);
    }

    public static BICubeFieldSource generateTIME() {
        return new BICubeFieldSource("tableA", "TIME", DBConstant.CLASS.TIME, 2);
    }

    public static BICubeFieldSource generateTIMESTAMP() {
        return new BICubeFieldSource("tableA", "TIMESTAMP", DBConstant.CLASS.TIMESTAMP, 2);
    }

    public static BICubeFieldSource generateSTRING() {
        return new BICubeFieldSource("tableA", "STRING", DBConstant.CLASS.STRING, 2);
    }

    public static BICubeFieldSource generateSTRINGA() {
        return new BICubeFieldSource("A", "a", DBConstant.CLASS.STRING, 2);
    }

}
