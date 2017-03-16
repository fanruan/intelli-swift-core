package com.fr.bi.stable.report.update.tool;

import com.fr.bi.stable.report.update.ReportSettingUpdateManager;
import com.fr.bi.stable.report.update.ReportConfVersionNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kary on 2017/2/4.
 */
public class ReportConfUpdateManager4Test extends ReportSettingUpdateManager {
    public ReportConfUpdateManager4Test() throws Exception {
        super();
        List list = generateNodeList();
        reSetNodeList(list);
    }

    private List generateNodeList() {
        ReportConfVersionNode node0 = VersionNodeFactoryTestTool.createNode0();
        ReportConfVersionNode node1 = VersionNodeFactoryTestTool.createNode1();
        ReportConfVersionNode node2 = VersionNodeFactoryTestTool.createVersionNode2();
        ReportConfVersionNode node3 = VersionNodeFactoryTestTool.createVersionNode3();
        List nodeList = new ArrayList();
        nodeList.add(node0);
        nodeList.add(node1);
        nodeList.add(node2);
        nodeList.add(node3);
        return nodeList;
    }

}