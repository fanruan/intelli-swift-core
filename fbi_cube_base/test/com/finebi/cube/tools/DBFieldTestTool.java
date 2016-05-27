package com.finebi.cube.tools;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;

/**
 * This class created on 2016/3/30.
 *
 * @author Connery
 * @since 4.0
 */
public class DBFieldTestTool {
    public static BICubeFieldSource generateDATE() {
        return new BICubeFieldSource(null, "DATE", DBConstant.CLASS.DATE, 2);
    }

    public static ICubeFieldSource generateBOOLEAN() {
        return new BICubeFieldSource(null, "BOOLEAN", DBConstant.CLASS.BOOLEAN, 2);
    }

    public static ICubeFieldSource generateDECIMAL() {
        return new BICubeFieldSource(null, "DECIMAL", DBConstant.CLASS.DECIMAL, 2);
    }

    public static BICubeFieldSource generateDOUBLE() {
        return new BICubeFieldSource(null, "DOUBLE", DBConstant.CLASS.DOUBLE, 2);
    }

    public static BICubeFieldSource generateDOUBLE(String fieldName) {
        return new BICubeFieldSource(null, fieldName, DBConstant.CLASS.DOUBLE, 2);
    }

    public static ICubeFieldSource generateFLOAT() {
        return new BICubeFieldSource(null, "FLOAT", DBConstant.CLASS.FLOAT, 2);
    }

    public static ICubeFieldSource generateINTEGER() {
        return new BICubeFieldSource(null, "INTEGER", DBConstant.CLASS.INTEGER, 2);
    }

    public static BICubeFieldSource generateLONG() {
        return new BICubeFieldSource(null, "LONG", DBConstant.CLASS.LONG, 2);
    }

    public static BICubeFieldSource generateTIME() {
        return new BICubeFieldSource(null, "TIME", DBConstant.CLASS.TIME, 2);
    }

    public static ICubeFieldSource generateTIMESTAMP() {
        return new BICubeFieldSource(null, "TIMESTAMP", DBConstant.CLASS.TIMESTAMP, 2);
    }

    public static BICubeFieldSource generateSTRING() {
        return new BICubeFieldSource(null, "STRING", DBConstant.CLASS.STRING, 2);
    }

    public static BICubeFieldSource generateSTRINGA() {
        return new BICubeFieldSource(null, "a", DBConstant.CLASS.STRING, 2);
    }

}
