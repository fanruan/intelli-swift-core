package com.fr.bi.field.target.calculator.sum;

import com.fr.bi.field.target.key.sum.StringAppendKey;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BICrossNode;
import com.fr.bi.stable.report.result.BINode;
import com.fr.bi.stable.report.result.BITargetKey;

import java.util.Collection;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public class StringAppendCalculator extends AbstractSummaryCalculator {
    public StringAppendCalculator(BISummaryTarget target) {
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
        return 0;
    }

    /**
     * 计算集合C的汇总值
     *
     * @param key 汇总的key 计算指标有区别
     * @param c   集合
     * @return 结果
     */
    @Override
    public <T extends BINode> Double calculateChildNodes(TargetGettingKey key, Collection<T> c) {
        return null;
    }

    /**
     * 计算一次集合C的汇总值
     *
     * @param key 汇总的key 计算指标有区别
     * @param c   集合
     * @return 结果
     */
    @Override
    public <T extends BICrossNode> Double calculateChildNodesOnce(TargetGettingKey key, Collection<T> c) {
        return null;
    }

    /**
     * 计算集合C的汇总值
     * key 为自己 计算指标有区别
     *
     * @param c 集合
     * @return 结果
     */
    @Override
    public Double calculateChildNodes(Collection<BINode> c) {
        return null;
    }

    @Override
    public BITargetKey createTargetKey() {
        return new StringAppendKey(target.createColumnKey(), target.getTargetFilter());
    }
}