package com.finebi.cube.tools;

import com.fr.bi.stable.relation.BIRowField;
import com.finebi.cube.relation.BITableSourceRelation;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public class BITableSourceRelationTestTool {
    public static BITableSourceRelation getAB() {
        return new BITableSourceRelation(BIRowField.rowNumberField, BIRowField.rowNumberField, BITableSourceTestTool.getDBTableSourceA(), BITableSourceTestTool.getDBTableSourceB());
    }

    public static BITableSourceRelation getAaB() {
        return new BITableSourceRelation(DBFieldTestTool.generateSTRINGA(), BIRowField.rowNumberField, BITableSourceTestTool.getDBTableSourceA(), BITableSourceTestTool.getDBTableSourceB());
    }

    public static BITableSourceRelation getAC() {
        return new BITableSourceRelation(BIRowField.rowNumberField, BIRowField.rowNumberField, BITableSourceTestTool.getDBTableSourceA(), BITableSourceTestTool.getDBTableSourceC());
    }

    public static BITableSourceRelation getBC() {
        return new BITableSourceRelation(BIRowField.rowNumberField, BIRowField.rowNumberField, BITableSourceTestTool.getDBTableSourceB(), BITableSourceTestTool.getDBTableSourceC());
    }

    public static BITableSourceRelation getMemoryAB() {
        return new BITableSourceRelation(BIRowField.rowNumberField, BIRowField.rowNumberField, BIMemoryDataSourceFactory.generateTableA(), BIMemoryDataSourceFactory.generateTableB());
    }

    public static BITableSourceRelation getMemoryBC() {
        return new BITableSourceRelation(BIRowField.rowNumberField, BIRowField.rowNumberField, BIMemoryDataSourceFactory.generateTableB(), BIMemoryDataSourceFactory.generateTableC());
    }

    public static BITableSourceRelation getMemoryCD() {
        return new BITableSourceRelation(BIRowField.rowNumberField, BIRowField.rowNumberField, BIMemoryDataSourceFactory.generateTableC(), BIMemoryDataSourceFactory.generateTableD());
    }

}
