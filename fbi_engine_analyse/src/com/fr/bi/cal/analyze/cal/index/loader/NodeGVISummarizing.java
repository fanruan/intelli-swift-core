package com.fr.bi.cal.analyze.cal.index.loader;

import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.LightNode;

/**
 * Created by 小灰灰 on 2017/2/20.
 */
public class NodeGVISummarizing extends NodeSummarizing {

    protected void sum(LightNode node) {
        if (node.getChildLength() == 0) {
            return;
        }
        resetSummaryValue(node);
        for (int i = 0; i < node.getChildLength(); i++) {
            LightNode child = node.getChild(i);
            sum(child);
            for (TargetGettingKey key : targetGettingKeys) {
                GroupValueIndex value = node.getTargetIndexValueMap().get(key);
                if (value == null) {
                    node.getTargetIndexValueMap().put(key, child.getTargetIndexValueMap().get(key));
                } else {
                    GroupValueIndex childValue = child.getTargetIndexValueMap().get(key);
                    if (childValue != null) {
                        node.getTargetIndexValueMap().put(key, GVIUtils.OR(childValue, value));
                    }
                }

            }
        }
    }

    protected void resetSummaryValue(LightNode node) {
        for (TargetGettingKey key : targetGettingKeys) {
            node.getTargetIndexValueMap().put(key, null);
        }
    }
}
