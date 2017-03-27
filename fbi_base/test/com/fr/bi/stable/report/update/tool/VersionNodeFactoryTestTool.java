package com.fr.bi.stable.report.update.tool;

import com.fr.bi.stable.report.update.ReportConfVersionNode;
import com.fr.bi.stable.report.update.ReportVersion;
import com.fr.bi.stable.report.update.operation.ReportUpdateOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kary on 2017/2/4.
 */
public class VersionNodeFactoryTestTool {
    public static ReportConfVersionNode createNode0() {
        List<ReportUpdateOperation> operations=new ArrayList<>();
        ReportUpdateOperation operation = new ReportUpdateOperationATest();
        operations.add(operation);
        return new ReportConfVersionNode(new ReportVersion("0.0",0D), operations);
    }

    public static ReportConfVersionNode createNode1() {
        List<ReportUpdateOperation> operations=new ArrayList<>();
        ReportUpdateOperation operation = new ReportUpdateOperationATest();
        operations.add(operation);
        return new ReportConfVersionNode(new ReportVersion("1.0",1D), operations);
    }

    public static ReportConfVersionNode createVersionNode2() {
        List<ReportUpdateOperation> operations=new ArrayList<>();
        ReportUpdateOperation operation = new ReportUpdateOperationBTest();
        operations.add(operation);
        return new ReportConfVersionNode(new ReportVersion("2.0",2D), operations);
    }

    public static ReportConfVersionNode createVersionNode3() {
        List<ReportUpdateOperation> operations=new ArrayList<>();
        ReportUpdateOperation operation = new ReportUpdateOperationCTest();
        operations.add(operation);
        return new ReportConfVersionNode(new ReportVersion("3.0",3D), operations);
    }
}