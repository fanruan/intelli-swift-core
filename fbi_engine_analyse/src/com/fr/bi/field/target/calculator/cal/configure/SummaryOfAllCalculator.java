package com.fr.bi.field.target.calculator.cal.configure;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.report.result.BIXLeftNode;
import com.fr.bi.field.target.target.cal.target.configure.BIConfiguredCalculateTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.key.XTargetGettingKey;
import com.fr.bi.report.result.BINode;
import com.fr.bi.stable.utils.CubeBaseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public abstract class SummaryOfAllCalculator extends AbstractConfigureCalculator {
    private static final long serialVersionUID = 4448457069572400146L;

    public SummaryOfAllCalculator(BIConfiguredCalculateTarget target, TargetGettingKey calTargetKey, int start_group) {
        super(target, calTargetKey, start_group);
    }

    @Override
    public void calCalculateTarget(BINode node) {
        cal(node, null);
    }

    private void cal(BINode node, XTargetGettingKey key) {
        if (calTargetKey == null) {
            return;
        }
        //获得当前node的纬度数
        int deep = getCalDeep(node);
//         deep = 2;
        BINode tempNode = node;
        //从第几个纬度开始计算
        int calDeep = start_group == 0 ? 0 : deep - start_group;
        /**
         *
         */

        for (int i = 0; i < calDeep; i++) {
            if (tempNode.getFirstChild() == null) {
                break;
            }
            tempNode = tempNode.getFirstChild();
        }
        /**
         * Connery：功能逻辑是要计算最后维度（start_group所在维度做汇总），所以深度
         * 必须大于start_group，这样才有value做汇总。
         * |D1|D2|D3|   v| 这样的维度，node的深度必须到达D3才能有v的值。
         *
         * 加1是因为node默认有一个空root
         *
         */
        if (calDeep > 0) {
            if (node.getDeep() > node.getFrameDeep()) {
                tempNode = getCalculatedRootNode(node);
            } else {
                return;
            }
        }

        List nodeList = new ArrayList();
        BINode cursor_node = tempNode;
        while (cursor_node != null) {
            if (shouldCalculate(cursor_node)) {
                nodeList.add(createNodeDealWith(cursor_node, key));
            }
            cursor_node = cursor_node.getSibling();
        }
        try {
            CubeBaseUtils.invokeCalculatorThreads(nodeList);
        } catch (InterruptedException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public void calCalculateTarget(BIXLeftNode node, XTargetGettingKey key) {
        cal(node, key);
    }

    public abstract Callable createNodeDealWith(BINode node, XTargetGettingKey key);

    protected BINode getFirstCalNode(BINode rank_node) {
        BINode temp_node = rank_node;
        if (temp_node.getFirstChild() != null) {
            temp_node = temp_node.getFirstChild();
        }
        return temp_node;
    }


    protected BINode getDeepCalNode(BINode rank_node) {
        BINode temp_node = rank_node;
        for (int i = 0; i < getCalDeep(rank_node); i++) {
            if (temp_node.getFirstChild() != null) {
                temp_node = temp_node.getFirstChild();
            }
        }
        return temp_node;
    }


}