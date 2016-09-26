package com.fr.bi.tool;

import com.fr.base.BaseUtils;
import com.fr.base.BaseXMLUtils;
import com.fr.bi.fs.BIDesignSetting;
import com.fr.bi.fs.BIFileRepository;
import com.fr.bi.fs.BIReportNode;
import com.fr.json.JSONObject;
import com.fr.stable.CodeUtils;

import java.io.File;

/**
 * Created by Young's on 2016/9/23.
 */
public class BIReadReportUtils {
    public static JSONObject getBIReportNodeJSON(BIReportNode node) throws Exception {
        String nodePath = CodeUtils.decodeText(node.getPath());
        /**
         * 兼容以前的绝对路径
         */
        String dirString = BIFileRepository.getInstance().getBIDirName(node.getUserId());
        //屏蔽系统文件路径问题
        int nodeLength = nodePath.lastIndexOf("\\" + dirString + "\\");
        nodeLength = nodeLength == -1 ? nodePath.lastIndexOf("/" + dirString + "/") : nodeLength;
        if (nodeLength > -1) {
            // +2 左右两个路径分隔符
            nodePath = nodePath.substring(nodeLength + dirString.length() + 2);
        }
        if (nodePath.startsWith("\\") || nodePath.startsWith("/")) {
            nodePath = nodePath.substring(1);
        }
        File parent = BIFileRepository.getInstance().getBIDirFile(node.getUserId());
        File file = new File(parent, nodePath);
        if (!file.exists()) {
            throw new RuntimeException("can't find file:" + node.getPath() + "! might be delete or move!");
        }
        BIDesignSetting setting = (BIDesignSetting) BaseXMLUtils.readXMLFile(
                BaseUtils.readResource(file.getAbsolutePath()),
                new BIDesignSetting());
        return setting.getReportSetting();
    }
}
