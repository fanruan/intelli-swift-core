package com.fr.bi.stable.report.update;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.report.update.operation.ReportSettingRenameOperation;
import com.fr.bi.stable.report.update.operation.ReportNullOperation;
import com.fr.bi.stable.report.update.operation.ReportSettingsUpdateOperation;

/**
 * Created by Kary on 2017/2/4.
 */
public class ReportVersionNodeFactory {
    public static ReportConfVersionNode createVersionNode(double version) throws Exception {
        if (BIReportConstant.HISTORY_VERSION.VERSION_4_0 == version) {
            return createVersionNodeFor40();
        }
        if (BIReportConstant.HISTORY_VERSION.VERSION_4_1 == version) {
            return createVersionNodeFor41();
        }
        throw new Exception();
    }

    private static ReportConfVersionNode createVersionNodeFor40() {
        ReportSettingsUpdateOperation operation = new ReportSettingRenameOperation();
        return new ReportConfVersionNode(BIReportConstant.HISTORY_VERSION.VERSION_4_0, operation);
    }

    private static ReportConfVersionNode createVersionNodeFor41() {
        ReportSettingsUpdateOperation operation = new ReportNullOperation();
        return new ReportConfVersionNode(BIReportConstant.HISTORY_VERSION.VERSION_4_1, operation);
    }
}
