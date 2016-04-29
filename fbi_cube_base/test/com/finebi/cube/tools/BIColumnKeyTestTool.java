package com.finebi.cube.tools;

import com.finebi.cube.structure.column.BIColumnKey;

/**
 * This class created on 2016/3/31.
 *
 * @author Connery
 * @since 4.0
 */
public class BIColumnKeyTestTool {
    public static BIColumnKey generateA() {
        return new BIColumnKey("A", BIColumnKey.STRING_COLUMN_TYPE, BIColumnKey.EMPTY_SUB_TYPE);
    }
}
