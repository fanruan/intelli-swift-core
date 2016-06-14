package com.fr.bi.field.target.calculator.sum;

import com.fr.bi.field.target.key.sum.CountKey;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.field.target.target.NoneTargetCountTarget;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.key.IndexKey;
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
public class CountCalculator extends AbstractSummaryCalculator {
    public static final CountCalculator NONE_TARGET_COUNT_CAL = new CountCalculator(new NoneTargetCountTarget(), "pony");
    private static final long serialVersionUID = -3263413185870966424L;
    private String countTarget;

    public CountCalculator(BISummaryTarget target, String countTarget) {
        super(target);
        this.countTarget = countTarget;
    }

    /**
     * 记录数没有的时候就是0,不用判断
     * 增加重载
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
     * @param key 汇总key
     * @param c   集合
     * @return double值
     */
    @Override
    public <T extends BICrossNode> Double calculateChildNodesOnce(TargetGettingKey key, Collection<T> c) {
        if (countTarget != null) {
            return null;
        }
        Iterator<T> iter = c.iterator();
        double v = 0;
        while (iter.hasNext()) {
            BICrossNode n = iter.next();
            Number number = n.getSummaryValue(key);
            if (number != null) {
                v += number.doubleValue();
            }
        }
        return new Double(v);
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
        return new CountKey(target.createColumnKey(), target.getTargetFilter(), countTarget == null ? null : new IndexKey(countTarget));
    }

    private <T extends BINode> Double calculateNodes(Object key, Collection<T> c) {
        if (countTarget != null) {
            return null;
        }
        Iterator<T> iter = c.iterator();
        double v = 0;
        while (iter.hasNext()) {
            BINode n = (BINode) iter.next();
            Number number = (Number) n.getSummaryValue(key);
            if (number != null) {
                v += number.doubleValue();
            } else {
                //最下面的child可能会有值
                int len = n.getChildLength();
                if (len > 0) {
                    Double result = calculateNodes(key, n.getChilds());
                    if (result != null) {
                        v += result.doubleValue();
                    }
                }
            }
        }
        return new Double(v);
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
        return gvi == null ? 0 : gvi.getRowsCountWithData();
    }
}