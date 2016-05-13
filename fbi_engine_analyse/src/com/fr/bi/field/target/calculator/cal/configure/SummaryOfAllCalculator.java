package com.fr.bi.field.target.calculator.cal.configure;

import com.fr.base.FRContext;
import com.fr.bi.field.target.target.cal.target.configure.BIConfiguredCalculateTarget;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BICrossNode;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.utils.CubeBaseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public abstract class SummaryOfAllCalculator extends AbstractConfigureCalulator {
    private static final long serialVersionUID = 4448457069572400146L;

    public SummaryOfAllCalculator(BIConfiguredCalculateTarget target, String target_id, int start_group) {
        super(target, target_id, start_group);
    }

    @Override
    public void calCalculateTarget(LightNode node) {
        Object key = getCalKey();
        if (key == null) {
            return;
        }
        LightNode tempNode = node;
        for (int i = 0; i < start_group; i++) {
            if (tempNode.getFirstChild() == null) {
                break;
            }
            tempNode = tempNode.getFirstChild();
        }
        List nodeList = new ArrayList();
        LightNode cursor_node = tempNode;
        while (cursor_node != null) {
            nodeList.add(createNodeDealWith(cursor_node));
            cursor_node = cursor_node.getSibling();
        }
        try {
            CubeBaseUtils.invokeCalculatorThreads(nodeList);
        } catch (InterruptedException e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
    }

    public abstract Callable createNodeDealWith(LightNode node);

    @Override
    public void calCalculateTarget(BICrossNode node, TargetGettingKey key1) {
        Object key = getCalKey();
        if (key == null) {
            return;
        }
        BICrossNode tempNode = node;
        for (int i = 0; i < start_group; i++) {
            if (tempNode.getLeftFirstChild() == null) {
                break;
            }
            tempNode = tempNode.getLeftFirstChild();
        }
        List nodeList = new ArrayList();
        BICrossNode cursor_node = tempNode;
        while (cursor_node != null) {
            nodeList.add(createNodeDealWith(cursor_node));
            cursor_node = cursor_node.getBottomSibling();
        }
        try {
            CubeBaseUtils.invokeCalculatorThreads(nodeList);
        } catch (InterruptedException e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
    }

    public abstract Callable createNodeDealWith(BICrossNode node);

    /**
     * 在第几个维度上汇总，暂时写死为1
     *
     * @return
     */
    protected int getCalDeep(Object rank_node) {
        int deep = 0;
        if (rank_node instanceof LightNode) {
            LightNode node = (LightNode)rank_node;
            while (node.getFirstChild() != null) {
                deep++;
                node = node.getFirstChild();
            }
        } else if (rank_node instanceof BICrossNode) {
            BICrossNode node = (BICrossNode) rank_node;
            while (node.getLeftFirstChild() != null) {
                deep++;
                node = node.getLeftFirstChild();
            }
        }else{
            return 1;
        }

        return deep;
    }

    protected LightNode getFirstCalNode(LightNode rank_node) {
        LightNode temp_node = rank_node;
        if (temp_node.getFirstChild() != null) {
            temp_node = temp_node.getFirstChild();
        }
        return temp_node;
    }

    protected BICrossNode getFirstCalCrossNode(BICrossNode rank_node) {
        BICrossNode temp_node = rank_node;
        if (temp_node.getLeftFirstChild() != null) {
            temp_node = temp_node.getLeftFirstChild();
        }
        return temp_node;
    }

    protected LightNode getDeepCalNode(LightNode rank_node) {
        LightNode temp_node = rank_node;
        for (int i = 0; i < getCalDeep(rank_node); i++) {
            if (temp_node.getFirstChild() != null) {
                temp_node = temp_node.getFirstChild();
            }
        }
        return temp_node;
    }
}