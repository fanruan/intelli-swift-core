package com.fr.bi.stable.utils.algorithem;

import com.fr.bi.fs.BIReportNode;
import com.fr.general.ComparatorUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Connery on 2015/12/4.
 */
public class BISortUtils {
    /**
     * 按照修改时间倒序排列 Noder
     *
     * @param nodeList node节点list
     */
    public static void sortByModifyTime(List<BIReportNode> nodeList) {
        Collections.sort(nodeList, new Comparator<BIReportNode>() {
            @Override
            public int compare(BIReportNode o1, BIReportNode o2) {
                return ComparatorUtils.compare(o2.getLastModifyTime(), o1.getLastModifyTime());
            }
        });
    }

}