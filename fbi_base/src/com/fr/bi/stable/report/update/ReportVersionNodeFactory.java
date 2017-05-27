package com.fr.bi.stable.report.update;

import com.fr.bi.stable.report.update.operation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kary on 2017/2/4.
 */
public class ReportVersionNodeFactory {
    public static List<ReportConfVersionNode> createVersionNodes() throws Exception {
        List<ReportConfVersionNode> versionNodes = new ArrayList<ReportConfVersionNode>();
        versionNodes.add(createVersionNodeFor40());
        versionNodes.add(createVersionNodeFor402());
        Collections.sort(versionNodes);
        return versionNodes;
    }

    private static ReportConfVersionNode createVersionNodeFor40() {
        ArrayList<ReportUpdateOperation> operations = new ArrayList<ReportUpdateOperation>();
        operations.add(new ReportNullOperation());
        return new ReportConfVersionNode(ReportVersionEnum.VERSION_4_0, operations);
    }

    private static ReportConfVersionNode createVersionNodeFor402() {
        ArrayList<ReportUpdateOperation> operations = new ArrayList<ReportUpdateOperation>();
        operations.add(new ReportCamelOperation());
        return new ReportConfVersionNode(ReportVersionEnum.VERSION_4_0_2, operations);
    }
}
