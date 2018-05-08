package com.fr.swift.fine.adaptor.conf.creater;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.basictable.table.FineDBBusinessTable;

/**
 * This class created on 2018-1-24 10:17:49
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class TestTableCreator {
//    public static FineDBBusinessTable tableA = new FineDBBusinessTable("A", FineEngineType.Cube, "local", "A");
//    public static FineDBBusinessTable tableB = new FineDBBusinessTable("B", FineEngineType.Cube, "local", "B");
//    public static FineDBBusinessTable tableC = new FineDBBusinessTable("C", FineEngineType.Cube, "local", "C");
//    public static FineDBBusinessTable tableD = new FineDBBusinessTable("D", FineEngineType.Cube, "local", "D");
//    public static FineDBBusinessTable tableE = new FineDBBusinessTable("E", FineEngineType.Cube, "local", "E");

    public static FineDBBusinessTable createA() {
        FineDBBusinessTable A = new FineDBBusinessTable("A", FineEngineType.Cube, "local", "A", "A");
        A.addField(TestFieldCreator.fieldA);
        return A;
    }

    public static FineDBBusinessTable createB() {
        FineDBBusinessTable B = new FineDBBusinessTable("B", FineEngineType.Cube, "local", "B", "B");
        B.addField(TestFieldCreator.fieldB);
        return B;
    }

    public static FineDBBusinessTable createC() {
        FineDBBusinessTable C = new FineDBBusinessTable("C", FineEngineType.Cube, "local", "C", "C");
        C.addField(TestFieldCreator.fieldC);
        return C;
    }

    public static FineDBBusinessTable createD() {
        FineDBBusinessTable D = new FineDBBusinessTable("D", FineEngineType.Cube, "local", "D", "D");
        D.addField(TestFieldCreator.fieldD);
        return D;
    }

    public static FineDBBusinessTable createE() {
        FineDBBusinessTable E = new FineDBBusinessTable("E", FineEngineType.Cube, "local", "E", "E");
        E.addField(TestFieldCreator.fieldE);
        return E;
    }

}
