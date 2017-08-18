package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndexOrHelper;

/**
 * Created by 小灰灰 on 2017/2/20.
 */
public class NodeGVISummarizing extends NodeSummarizing {

    public NodeGVISummarizing(MetricMergeResult node, TargetAndKey[] targetAndKeys) {
        super(node, targetAndKeys);
    }

    protected void sum(MetricMergeResult node) {
        if (node.getChildLength() == 0) {
            return;
        }
        resetSummaryValue(node);
        for (int i = 0; i < node.getChildLength(); i++) {
            MetricMergeResult child = (MetricMergeResult) node.getChild(i);
            sum(child);
        }
        //这边or运算再循环一次，减少运算过程中内存中索引对象的数量
        GroupValueIndexOrHelper[] helpers = new GroupValueIndexOrHelper[node.getGvis().length];
        for (int i = 0; i < helpers.length; i++) {
            helpers[i] = new GroupValueIndexOrHelper();
        }
        GroupValueIndex[] gvis = new GroupValueIndex[helpers.length];
        for (int i = 0; i < node.getChildLength(); i++) {
            MetricMergeResult child = (MetricMergeResult) node.getChild(i);
            for (int j = 0; j < helpers.length; j++) {
                helpers[j].add(child.getGvis()[j]);
            }
        }
        for (int i = 0; i < helpers.length; i++) {
            gvis[i] = helpers[i].compute();
        }
        node.setGvis(gvis);
    }

}
