package com.fr.bi.stable.report.update.tool;

import com.fr.bi.stable.report.update.ReportConfVersionNode;
import com.fr.bi.stable.report.update.ReportVersionEnum;
import com.fr.bi.stable.report.update.operation.ReportUpdateOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kary on 2017/2/4.
 */
public class VersionNodeFactoryTestTool {
    public static ReportConfVersionNode createNode0() {
        List<ReportUpdateOperation> operations = new ArrayList<ReportUpdateOperation>();
        ReportUpdateOperation operation = new ReportUpdateOperationATest();
        operations.add(operation);
        return new ReportConfVersionNode(ReportVersionEnum.V0, operations);
    }

    public static ReportConfVersionNode createNode1() {
        List<ReportUpdateOperation> operations = new ArrayList<ReportUpdateOperation>();
        ReportUpdateOperation operation = new ReportUpdateOperationATest();
        operations.add(operation);
        return new ReportConfVersionNode(ReportVersionEnum.V1, operations);
    }

    public static ReportConfVersionNode createVersionNode2() {
        List<ReportUpdateOperation> operations = new ArrayList<ReportUpdateOperation>();
        ReportUpdateOperation operation = new ReportUpdateOperationBTest();
        operations.add(operation);
        return new ReportConfVersionNode(ReportVersionEnum.V2, operations);
    }

    public static ReportConfVersionNode createVersionNode3() {
        List<ReportUpdateOperation> operations = new ArrayList<ReportUpdateOperation>();
        ReportUpdateOperation operation = new ReportUpdateOperationCTest();
        operations.add(operation);
        return new ReportConfVersionNode(ReportVersionEnum.V3, operations);
    }
}