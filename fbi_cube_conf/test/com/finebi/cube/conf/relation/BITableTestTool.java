package com.finebi.cube.conf.relation;

import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.IBusinessTable;

/**
 * Created by Connery on 2016/1/14.
 */
public class BITableTestTool {

    public static IBusinessTable getA() {
        return new BIBusinessTable("A", "A_table_Name");
    }

    public static IBusinessTable getB() {
        return new BIBusinessTable("B", "B_table_Name");
    }

    public static IBusinessTable getC() {
        return new BIBusinessTable("C", "C_table_Name");
    }

    public static IBusinessTable getD() {
        return new BIBusinessTable("D", "D_table_Name");
    }

    public static IBusinessTable getE() {
        return new BIBusinessTable("E", "E_table_Name");
    }

    public static IBusinessTable getF() {
        return new BIBusinessTable("F", "F_table_Name");
    }

    public static IBusinessTable getG() {
        return new BIBusinessTable("G", "G_table_Name");
    }

    public static IBusinessTable getH() {
        return new BIBusinessTable("H", "H_table_Name");
    }

    public static IBusinessTable getI() {
        return new BIBusinessTable("I", "I_table_Name");
    }

    public static IBusinessTable getK() {
        return new BIBusinessTable("K", "K_table_Name");
    }
}