package com.fr.bi.stable.report.update.tool;

import com.fr.bi.stable.report.update.ReportConfVersionNode;
import com.fr.bi.stable.report.update.ReportVersion;
import com.fr.bi.stable.report.update.operation.ReportSettingsUpdateOperation;

/**
 * Created by Kary on 2017/2/4.
 */
public class VersionNodeFactoryTestTool {
    public static ReportConfVersionNode createNode0() {
        ReportSettingsUpdateOperation operation = new ReportUpdateOperationATest();
        return new ReportConfVersionNode(new ReportVersion("0.0",0D), operation);
    }

    public static ReportConfVersionNode createNode1() {
        ReportSettingsUpdateOperation operation = new ReportUpdateOperationATest();
        return new ReportConfVersionNode(new ReportVersion("1.0",1D), operation);
    }

    public static ReportConfVersionNode createVersionNode2() {
        ReportSettingsUpdateOperation operation = new ReportUpdateOperationBTest();
        return new ReportConfVersionNode(new ReportVersion("2.0",2D), operation);
    }

    public static ReportConfVersionNode createVersionNode3() {
        ReportSettingsUpdateOperation operation = new ReportUpdateOperationCTest();
        return new ReportConfVersionNode(new ReportVersion("3.0",3D), operation);
    }
}