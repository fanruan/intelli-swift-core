package com.finebi.cube.conf.relation;

import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;

/**
 * Created by Connery on 2016/1/14.
 */
public class BITableTestTool {

    public static BusinessTable getA() {
        return new BIBusinessTable("A", "A_table_Name");
    }

    public static BusinessTable getB() {
        return new BIBusinessTable("B", "B_table_Name");
    }

    public static BusinessTable getC() {
        return new BIBusinessTable("C", "C_table_Name");
    }

    public static BusinessTable getD() {
        return new BIBusinessTable("D", "D_table_Name");
    }

    public static BusinessTable getE() {
        return new BIBusinessTable("E", "E_table_Name");
    }

    public static BusinessTable getF() {
        return new BIBusinessTable("F", "F_table_Name");
    }

    public static BusinessTable getG() {
        return new BIBusinessTable("G", "G_table_Name");
    }

    public static BusinessTable getH() {
        return new BIBusinessTable("H", "H_table_Name");
    }

    public static BusinessTable getI() {
        return new BIBusinessTable("I", "I_table_Name");
    }

    public static BusinessTable getK() {
        return new BIBusinessTable("K", "K_table_Name");
    }
}