package com.fr.bi.field.target.calculator.cal;

import com.fr.bi.field.target.key.cal.BIFormulaCalculatorTargetKey;
import com.fr.bi.field.target.target.cal.BICalculateTarget;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BICrossNode;
import com.fr.bi.stable.report.result.BITargetKey;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.bi.stable.utils.BIFormularUtils;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;

import java.util.*;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public class FormulaCalculator extends CalCalculator {
    private static final long serialVersionUID = 3296274167185012491L;
    private static Calculator c = Calculator.createCalculator();
    private String expression = StringUtils.EMPTY;

    public FormulaCalculator() {
    }

    public FormulaCalculator(BICalculateTarget target,String expression) {
        super(target);
        this.expression = expression;
    }

    /**
     * field准备好了
     *
     * @param targetSet 目标set
     * @return true或fasle
     */
    @Override
    public boolean isAllFieldsReady(Set<TargetGettingKey> targetSet) {
        Iterator<String> it = BIFormularUtils.createColumnIndexMap(expression).values().iterator();
        while (it.hasNext()){
            Object key = targetMap.get(it.next());
            if (key == null) {
                return false;
            }
            boolean v = targetSet.contains(key);
            if (!v) {
                return false;
            }
        }
        return true;
    }


    /**
     * 计算
     *
     * @param node node节点
     */
    @Override
    public void calCalculateTarget(LightNode node) {
        String formula = "=" + expression;
        try {
            Object value = BIFormularUtils.getCalculatorValue(c, formula, BICollectionUtils.mergeMapByKeyMapValue(targetMap, node.getSummaryValueMap()));
            //抛错就是没有值啦
            node.setSummaryValue(createTargetGettingKey(), value);
        } catch (Throwable e) {
        }
        for (int i = 0, len = node.getChildLength(); i < len; i++) {
            calCalculateTarget(node.getChild(i));
        }
    }

    /**
     * 计算
     *
     * @param node 节点
     * @param key  关键字
     */
    @Override
    public void calCalculateTarget(BICrossNode node, TargetGettingKey key) {
        String formula = "=" + expression;
        try {
            Object value = BIFormularUtils.getCalculatorValue(c, formula, BICollectionUtils.mergeMapByKeyMapValue(targetMap, node.getSummaryValue()));
            //抛错就是没有值啦
            node.setSummaryValue(createTargetGettingKey(), value);
        } catch (Throwable e) {
        }
        for (int i = 0, len = node.getLeftChildLength(); i < len; i++) {
            calCalculateTarget(node.getLeftChild(i), key);
        }
    }


    /**
     * 计算集合C的汇总值
     *
     * @param targetGettingkey 汇总的key 计算指标有区别
     * @param collection       集合
     * @return 计算集合C的汇总值
     */
    @Override
    public Double calculateChildNodes(TargetGettingKey targetGettingkey, Collection collection) {
        Object value = null;
        String formular = "=" + expression;
        Iterator<Map.Entry<String, String>> it = BIFormularUtils.createColumnIndexMap(expression).entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, String> entry = it.next();
            TargetCalculator cal = (TargetCalculator) targetMap.get(entry.getValue());
            Double v = new Double(0);
            if (cal != null) {
                v = cal.calculateChildNodes(targetGettingkey, collection);
            }
            c.set(entry.getKey(), v);

        }
        try {
            value = c.evalValue(formular);
        } catch (Throwable e) {
        }
        if (value instanceof Number) {
            return new Double(((Number) value).doubleValue());
        } else {
            return null;
        }
    }

    /**
     * 计算一次集合C的汇总值
     *
     * @param targetGettingkey 汇总的key 计算指标有区别
     * @param collection       集合
     * @return 结果
     */
    @Override
    public Double calculateChildNodesOnce(TargetGettingKey targetGettingkey, Collection collection) {
        return calculateChildNodes(targetGettingkey, collection);
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
        Object value = null;
        String formular = "=" + expression;
        Iterator<Map.Entry<String, String>> it = BIFormularUtils.createColumnIndexMap(expression).entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, String> entry = it.next();
            TargetCalculator cal = (TargetCalculator) targetMap.get(entry.getValue());
            Double v = new Double(0);
            if (cal != null) {
                v = cal.calculateChildNodes(collection);
            }
            c.set(entry.getKey(), v);

        }

        try {
            value = c.evalValue(formular);
        } catch (Throwable e) {
        }
        if (value instanceof Number) {
            return new Double(((Number) value).doubleValue());
        } else {
            return null;
        }

    }


    @Override
    public BITargetKey createTargetKey() {
        return new BIFormulaCalculatorTargetKey(expression, targetName, targetMap);
    }

}