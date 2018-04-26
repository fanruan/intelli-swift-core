package com.fr.swift.fine.adaptor.conf.creater;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.pack.FineBusinessPackageImp;
import com.finebi.conf.structure.bean.pack.FineBusinessPackage;

/**
 * This class created on 2018-1-24 14:53:43
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class TestPackageCreator {

    public static FineBusinessPackage createP1() {
        FineBusinessPackage fineBusinessPackage = new FineBusinessPackageImp("001", "lucifer001", -999, 1000, FineEngineType.Cube, 1000);
        return fineBusinessPackage;
    }

    public static FineBusinessPackage createP2() {
        FineBusinessPackage fineBusinessPackage = new FineBusinessPackageImp("002", "lucifer002", -999, 1000, FineEngineType.Cube, 1000);
        return fineBusinessPackage;
    }

    public static FineBusinessPackage createP3() {
        FineBusinessPackage fineBusinessPackage = new FineBusinessPackageImp("003", "lucifer003", -999, 1000, FineEngineType.Cube, 1000);
        return fineBusinessPackage;
    }
}
