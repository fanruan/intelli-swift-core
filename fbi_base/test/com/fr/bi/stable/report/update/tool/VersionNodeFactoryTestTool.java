package com.fr.bi.stable.report.update.tool;

import com.fr.bi.stable.report.update.ReportConfVersionNode;
import com.fr.bi.stable.report.update.operation.ReportSettingsUpdateOperation;

/**
 * Created by Kary on 2017/2/4.
 */
public class VersionNodeFactoryTestTool {
    public static ReportConfVersionNode createNode0() {
        ReportSettingsUpdateOperation operation = new ReportUpdateOperationATest();
        return new ReportConfVersionNode(0, operation);
    }

    public static ReportConfVersionNode createNode1() {
        ReportSettingsUpdateOperation operation = new ReportUpdateOperationATest();
        return new ReportConfVersionNode(1, operation);
    }

    public static ReportConfVersionNode createVersionNode2() {
        ReportSettingsUpdateOperation operation = new ReportUpdateOperationBTest();
        return new ReportConfVersionNode(2, operation);
    }

    public static ReportConfVersionNode createVersionNode3() {
        ReportSettingsUpdateOperation operation = new ReportUpdateOperationCTest();
        return new ReportConfVersionNode(3, operation);
    }
}