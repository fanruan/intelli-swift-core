package com.fr.bi.field.target.calculator.cal;

import com.fr.bi.field.target.target.cal.BICalculateTarget;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BICrossNode;
import com.fr.bi.stable.report.result.BINode;
import com.fr.bi.stable.utils.BIFormularUtils;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public class FormulaCalculator extends CalCalculator {
    private static final long serialVersionUID = 3296274167185012491L;
    private Calculator c = Calculator.createCalculator();
    private String expression = StringUtils.EMPTY;
    private String incrementParaFormula;
    //用到的指标
    private Map<String, TargetGettingKey> paraTargetMap;
    public FormulaCalculator() {
    }

    public FormulaCalculator(BICalculateTarget target, String expression) {
        super(target);
        this.expression = expression;
        //构造的时候就转化成自增长id的公式，省得每次计算结果都算一下
        incrementParaFormula = "=" + BIFormularUtils.getIncrementParaFormula(expression);
        initParaTargetMap();
    }

    //构建的时候把全部指标都塞过来了，造成没必要的遍历，取下用到的指标，转化成公式的$1->TargetGettingKey的map
    private void initParaTargetMap() {
        Map<String, String> map = BIFormularUtils.createColumnIndexMap(expression);
        paraTargetMap = new HashMap<String, TargetGettingKey>();
        if (targetMap != null){
            for (Map.Entry<String, String> entry :  BIFormularUtils.createColumnIndexMap(expression).entrySet()){
                if (targetMap.containsKey(entry.getValue())){
                    paraTargetMap.put(entry.getKey(), targetMap.get(entry.getValue()).createTargetGettingKey());
                }
            }
        }
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
        try {
            Object value = BIFormularUtils.getCalculatorValue(c, incrementParaFormula, paraTargetMap, node.getSummaryValue());
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
        try {
            Object value = BIFormularUtils.getCalculatorValue(c, incrementParaFormula, paraTargetMap, node.getSummaryValue());
            //抛错就是没有值啦
            node.setSummaryValue(createTargetGettingKey(),((Number) value).doubleValue());
        } catch (Throwable e) {
        }
        for (int i = 0, len = node.getLeftChildLength(); i < len; i++) {
            calCalculateTarget(node.getLeftChild(i), key);
        }
    }
}