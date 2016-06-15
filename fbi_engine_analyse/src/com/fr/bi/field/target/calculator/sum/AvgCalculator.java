package com.fr.bi.field.target.calculator.sum;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.field.target.calculator.cal.FormulaCalculator;
import com.fr.bi.field.target.key.sum.AvgKey;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BICrossNode;
import com.fr.bi.stable.report.result.BITargetKey;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.report.result.TargetCalculator;

import java.util.Collection;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public class AvgCalculator extends FormulaCalculator {

    private static final long serialVersionUID = -2976871776170710571L;
    private SumCalculator sum;
    private CountCalculator count;

    public AvgCalculator(BISummaryTarget target) {
        this.sum = new SumCalculator(target);
        this.count = new CountCalculator(target, target.getStatisticElement().getFieldName());
    }

    @Override
    public TargetGettingKey createTargetGettingKey() {
        return new TargetGettingKey(createTargetKey(), getName());
    }

    /**
     * 创建计算指标
     *
     * @return 指标数组
     */
    @Override
    public TargetCalculator[] createTargetCalculators() {
        return new TargetCalculator[]{sum, count};
    }

    @Override
    public BITargetKey createTargetKey() {
        return new AvgKey(sum.target.createColumnKey(), sum.target.getTargetFilter());
    }

    /**
     * 计算
     *
     * @param node node节点
     */
    @Override
    public void calCalculateTarget(LightNode node) {
        Double sum = (Double) node.getSummaryValue(new TargetGettingKey(this.sum.createTargetKey(), this.sum.getName()));
        Double count = (Double) node.getSummaryValue(new TargetGettingKey(this.count.createTargetKey(), this.count.getName()));
        Object value = null;
        if (sum != null && count != null && count.doubleValue() != 0) {
            double s = sum.doubleValue();
            double c = count.doubleValue();
            value = new Double(s / c);
            node.setSummaryValue(createTargetGettingKey(), value);
        }
        for (int i = 0, len = node.getChildLength(); i < len; i++) {
            calCalculateTarget(node.getChild(i));
        }
    }

    /**
     * 创建tablekey
     *
     * @return 创建的tablekey
     */
    @Override
    public BusinessTable createTableKey() {
        return sum.createTableKey();
    }

    @Override
    public String getName() {
        return sum.getName();
    }

    /**
     * 计算集合C的汇总值
     *
     * @param key        汇总的key 计算指标有区别
     * @param collection 集合
     * @return 计算集合C的汇总值
     */
    @Override
    public Double calculateChildNodes(TargetGettingKey key, Collection collection) {
        Double sum = this.sum.calculateChildNodes(new TargetGettingKey(this.sum.createTargetKey(), key.getTargetName()), collection);
        Double count = this.count.calculateChildNodes(new TargetGettingKey(this.count.createTargetKey(), key.getTargetName()), collection);
        //虽然count != null还是要check一下
        if (sum == null || count == null || count.doubleValue() == 0) {
            return null;
        }
        //count 为 0时 sum 应该是null值， so count肯定不为0
        return new Double(sum.doubleValue() / count.doubleValue());
    }

    /**
     * 计算一次集合C的汇总值
     *
     * @param key        汇总的key 计算指标有区别
     * @param collection 集合
     * @return 结果
     */
    @Override
    public Double calculateChildNodesOnce(TargetGettingKey key, Collection collection) {
        Double sum = this.sum.calculateChildNodesOnce(new TargetGettingKey(this.sum.createTargetKey(), key.getTargetName()), collection);
        Double count = this.count.calculateChildNodesOnce(new TargetGettingKey(this.count.createTargetKey(), key.getTargetName()), collection);
        //虽然count != null还是要check一下
        if (sum == null || count == null || count.doubleValue() == 0) {
            return null;
        }
        //count 为 0时 sum 应该是null值， so count肯定不为0
        return new Double(sum.doubleValue() / count.doubleValue());
    }

    /**
     * 计算集合C的汇总值
     * key 为自己 计算指标有区别
     *
     * @param collection 集合
     * @return 结果
     */
    @Override
    public Double calculateChildNodes(Collection collection) {
        Double sum = this.sum.calculateChildNodes(collection);
        Double count = this.count.calculateChildNodes(collection);
        //虽然count != null还是要check一下
        if (sum == null || count == null || count.doubleValue() == 0) {
            return null;
        }
        //count 为 0时 sum 应该是null值， so count肯定不为0
        return new Double(sum.doubleValue() / count.doubleValue());
    }

    /**
     * 计算
     *
     * @param node 节点
     * @param key  关键字
     */
    @Override
    public void calCalculateTarget(BICrossNode node, TargetGettingKey key) {
        Number sum = node.getSummaryValue(new TargetGettingKey(this.sum.createTargetKey(), key.getTargetName()));
        Number count = node.getSummaryValue(new TargetGettingKey(this.count.createTargetKey(), key.getTargetName()));
        //虽然count != null还是要check一下
        if (sum == null || count == null || count.doubleValue() == 0) {
            node.setSummaryValue(key, 0);
        } else {
            node.setSummaryValue(key, new Double(sum.doubleValue() / count.doubleValue()));
        }

        for (int i = 0, len = node.getLeftChildLength(); i < len; i++) {
            calCalculateTarget(node.getLeftChild(i), key);
        }
    }
}