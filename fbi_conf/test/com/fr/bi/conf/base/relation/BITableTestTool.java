package com.fr.bi.conf.base.relation;

import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;

/**
 * Created by Connery on 2016/1/14.
 */
public class BITableTestTool {

    public static BITable getA() {
        return new BITable("A", "A_table_Name");
    }

    public static BITable getB() {
        return new BITable("B", "B_table_Name");
    }

    public static BITable getC() {
        return new BITable("C", "C_table_Name");
    }

    public static BITable getD() {
        return new BITable("D", "D_table_Name");
    }

    public static BITable getE() {
        return new BITable("E", "E_table_Name");
    }

    public static Table getF() {
        return new BITable("F", "F_table_Name");
    }

    public static Table getG() {
        return new BITable("G", "G_table_Name");
    }

    public static Table getH() {
        return new BITable("H", "H_table_Name");
    }

    public static Table getI() {
        return new BITable("I", "I_table_Name");
    }

    public static Table getK() {
        return new BITable("K", "K_table_Name");
    }
}