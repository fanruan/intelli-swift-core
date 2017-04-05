package com.fr.bi.field.target.calculator.cal;

import com.fr.bi.field.target.target.cal.BICalculateTarget;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BICrossNode;
import com.fr.bi.stable.report.result.BINode;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.bi.stable.utils.BIFormularUtils;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public class FormulaCalculator extends CalCalculator {
    private static final long serialVersionUID = 3296274167185012491L;
    private Calculator c = Calculator.createCalculator();
    private String expression = StringUtils.EMPTY;

    public FormulaCalculator() {
    }

    public FormulaCalculator(BICalculateTarget target, String expression) {
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
        while (it.hasNext()) {
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
    public void calCalculateTarget(BINode node) {
        String formula = "=" + expression;
        try {
            Object value = BIFormularUtils.getCalculatorValue(c, formula, BICollectionUtils.mergeMapByKeyMapValue(targetMap, node.getSummaryValue()));
            //抛错就是没有值啦
            //pony 这边都存double吧，汇总在汇总要写cube，类型要统一
            node.setSummaryValue(createTargetGettingKey(), ((Number) value).doubleValue());
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
}