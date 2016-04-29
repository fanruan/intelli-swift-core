package com.fr.bi.field.target.calculator.sum;

import com.fr.bi.field.target.key.sum.MaxKey;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BICrossNode;
import com.fr.bi.stable.report.result.BINode;
import com.fr.bi.stable.report.result.BITargetKey;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public class MaxCalculator extends AbstractSummaryCalculator {
    private static final long serialVersionUID = 2190789603567558987L;

    public MaxCalculator(BISummaryTarget target) {
        super(target);
    }

    /**
     * 创建sum值
     *
     * @param gvi 索引
     * @param ti  索引
     * @return double值
     */
    @Override
    public double createSumValue(GroupValueIndex gvi, ICubeTableService ti) {
        return ti.getMAXValue(gvi, target.createKey(target.getStatisticElement()));
    }

    /**
     * 计算子节点
     *
     * @param key 汇总的key 计算指标有区别
     * @param c   集合
     * @return double值
     */
    @Override
    public <T extends BINode> Double calculateChildNodes(TargetGettingKey key, Collection<T> c) {
        return calculateNodes(key, c);
    }

    /**
     * 计算子节点
     *
     * @param key 汇总的key 计算指标有区别
     * @param c   集合
     * @return double值
     */
    @Override
    public <T extends BICrossNode> Double calculateChildNodesOnce(TargetGettingKey key, Collection<T> c) {
        Iterator<T> iter = c.iterator();
        double v = 0;
        boolean firstValue = true;
        while (iter.hasNext()) {
            BICrossNode n = iter.next();
            Number number = n.getSummaryValue(key);
            if (number != null) {
                double value = number.doubleValue();
                if (firstValue) {
                    firstValue = false;
                    v = value;
                } else {
                    v = Math.max(v, value);
                }
            }
        }
        return firstValue ? null : new Double(v);
    }

    /**
     * 计算子节点
     *
     * @param c 集合
     * @return double值
     */
    @Override
    public Double calculateChildNodes(Collection<BINode> c) {
        return calculateNodes(this, c);
    }

    @Override
    public BITargetKey createTargetKey() {
        return new MaxKey(target.createColumnKey(), target.getTargetFilter());
    }

    private <T extends BINode> Double calculateNodes(Object key, Collection<T> c) {
        Iterator<T> iter = c.iterator();
        double v = 0;
        boolean firstValue = true;
        while (iter.hasNext()) {
            BINode n = (BINode) iter.next();
            Number number = (Number) n.getSummaryValue(key);
            if (number != null) {
                double value = number.doubleValue();
                if (firstValue) {
                    firstValue = false;
                    v = value;
                } else {
                    v = Math.max(v, value);
                }
            } else {
                //最下面的child可能会有值
                int len = n.getChildLength();
                if (len > 0) {
                    Double result = calculateNodes(key, n.getChilds());
                    if (result != null) {
                        if (firstValue) {
                            firstValue = false;
                            v = result;
                        } else {
                            v = Math.max(v, result);
                        }
                    }
                }
            }
        }
        return firstValue ? null : new Double(v);
    }
}